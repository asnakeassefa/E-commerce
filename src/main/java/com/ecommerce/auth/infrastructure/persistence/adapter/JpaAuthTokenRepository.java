package com.ecommerce.auth.infrastructure.persistence.adapter;

import com.ecommerce.auth.domain.entity.AuthToken;
import com.ecommerce.auth.infrastructure.persistence.entity.AuthTokenJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaAuthTokenRepository extends JpaRepository<AuthTokenJpaEntity, UUID> {

    Optional<AuthTokenJpaEntity> findByToken(String token);

    /**
     * Marks all unused tokens of a given type for a user as used.
     * This prevents old tokens from being valid after a new one is issued.
     */
    @Modifying
    @Query("UPDATE AuthTokenJpaEntity t SET t.used = true " +
           "WHERE t.userId = :userId AND t.type = :type AND t.used = false")
    void invalidateAllByUserIdAndType(@Param("userId") UUID userId,
                                       @Param("type") AuthToken.TokenType type);
}
