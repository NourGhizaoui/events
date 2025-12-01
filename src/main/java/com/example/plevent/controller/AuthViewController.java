package com.example.plevent.controller;

import com.example.plevent.model.Erole;
import com.example.plevent.model.Role;
import com.example.plevent.model.User;
import com.example.plevent.repository.RoleRepository;
import com.example.plevent.repository.UserRepository;
import com.example.plevent.request.SignupRequest;
import com.example.plevent.service.UserService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/auth")
public class AuthViewController {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        // Crée un nouvel objet User pour le formulaire
        model.addAttribute("userForm", new User());
        
        // Récupère les rôles sauf ADMIN
        List<Role> roles = roleRepository.findByNameNot(Erole.ROLE_ADMIN);
        model.addAttribute("roles", roles);

        return "register"; // nom du template register.html
    }


/*
    @PostMapping("/signup")
    public String processSignup(@ModelAttribute("userForm") SignupRequest form, Model model) {

        if (userRepository.existsByUsername(form.getUsername())) {
            model.addAttribute("error", "Nom d'utilisateur déjà pris !");
            model.addAttribute("roles", roleRepository.findAll());
            return "register";
        }

        if (userRepository.existsByEmail(form.getEmail())) {
            model.addAttribute("error", "Email déjà utilisé !");
            model.addAttribute("roles", roleRepository.findAll());
            return "register";
        }

        User user = new User();
        user.setUsername(form.getUsername());
        user.setEmail(form.getEmail());
        user.setPassword(encoder.encode(form.getPassword()));

        Set<Role> roles = new HashSet<>();
        if (form.getRole() == null) {
            roles.add(roleRepository.findByName(Erole.ROLE_USER).orElseThrow());
        } else {
            form.getRole().forEach(r -> {
                switch (r.toLowerCase()) {
                    case "admin":
                        roles.add(roleRepository.findByName(Erole.ROLE_ADMIN).orElseThrow());
                        break;
                    case "organisateur":
                        roles.add(roleRepository.findByName(Erole.ROLE_ORGANISATOR).orElseThrow());
                        break;
                    default:
                        roles.add(roleRepository.findByName(Erole.ROLE_USER).orElseThrow());
                }
            });
        }
        user.setRoles(roles);
        user.setActive(true);

        userRepository.save(user);

        model.addAttribute("success", "Inscription réussie !");
        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("userForm", new SignupRequest());
        return "register";
    }

    @GetMapping("/login")
    public String showLoginForm(Model model,
                                @RequestParam(value = "error", required = false) String error,
                                @RequestParam(value = "logout", required = false) String logout) {

        if (error != null) {
            model.addAttribute("error", "Nom d'utilisateur ou mot de passe incorrect !");
        }

        if (logout != null) {
            model.addAttribute("success", "Déconnexion réussie !");
        }

        return "login"; // Thymeleaf template login.html
    }*/
    
    @PostMapping("/signup")
    public String processSignup(@ModelAttribute("userForm") SignupRequest form, Model model) {

        if (userRepository.existsByUsername(form.getUsername())) {
            model.addAttribute("error", "Nom d'utilisateur déjà pris !");
            model.addAttribute("roles", roleRepository.findAll());
            return "register";
        }

        if (userRepository.existsByEmail(form.getEmail())) {
            model.addAttribute("error", "Email déjà utilisé !");
            model.addAttribute("roles", roleRepository.findAll());
            return "register";
        }

        User user = new User();
        user.setUsername(form.getUsername());
        user.setEmail(form.getEmail());
        user.setPassword(encoder.encode(form.getPassword()));

        Set<Role> roles = new HashSet<>();
        boolean isOrganisateur = false;

        if (form.getRole() == null) {
            // par défaut, ROLE_USER
            roles.add(roleRepository.findByName(Erole.ROLE_USER).orElseThrow());
        } else {
            for (String r : form.getRole()) {
                Erole erole;
                switch (r.toLowerCase()) {
                    case "organisateur":
                    case "role_organisator":  // selon ce que ton formulaire envoie
                        erole = Erole.ROLE_ORGANISATOR;
                        isOrganisateur = true;
                        break;
                    case "admin":
                    case "role_admin":
                        erole = Erole.ROLE_ADMIN;
                        break;
                    default:
                        erole = Erole.ROLE_USER;
                }
                Role roleEntity = roleRepository.findByName(erole)
                        .orElseThrow(() -> new RuntimeException("Role non trouvé : " + erole));
                roles.add(roleEntity);
            }
        }

        user.setRoles(roles);
        user.setActive(!isOrganisateur);  // true si user normal, false si organisateur

        System.out.println("Roles assigned: " + roles);
        System.out.println("isOrganisateur = " + isOrganisateur);
        System.out.println("Active = " + user.isEnabled());

        userRepository.save(user);

        model.addAttribute("success", "Inscription réussie !");
        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("userForm", new SignupRequest());
        return "register";
    }

  /*  @GetMapping("/login")
    public String showLoginForm(Model model,
                                @RequestParam(value = "error", required = false) String error,
                                @RequestParam(value = "logout", required = false) String logout) {

        if (error != null) {
            model.addAttribute("error", "Nom d'utilisateur ou mot de passe incorrect !");
        }

        if (logout != null) {
            model.addAttribute("success", "Déconnexion réussie !");
        }

        return "login"; // Thymeleaf template login.html 
    }*/
    
    
    
    @Autowired
    private UserService userService;

    // Affiche le formulaire de login
    @GetMapping("/login")
    public String showLoginForm() {
        return "login"; // login.html
    }

    // Traitement du login
    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {

        User user = userService.findByUsernameAndPassword(username, password);

        if (user == null) {
            model.addAttribute("error", "Nom d'utilisateur ou mot de passe incorrect");
            return "login";
        }

        // Sauvegarde user dans session
        session.setAttribute("loggedUser", user);

        // Vérification rôle et activation
        for (Role role : user.getRoles()) {
            String roleName = role.getName().name(); // ROLE_USER, ROLE_ORGANISATOR, ROLE_ADMIN

            switch (roleName) {
                case "ROLE_USER":
                    return "redirect:/user/dashboard";
                case "ROLE_ORGANISATOR":
                    if (user.isEnabled()) {
                        return "redirect:/organizer/dashboard";
                    } else {
                        model.addAttribute("error", "Il faut attendre que l'administrateur accepte votre compte !");
                        return "login";
                    }
                case "ROLE_ADMIN":
                    return "redirect:/admin/dashboard";
            }
        }

        // Par défaut si rôle non reconnu
        model.addAttribute("error", "Rôle non reconnu");
        return "login";
    }

}
