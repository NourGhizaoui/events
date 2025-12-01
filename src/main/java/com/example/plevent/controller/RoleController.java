package com.example.plevent.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.plevent.repository.RoleRepository;

@RestController
@RequestMapping("/api")
public class RoleController {

    @Autowired
    private RoleRepository roleRepository;

    @GetMapping("/roles")
    public List<String> getRoles() {
        // Retourne une liste simple de String : ["ROLE_USER", "ROLE_ADMIN", ...]
        return roleRepository.findAll()
                .stream()
                .map(role -> role.getName().name()) 
                .toList();
    }
}

