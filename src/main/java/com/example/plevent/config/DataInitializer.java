package com.example.plevent.config;

import com.example.plevent.model.Category;
import com.example.plevent.model.Role;
import com.example.plevent.model.Erole;
import com.example.plevent.repository.CategoryRepository;
import com.example.plevent.repository.RoleRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataInitializer {

    @Autowired
    private RoleRepository roleRepository;

    // Initialisation des rôles via CommandLineRunner
    @Bean
    public CommandLineRunner initRoles() {
        return args -> {
            for (Erole erole : Erole.values()) {
                roleRepository.findByName(erole).orElseGet(() -> {
                    Role role = new Role();
                    role.setName(erole);
                    return roleRepository.save(role);
                });
            }
            System.out.println("Roles initialized in database.");
        };
    }

    // Initialisation des catégories via CommandLineRunner
    @Bean
    public CommandLineRunner initCategories(CategoryRepository categoryRepository) {
        return args -> {
            if (categoryRepository.count() == 0) {
                List<Category> categories = List.of(
                        new Category("Technology", "Events related to tech, coding, and startups."),
                        new Category("Music", "Concerts, festivals, and live performances."),
                        new Category("Sports", "Fitness events, tournaments, and outdoor activities."),
                        new Category("Art & Culture", "Museums, galleries, and theater shows.")
                );

                categoryRepository.saveAll(categories);
                System.out.println("--- Categories Initialized ---");
            }
        };
    }
}

