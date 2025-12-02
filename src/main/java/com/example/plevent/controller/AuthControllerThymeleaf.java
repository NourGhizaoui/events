package com.example.plevent.controller;

import com.example.plevent.model.*;
import com.example.plevent.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;

@Controller
@RequestMapping("/auth")
public class AuthControllerThymeleaf {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder encoder;

    // ------------------- LOGIN PAGE -------------------
    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            @RequestParam(value = "inactive", required = false) String inactive,
                            Model model) {
        if (error != null) model.addAttribute("error", "Invalid username or password.");
        if (logout != null) model.addAttribute("message", "You have been logged out.");
        if (inactive != null) model.addAttribute("error", "Your account is not yet activated by admin.");
        return "login";
    }

    // ------------------- SIGNUP PAGE -------------------
    @GetMapping("/signup")
    public String signupPage() {
        return "signup";
    }

    // ------------------- SIGNUP FORM -------------------
    @PostMapping("/signup")
    public String registerUser(@RequestParam String username,
                               @RequestParam String email,
                               @RequestParam String password,
                               @RequestParam String role,
                               Model model) {

        if (userRepository.existsByUsername(username)) {
            model.addAttribute("error", "Username is already taken!");
            return "signup";
        }
        if (userRepository.existsByEmail(email)) {
            model.addAttribute("error", "Email is already in use!");
            return "signup";
        }

        User user = new User(username, email, encoder.encode(password));

        Set<Role> roles = new HashSet<>();
        switch (role) {
            case "organisator":
                roles.add(roleRepository.findByName(Erole.ROLE_ORGANISATOR).orElseThrow());
                user.setActive(false); // 🔥 attente admin
                model.addAttribute("message", "Organisator registered, waiting for admin approval!");
                break;
            case "admin":
                roles.add(roleRepository.findByName(Erole.ROLE_ADMIN).orElseThrow());
                user.setActive(true);
                model.addAttribute("message", "Admin registered successfully!");
                break;
            default:
                roles.add(roleRepository.findByName(Erole.ROLE_USER).orElseThrow());
                user.setActive(true);
                model.addAttribute("message", "User registered successfully!");
        }

        user.setRoles(roles);
        userRepository.save(user);

        return "signup";
    }
}
