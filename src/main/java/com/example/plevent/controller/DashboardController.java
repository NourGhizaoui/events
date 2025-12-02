package com.example.plevent.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/user/dashboard")
    public String userDashboard(Authentication authentication, Model model) {
        model.addAttribute("username", authentication.getName());
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

