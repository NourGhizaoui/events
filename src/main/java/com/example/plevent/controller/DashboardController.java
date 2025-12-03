package com.example.plevent.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.plevent.model.Category;
import com.example.plevent.model.Event;
import com.example.plevent.model.EventStatus;
import com.example.plevent.repository.EventRepository;
import com.example.plevent.service.CategoryService;

@Controller
public class DashboardController {

  /*  @GetMapping("/user/dashboard")
    public String userDashboard(Authentication authentication, Model model) {
        model.addAttribute("username", authentication.getName());
        return "user/dashboard_user";
    }*/
	
	  @Autowired
	    private EventRepository eventRepository;
	  @Autowired
	    private CategoryService categoryService;

	    @GetMapping("/user/dashboard")
	    public String userDashboard(Model model, Principal principal) {

	        String username = principal.getName();

	        // Récupérer seulement les events APPROVED
	        List<Event> approvedEvents = eventRepository.findByStatus(EventStatus.APPROVED);
	     // Récupérer toutes les catégories pour le filtre
	        List<Category> categories = categoryService.findAll();
	        model.addAttribute("categories", categories);


	        model.addAttribute("username", username);
	        model.addAttribute("approvedEvents", approvedEvents);

	        // Nom du fichier Thymeleaf : src/main/resources/templates/user/dashboard_user.html
	        return "user/dashboard_user";
	    }

	
/*
    @GetMapping("/organisator/dashboard")
    public String organisatorDashboard(Authentication authentication, Model model) {
        model.addAttribute("username", authentication.getName());
        return "organizer/dashboard_organisator";
    }
*/
/*    @GetMapping("/admin/dashboard")
    public String adminDashboard(Authentication authentication, Model model) {
        model.addAttribute("username", authentication.getName());
        return "admin/dashboard_admin";
    }*/
}

