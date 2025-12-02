package com.example.plevent.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        boolean isAdmin = false;
        boolean isOrganisator = false;
        boolean isUser = false;
        boolean isActive = true;

        for (GrantedAuthority auth : authentication.getAuthorities()) {
            String role = auth.getAuthority();
            if (role.equals("ROLE_ADMIN")) isAdmin = true;
            if (role.equals("ROLE_ORGANISATOR")) isOrganisator = true;
            if (role.equals("ROLE_USER")) isUser = true;
        }

        // Vérifier si l'organisator est actif
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetailsImpl user) {
            isActive = user.isEnabled();
        }

        if (isAdmin) {
            response.sendRedirect("/admin/dashboard");
        } else if (isOrganisator) {
            if (isActive) {
                response.sendRedirect("/organisator/dashboard");
            } else {
                response.sendRedirect("/auth/login?inactive");
            }
        } else if (isUser) {
            response.sendRedirect("/user/dashboard");
        } else {
            response.sendRedirect("/auth/login?error");
        }
    }
}
