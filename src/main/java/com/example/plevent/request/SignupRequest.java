package com.example.plevent.request;

import jakarta.validation.constraints.*;
import java.util.Set;

public class SignupRequest {


@NotBlank
@Size(min = 3, max = 50)
private String username;

@NotBlank
@Size(max = 100)
@Email
private String email;

private Set<String> role;

@NotBlank
@Size(min = 6, max = 40)
private String password;

// Constructeur par défaut
public SignupRequest() {
}

// Getters et Setters
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

public Set<String> getRole() {
    return role;
}

public void setRole(Set<String> role) {
    this.role = role;
}

public String getPassword() {
    return password;
}

public void setPassword(String password) {
    this.password = password;
}


}
