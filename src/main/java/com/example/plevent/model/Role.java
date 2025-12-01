package com.example.plevent.model;

import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Role {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

@Enumerated(EnumType.STRING)
@Column(length = 20, unique = true, nullable = false)
private Erole name;

// Constructeur par défaut
public Role() {
}

// Constructeur avec tous les champs
public Role(Long id, Erole name) {
    this.id = id;
    this.name = name;
}

// Getters et Setters
public Long getId() {
    return id;
}

public void setId(Long id) {
    this.id = id;
}

public Erole getName() {
    return name;
}

public void setName(Erole name) {
  this.name = name;
}


}
