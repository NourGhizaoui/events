package com.example.plevent.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class WebSecurityConfig {

    @Autowired
    private AuthTokenFilter authTokenFilter; // JWT

    @Autowired
    private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    
    @Autowired
    private AuthEntryPointJwt unauthorizedHandler; // 401 JSON pour API

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // AuthenticationManager pour login
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // SECURITE GLOBALE
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                // --------------- EXCEPTIONS POUR API ---------------
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(unauthorizedHandler)
                )

                // --------------- ROUTES AUTORISÉES ---------------
                .authorizeHttpRequests(auth -> auth
                        // Public
                        .requestMatchers("/auth/**", "/css/**", "/js/**", "/images/**").permitAll()

                        // API ouverte pour LOGIN & REGISTER
                        .requestMatchers("/api/auth/**").permitAll()

                        // API protégée par JWT
                        .requestMatchers("/api/**").authenticated()

                        // Dashboards Thymeleaf protégés par rôles
                        .requestMatchers("/user/**").hasRole("USER")
                        .requestMatchers("/organisator/**").hasRole("ORGANISATOR")
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // Tout le reste nécessite une session valide
                        .anyRequest().authenticated()
                )

                // --------------- FORM LOGIN (Thymeleaf) ---------------
                .formLogin(form -> form
                        .loginPage("/auth/login")
                        .loginProcessingUrl("/auth/login")
                        .successHandler(customAuthenticationSuccessHandler)
                        .failureUrl("/auth/login?error=true")
                        .permitAll()
                )

                // --------------- SESSION STATE (pour Thymeleaf) ---------------
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )

                // --------------- LOGOUT ---------------
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .logoutSuccessUrl("/auth/login?logout=true")
                );

        // --------------- FILTRE JWT UNIQUEMENT POUR API ---------------
        http.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
