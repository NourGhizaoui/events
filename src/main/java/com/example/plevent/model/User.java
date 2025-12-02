package com.example.plevent.model;

import jakarta.persistence.*;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.HashSet;

@Entity
@Table(name = "users")
public class User {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

@Column(length = 50, unique = true, nullable = false)
private String username;

@Column(length = 100, unique = true, nullable = false)
private String email;

@Column(nullable = false)
private String password;

@ManyToMany(fetch = FetchType.EAGER)
@JoinTable(
    name = "user_roles",
    joinColumns = @JoinColumn(name = "user_id"),
    inverseJoinColumns = @JoinColumn(name = "role_id")
)
private Set<Role> roles = new HashSet<>();

// Constructeur par défaut
public User() {
}

// Constructeur avec tous les champs
public User(Long id, String username, String email, String password, Set<Role> roles) {
    this.id = id;
    this.username = username;
    this.email = email;
    this.password = password;
    this.roles = roles;
}

//🔥 Ajout du constructeur utilisé dans AuthController
public User(String username, String email, String password) {
 this.username = username;
 this.email = email;
 this.password = password;
}

// Getters et Setters
public Long getId() {
    return id;
}

public void setId(Long id) {
    this.id = id;
}

public String getUsername() {
    return username;
}

public void setUsername(String username) {
    this.username = username;
}

public String getEmail() {
    return email;
}

public void setEmail(String email) {
    this.email = email;
}

public String getPassword() {
    return password;
}

public void setPassword(String password) {
    this.password = password;
}

public Set<Role> getRoles() {
    return roles;
}

public void setRoles(Set<Role> roles) {
    this.roles = roles;
}


private boolean active ; // 🔥 NEW
public boolean isEnabled() {
    return active;
}

public void setActive(boolean active) {
    this.active = active;
}




@Transient  // ⚡ Indique à JPA que ce n’est pas une colonne en base
public String getRolesAsString() {
    return roles.stream()
                .map(r -> r.getName().name()) // ou r.getName() selon ton enum
                .collect(Collectors.joining(", "));
}



@Transient
public String getRoleSimple() {
    if (roles.stream().anyMatch(r -> r.getName().name().equals("USER"))) {
        return "USER";
    } else if (roles.stream().anyMatch(r -> r.getName().name().equals("ORGANISATOR"))) {
        return "ORGANISATOR";
    }
    return "";
}
@Transient
public String getFilteredRoles() {
    return roles.stream()
                .map(r -> r.getName().name()) // récupère le nom exact de l'enum
                .filter(n -> n.equals("ROLE_USER") || n.equals("ROLE_ORGANISATOR")) // filtrer seulement USER et ORGANISATOR
                .map(n -> n.replace("ROLE_", "")) // pour afficher juste USER ou ORGANISATOR
                .collect(Collectors.joining(", "));
}


}
