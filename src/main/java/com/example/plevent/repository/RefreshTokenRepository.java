package com.example.plevent.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.plevent.model.RefreshToken;
import com.example.plevent.model.User;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByUser(User user); // <-- ajouté
    int deleteByUser(User user);
}
