package com.ecommerce.auth.infrastructure.persistence.adapter;

import com.ecommerce.auth.domain.entity.AuthToken;
import com.ecommerce.auth.domain.repository.AuthTokenRepository;
import com.ecommerce.auth.infrastructure.persistence.entity.AuthTokenJpaEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * Adapter: implements the Domain's AuthTokenRepository port using Spring Data JPA.
 *
 * Converts between Domain AuthToken and JPA AuthTokenJpaEntity.
 */
@Component
public class AuthTokenRepositoryAdapter implements AuthTokenRepository {

    private final JpaAuthTokenRepository jpaAuthTokenRepository;

    public AuthTokenRepositoryAdapter(JpaAuthTokenRepository jpaAuthTokenRepository) {
        this.jpaAuthTokenRepository = jpaAuthTokenRepository;
    }

    @Override
    public AuthToken save(AuthToken token) {
        AuthTokenJpaEntity jpaEntity = toJpaEntity(token);
        AuthTokenJpaEntity saved = jpaAuthTokenRepository.save(jpaEntity);
        return toDomainEntity(saved);
    }

    @Override
    public Optional<AuthToken> findByToken(String token) {
        return jpaAuthTokenRepository.findByToken(token)
                .map(this::toDomainEntity);
    }

    @Override
    @Transactional
    public void invalidateAllByUserIdAndType(UUID userId, AuthToken.TokenType type) {
        jpaAuthTokenRepository.invalidateAllByUserIdAndType(userId, type);
    }

    // ── Mapping helpers ──────────────────────────────────────────────

    private AuthTokenJpaEntity toJpaEntity(AuthToken token) {
        return AuthTokenJpaEntity.builder()
                .id(token.getId())
                .userId(token.getUserId())
                .token(token.getToken())
                .type(token.getType())
                .used(token.isUsed())
                .expiresAt(token.getExpiresAt())
                .createdAt(token.getCreatedAt())
                .build();
    }

    private AuthToken toDomainEntity(AuthTokenJpaEntity entity) {
        return AuthToken.reconstitute(
                entity.getId(),
                entity.getUserId(),
                entity.getToken(),
                entity.getType(),
                entity.isUsed(),
                entity.getExpiresAt(),
                entity.getCreatedAt()
        );
    }
}
