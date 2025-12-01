package com.example.plevent.controller;

import com.example.plevent.model.Erole;

import com.example.plevent.model.Role; // Assuming this model exists
import com.example.plevent.model.User; // Assuming this model exists
import com.example.plevent.repository.RoleRepository;
import com.example.plevent.repository.UserRepository;
import com.example.plevent.request.LoginRequest;
import com.example.plevent.request.SignupRequest;
import com.example.plevent.response.JwtResponse;
import com.example.plevent.response.MessageResponse;
import com.example.plevent.security.JwtUtils;
import com.example.plevent.security.RefreshTokenService;
import com.example.plevent.security.UserDetailsImpl;
import com.example.plevent.service.RoleService; // Service dependency
import org.springframework.security.core.Authentication; // 🎯 Required for checking login authority
import org.springframework.security.core.GrantedAuthority; // Required for iterating authorities
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // 🎯 Required for adding data to the view (register form)
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Collection;
import java.util.Optional;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired AuthenticationManager authenticationManager;
    @Autowired UserRepository userRepository;
    @Autowired RoleRepository roleRepository;
    @Autowired PasswordEncoder encoder;
    @Autowired JwtUtils jwtUtils;
    @Autowired RefreshTokenService refreshTokenService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
            .map(item -> item.getAuthority()).collect(Collectors.toList());

        // create refresh token
        var refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        return ResponseEntity.ok(new JwtResponse(
                jwt, 
                userDetails.getId(), 
                userDetails.getUsername(), 
                userDetails.getEmail(), 
                roles
        ));
    }
/*
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(Erole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        roleRepository.findByName(Erole.ROLE_ADMIN).ifPresent(roles::add);
                        break;
                    case "mod":
                        roleRepository.findByName(Erole.ROLE_ORGANISATOR).ifPresent(roles::add);
                        break;
                    default:
                        roleRepository.findByName(Erole.ROLE_USER).ifPresent(roles::add);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
    
    */
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {

        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        boolean isOrganisateur = false;

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(Erole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);

        } else {
            for (String role : strRoles) {
                switch (role) {
                    case "admin":
                        roles.add(roleRepository.findByName(Erole.ROLE_ADMIN)
                            .orElseThrow(() -> new RuntimeException("Error: Role not found")));
                        break;

                    case "organisateur": // 🔥 IMPORTANT : c’est ton ROLE_ORGANISATOR
                        roles.add(roleRepository.findByName(Erole.ROLE_ORGANISATOR)
                            .orElseThrow(() -> new RuntimeException("Error: Role not found")));
                        isOrganisateur = true;
                        break;

                    default:
                        roles.add(roleRepository.findByName(Erole.ROLE_USER)
                            .orElseThrow(() -> new RuntimeException("Error: Role not found")));
                }
            }
        }

        user.setRoles(roles);

        // 🔥 GESTION activation selon rôle
        if (isOrganisateur) {
            user.setActive(false); // ❌ doit attendre validation admin
        } else {
            user.setActive(true);   // ✔️ user normal actif
        }

        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

}