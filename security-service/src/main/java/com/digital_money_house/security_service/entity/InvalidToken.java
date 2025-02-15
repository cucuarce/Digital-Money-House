package com.digital_money_house.security_service.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "invalid_tokens")
public class InvalidToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private Long id;

    @Column(name = "token", nullable = false, unique = true)
    private String token;

    @Column(name = "invalidated_at")
    private LocalDateTime invalidatedAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    public InvalidToken(String token) {
        this.token = token;
    }

    public InvalidToken() {
    }
}
