package com.example.plevent.service;

import com.example.plevent.model.Role;
import com.example.plevent.model.User;
import com.example.plevent.repository.RoleRepository;
import com.example.plevent.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;


    public List<User> getPendingOrganizers() {
        return userRepository.findAll().stream()
                .filter(u -> u.getRoles().stream()
                        .anyMatch(r -> r.getName().name().equals("ROLE_ORGANISATOR")))
                .filter(u -> !u.isEnabled())
                .toList();
    }

    public void enableUser(Long id) {
        User u = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        u.setActive(true);
        userRepository.save(u);
    }

    public void disableUser(Long id) {
        User u = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        u.setActive(false);
        userRepository.save(u);
    }
    
 // Recherche par username et password
    public User findByUsernameAndPassword(String username, String password) {
        return userRepository.findByUsernameAndPassword(username, password);
    }
    
    public void setUserActiveStatus(Long userId, boolean status) {
    	User user = userRepository.findById(userId)
    	.orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
    	user.setActive(status);
    	userRepository.save(user);
    	}
    // Vérifie si username existe
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    // Sauvegarde un utilisateur
    public void save(User user) {
        userRepository.save(user);
    }

    // Récupère tous les rôles
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    
}


/*
@Service
public class UserService implements UserDetailsService {
	

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder; // 🎯 FIX 1: Declare the field
    // Inject dependent services (REQUIRED if handling cleanup in service)
    private final BookingService bookingService;
    private final EventService eventService;

    // 🎯 FIX 2: Updated Constructor to include passwordEncoder
    public UserService(UserRepository userRepository,
                       @Lazy BookingService bookingService, // Use @Lazy for potential circular dependency
                       @Lazy EventService eventService,      // Use @Lazy for potential circular dependency
                       BCryptPasswordEncoder passwordEncoder) { // Inject the encoder
        this.userRepository = userRepository;
        this.bookingService = bookingService;
        this.eventService = eventService;
        this.passwordEncoder = passwordEncoder; // Assign the encoder
    }

    // Used for login
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);

        if (user == null)
            throw new UsernameNotFoundException("User not found");

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority(user.getRole().getName()))
        );
    }

    // Used for registration
    public void saveUser(User user) {
        // Only encode if the password is new/changed and not already encoded (common practice)
        if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        userRepository.save(user);
    }

    // Used in ApiBookingController
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // 🎯 Used by AdminController to list all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // 🎯 Used by AdminController to update a user's role
    public User findById(Integer id) {
        Optional<User> userOptional = userRepository.findById(id);
        return userOptional.orElse(null);
    }

    // 🎯 Robust delete method to handle dependencies
    public void deleteById(Integer id) {
        // 1. Delete all bookings associated with this user
        // Requires a new derived delete method in BookingRepository
        bookingService.deleteByUserId(id);

        // 2. Delete all events organized by this user
        // Requires a new derived delete method in EventRepository
        eventService.deleteByOrganizerId(id);

        // 3. Delete the user
        userRepository.deleteById(id);
    }
    */
