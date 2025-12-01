package com.example.plevent.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.example.plevent.model.User;
import com.example.plevent.security.UserDetailsImpl;
import com.example.plevent.service.UserService;
/*
@Controller
@RequestMapping("/admin")
public class AdminViewController {

    @GetMapping("/login")
    public String adminLogin() {
        return "admin/loginadmin";
    }

    @GetMapping("/dashboard")
    public String adminDashboard(@AuthenticationPrincipal UserDetailsImpl admin, Model model) {
        model.addAttribute("admin", admin);
        return "admin/dashboard";
    }
    
    
    @Autowired
    private UserService userService;

    // Liste des organisateurs en attente
    @GetMapping("/users")
    public String getPendingUsers(Model model) {
        List<User> pendingOrganizers = userService.getPendingOrganizers();
        model.addAttribute("organizers", pendingOrganizers);
        return "admin/users";
    }

    // Validation
    @PostMapping("/validate/{id}")
    public String validateUser(@PathVariable Long id) {
        userService.enableUser(id);
        return "redirect:/admin/users?success";
    }

    // Désactivation
    @PostMapping("/disable/{id}")
    public String disableUser(@PathVariable Long id) {
        userService.disableUser(id);
        return "redirect:/admin/users?disabled";
    }
}
*/


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.example.plevent.model.User;
import com.example.plevent.security.UserDetailsImpl;
import com.example.plevent.service.UserService;
import com.example.plevent.repository.UserRepository;

@Controller
@RequestMapping("/admin")
public class AdminViewController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/login")
    public String adminLogin() {
        return "admin/loginadmin";
    }

    @GetMapping("/dashboard")
    public String adminDashboard(@AuthenticationPrincipal UserDetailsImpl admin, Model model) {
        model.addAttribute("admin", admin);

        // Statistiques simples
        long totalUsers = userRepository.count();
        long totalActive = userRepository.findAll().stream().filter(User::isEnabled).count();
        long pendingOrganizers = userService.getPendingOrganizers().size();

        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalActive", totalActive);
        model.addAttribute("pendingOrganizers", pendingOrganizers);

        return "admin/dashboard";
    }

    // Liste des utilisateurs (organisateurs en attente et actifs)
  
    /*
    @GetMapping("/users")
    public String getUsers(Model model) {
    // Récupère les organisateurs en attente et les utilisateurs actifs
    List<User> pendingOrganizers = userService.getPendingOrganizers();
    List<User> activeUsers = userRepository.findAll().stream()
    .filter(User::isEnabled)
    .toList();

    
    // On passe directement les listes à Thymeleaf
    model.addAttribute("pendingOrganizers", pendingOrganizers);
    model.addAttribute("activeUsers", activeUsers);

    return "admin/users";
    

    }*/
    
    @GetMapping("/users")
    public String getUsers(Model model) {
        // Récupère les organisateurs en attente
        List<User> pendingOrganizers = userService.getPendingOrganizers();

        // Récupère tous les utilisateurs actifs sauf les admins
        List<User> activeUsers = userRepository.findAll().stream()
            .filter(User::isEnabled)
            .filter(u -> u.getRoles().stream().noneMatch(r -> r.getName().name().equals("ROLE_ADMIN")))
            .toList();

        // On passe les listes à Thymeleaf
        model.addAttribute("pendingOrganizers", pendingOrganizers);
        model.addAttribute("activeUsers", activeUsers);

        return "admin/users";
    }



   
    @GetMapping("/user/activate/{id}")
    public String activateUser(@PathVariable Long id) {
    userService.setUserActiveStatus(id, true);
    return "redirect:/admin/users";
    }

    @GetMapping("/user/deactivate/{id}")
    public String deactivateUser(@PathVariable Long id) {
    userService.setUserActiveStatus(id, false);
    return "redirect:/admin/users";
    }

}
