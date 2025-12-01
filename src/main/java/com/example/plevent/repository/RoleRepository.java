package com.example.plevent.repository;

import com.example.plevent.model.Erole;
import com.example.plevent.model.Role;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(Erole name);
    List<Role> findByNameNot(Erole name);


}

