package com.ecommerce.auth.infrastructure.persistence.entity;

import com.ecommerce.auth.domain.entity.AuthToken;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JPA schema entity mapping to the "auth_tokens" table.
 * Stores both email verification and password reset tokens.
 */
@Entity
@Table(name = "auth_tokens", indexes = {
        @Index(name = "idx_auth_tokens_token", columnList = "token", unique = true),
        @Index(name = "idx_auth_tokens_user_type", columnList = "user_id, type")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthTokenJpaEntity {

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "token", nullable = false, unique = true)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private AuthToken.TokenType type;

    @Column(name = "used", nullable = false)
    private boolean used;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
