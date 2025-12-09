package com.example.plevent.controller;

import com.example.plevent.model.Event;
import com.example.plevent.model.EventStatus;
import com.example.plevent.model.User;
import com.example.plevent.repository.CategoryRepository;
import com.example.plevent.repository.EventRepository;
import com.example.plevent.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/organisator")
public class OrganizatorController {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private CategoryRepository categoryRepository;
    
    
    @Autowired
    private UserRepository userRepository;
    // Dashboard avec liste et statistiques
    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        User organizer = userRepository.findByUsername(authentication.getName())
                                       .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        List<Event> events = eventRepository.findByOrganizerId(organizer.getId().intValue());

        long totalEvents = events.size();
        long pendingEvents = events.stream().filter(e -> e.getStatus() == EventStatus.PENDING).count();
        long approvedEvents = events.stream().filter(e -> e.getStatus() == EventStatus.APPROVED).count();
        long pastEvents = events.stream().filter(e -> e.getEventDate().isBefore(LocalDateTime.now())).count();

        model.addAttribute("events", events);
        model.addAttribute("totalEvents", totalEvents);
        model.addAttribute("pendingEvents", pendingEvents);
        model.addAttribute("approvedEvents", approvedEvents);
        model.addAttribute("pastEvents", pastEvents);

        return "organizer/dashboard_organisator";
    }


    // Formulaire création
    @GetMapping("/event/new")
    public String newEventForm(Model model) {
        model.addAttribute("event", new Event());
        model.addAttribute("categories", categoryRepository.findAll()); // toutes les catégories existantes

        return "organizer/event_form";
    }

    // Sauvegarder création / édition
   /* @PostMapping("/event/save")
    public String saveEvent(@ModelAttribute Event event, Authentication authentication) {
        User organizer = userRepository.findByUsername(authentication.getName())
                                       .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        event.setOrganizer(organizer);
        eventRepository.save(event);
        return "redirect:/organisator/dashboard";
    }*/
    
    
    @PostMapping("/event/save")
    public String saveEvent(@ModelAttribute Event event, Authentication authentication) {

        User organizer = userRepository.findByUsername(authentication.getName())
                                       .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        event.setOrganizer(organizer);

        // Si nouvel événement → status = PENDING
        if (event.getId() == null) {
            event.setStatus(EventStatus.PENDING);
        } else {
            // Si édition, garder le status actuel depuis la DB
            Event existing = eventRepository.findById(event.getId())
                                            .orElseThrow(() -> new RuntimeException("Événement introuvable"));
            event.setStatus(existing.getStatus());
        }

        eventRepository.save(event);
        return "redirect:/organisator/dashboard";
    }

    


    // Éditer événement
    @GetMapping("/event/edit/{id}")
    public String editEventForm(@PathVariable Integer id, Model model) {
        Event event = eventRepository.findById(id).orElseThrow();
        model.addAttribute("event", event);
        model.addAttribute("categories", categoryRepository.findAll());

        return "organizer/event_form";
    }

    // Supprimer événement
    @PostMapping("/event/delete/{id}")
    public String deleteEvent(@PathVariable Integer id) {
        eventRepository.deleteById(id);
        return "redirect:/organisator/dashboard";
    }
    
    
    
    
}
