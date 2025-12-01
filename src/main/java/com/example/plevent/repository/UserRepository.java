package com.example.plevent.repository;

import com.example.plevent.model.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    
    Optional<User> findByUsername(String username);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    
    // Recherche par username et password pour authentification
     User findByUsernameAndPassword(String username, String password);

}
