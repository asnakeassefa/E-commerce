package com.ecommerce.auth.domain.repository;

import com.ecommerce.auth.domain.entity.User;

import java.util.Optional;
import java.util.UUID;

/**
 * Port (interface) for User persistence.
 * This lives in the Domain layer. The actual database implementation
 * lives in Infrastructure — the domain doesn't know or care what
 * database is behind this.
 */
public interface UserRepository {

    User save(User user);

    Optional<User> findByEmail(String email);

    Optional<User> findById(UUID id);

    boolean existsByEmail(String email);
}
