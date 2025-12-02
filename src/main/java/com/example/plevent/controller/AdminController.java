package com.example.plevent.controller;

import com.example.plevent.model.User;
import com.example.plevent.repository.UserRepository;
import com.example.plevent.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    // Dashboard admin
    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        List<User> allUsers = userRepository.findAll();

        // Filtrer seulement USER ou ORGANISATOR
        List<User> users = allUsers.stream()
            .filter(u -> u.getRoles().stream()
                          .anyMatch(r -> r.getName().name().equals("ROLE_USER") 
                                      || r.getName().name().equals("ROLE_ORGANISATOR")))
            .toList();

        model.addAttribute("users", users);

        // Statistiques correctes
        long totalUsers = users.size();
        long organisators = users.stream()
                                 .filter(u -> u.getRoles().stream()
                                               .anyMatch(r -> r.getName().name().equals("ROLE_ORGANISATOR")))
                                 .count();
        long normalUsers = users.stream()
                                .filter(u -> u.getRoles().stream()
                                              .anyMatch(r -> r.getName().name().equals("ROLE_USER")))
                                .count();

        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("organisators", organisators);
        model.addAttribute("normalUsers", normalUsers);

        return "admin/dashboard_admin";
    }

    // Activer/Désactiver utilisateur
    @PostMapping("/user/{id}/toggle")
    public String toggleUser(@PathVariable Long id) {
        User user = userRepository.findById(id).orElseThrow();
        user.setActive(!user.isEnabled());
        userRepository.save(user);
        return "redirect:/admin/dashboard";
    }

    // Supprimer utilisateur
    @PostMapping("/user/{id}/delete")
    public String deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return "redirect:/admin/dashboard";
    }


    
}
