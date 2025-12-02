package com.example.plevent.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;

@Configuration
@EnableMethodSecurity
public class WebSecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Autowired
    private CustomAuthenticationSuccessHandler successHandler;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(org.springframework.security.config.annotation.web.builders.HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Pages Thymeleaf accessibles à tous
                .requestMatchers("/auth/login", "/auth/signup", "/css/**", "/js/**").permitAll()
                // API REST login/signup accessibles à tous
                .requestMatchers("/api/auth/login", "/api/auth/signup", "/api/auth/refreshtoken").permitAll()
                // Dashboards selon rôle
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/organisator/**").hasRole("ORGANISATOR")
                .requestMatchers("/user/**").hasRole("USER")
                
                // Toutes les autres requêtes nécessitent authentification
                .anyRequest().authenticated()
            )
            // Form login pour Thymeleaf avec successHandler
            .formLogin(form -> form
                .loginPage("/auth/login")
                .successHandler(successHandler)
                .permitAll()
            )
           /* .logout(logout -> logout
                .logoutUrl("/auth/logout")
                .logoutSuccessUrl("/auth/login-form?logout")
                .permitAll()
            )*/;

        // Filtre JWT avant le UsernamePasswordAuthenticationFilter
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
