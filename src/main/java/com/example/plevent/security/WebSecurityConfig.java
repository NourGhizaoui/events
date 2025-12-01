package com.example.plevent.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
/*
@Configuration
@EnableMethodSecurity
public class WebSecurityConfig {

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Autowired
    private AuthTokenFilter authenticationJwtTokenFilter;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.cors().and().csrf().disable()
            .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
            .authorizeHttpRequests()

                // routes publiques
                .requestMatchers(
                    "/auth/**",
                    "/signup",
                    "/login",
                    "/api/auth/**",
                    "/api/roles",
                    "/css/**",
                    "/js/**",
                    "/admin/loginadmin",
                    "/admin/**" // 🔥 très important !
                ).permitAll()

                // zone admin sécurisée
                .requestMatchers("/admin/**").hasRole("ADMIN")

                .anyRequest().authenticated()
                
                
                ;

        http.addFilterBefore(authenticationJwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }


}
*/
@Configuration
@EnableMethodSecurity
public class WebSecurityConfig {

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Autowired
    private AuthTokenFilter authenticationJwtTokenFilter;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth

                // Routes publiques
                .requestMatchers(
                    "/auth/**",
                    "/api/auth/**",
                    "/api/roles",       // ✅ autorisé
                    "/css/**",
                    "/js/**",
                    "/images/**"
                ).permitAll()

                // Page login admin accessible sans être connecté
                .requestMatchers("/admin/login").permitAll()

                // Toute la zone admin est protégée
                .requestMatchers("/admin/**").hasRole("ADMIN")


                .anyRequest().authenticated()
            )

            // Login admin
            .formLogin(form -> form
                .loginPage("/admin/login")               // PAGE
                .loginProcessingUrl("/admin/login")      // ACTION DU FORM
                .defaultSuccessUrl("/admin/dashboard", true)
                .failureUrl("/admin/login?error=true")
                .permitAll()
            )
            
            .formLogin(form -> form
                    .loginPage("/auth/login")
                    .defaultSuccessUrl("/user/dashboard") // page par défaut après login
                    .permitAll()
                )

            // Logout Admin
            .logout(logout -> logout .logoutUrl("/admin/logout") 
            		.logoutSuccessUrl("/login?logout") // redirige après logout 
            		.invalidateHttpSession(true) 
            		.deleteCookies("JSESSIONID") 
            		.permitAll() )

            // JWT = stateless → mais uniquement pour l’API
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))


            // Important : Auth filter JWT avant UsernamePasswordAuthenticationFilter
            .addFilterBefore(authenticationJwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
