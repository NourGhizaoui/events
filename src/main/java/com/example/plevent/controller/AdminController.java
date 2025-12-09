package com.example.plevent.controller;

import com.example.plevent.model.Event;
import com.example.plevent.model.EventStatus;
import com.example.plevent.model.User;
import com.example.plevent.repository.UserRepository;
import com.example.plevent.repository.EventRepository;
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
    @Autowired
    private EventRepository eventRepository;


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
        
        model.addAttribute("events", eventRepository.findAll());


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


    
    
    @PostMapping("/event/{id}/approve")
    public String approveEvent(@PathVariable Integer id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        event.setStatus(EventStatus.APPROVED);
        eventRepository.save(event);
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/event/{id}/cancel")
    public String cancelEvent(@PathVariable Integer id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        event.setStatus(EventStatus.REFUSED);
        eventRepository.save(event);
        return "redirect:/admin/dashboard";
    }
    
    
    
    // PAGE LISTE EVENTS
    // =======================
    @GetMapping("/events")
    public String eventsPage(Model model) {
        model.addAttribute("events", eventRepository.findAll());
        return "admin/events_admin";
    }
    
    
    
    @GetMapping("/users")
    public String usersPage(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "admin/users";
    }

    
    
}
