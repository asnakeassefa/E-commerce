package com.ecommerce.auth.domain.entity;

import java.time.LocalDateTime;
import java.util.UUID;

public class User {
    

    private UUID id;
    private String name;
    private String email;
    private String password;
    private Role role;
    private boolean verified;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

public static User createNew(String name, String email, String hashedPassword, Role role) {
        User user = new User();
        user.name = name;
        user.email = email;
        user.password = hashedPassword;
        user.role = role;
        user.verified = false;
        // user.credentialsNonExpired = true;
        // user.accountNonExpired = true;
        // user.accountNonLocked = true;
        user.active = (role != Role.VENDOR); // Vendors require admin approval
        user.createdAt = LocalDateTime.now();
        user.updatedAt = LocalDateTime.now();
        return user;
    }
public static User reconstitute(UUID id, String name, String email, String password, Role role, boolean verified, boolean active, LocalDateTime createdAt, LocalDateTime updatedAt) {
        User user = new User();
        user.id = id;
        user.name = name;
        user.email = email;
        user.password = password;
        user.role = role;
        user.verified = verified;
        user.active = active;
        // user.credentialsNonExpired = credentialsNonExpired;
        // user.accountNonExpired = accountNonExpired;
        // user.accountNonLocked = accountNonLocked;
        user.createdAt = createdAt;
        user.updatedAt = updatedAt;
        return user;
        
    }


public void validateForRegistration() {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name must not be blank");
        }
        if (email == null || !email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (role == null) {
            throw new IllegalArgumentException("Role must be specified");
        }
    }
    public UUID getId()                { return id; }
    public String getName()            { return name; }
    public String getEmail()           { return email; }
    public String getPassword()        { return password; }
    public Role getRole()              { return role; }
    public boolean isActive()          { return active; }
    public boolean isVerified()        { return verified; }
    // public boolean isCredentialsNonExpired() { return credentialsNonExpired; }
    // public boolean isAccountNonExpired() { return accountNonExpired; }
    // public boolean isAccountNonLocked() { return accountNonLocked; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }


    public void updatePassword(String hashedPassword) {
        this.password = hashedPassword;
        this.updatedAt = LocalDateTime.now();
    }

    public void activate() {
        this.active = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void deactivate() {
        this.active = false;
        this.updatedAt = LocalDateTime.now();
    }
    public void verify() {
        this.verified = true;
        this.updatedAt = LocalDateTime.now();
    }
    public void unverify() {
        this.verified = false;
        this.updatedAt = LocalDateTime.now();
    }
    // public void lock() {
    //     this.accountNonLocked = false;
    //     this.updatedAt = LocalDateTime.now();
    // }
    // public void unlock() {
    //     this.accountNonLocked = true;
    //     this.updatedAt = LocalDateTime.now();
    // }
    // public void expire() {
    //     this.accountNonExpired = false;
    //     this.updatedAt = LocalDateTime.now();
    // }
    // public void unexpire() {
    //     this.accountNonExpired = true;
    //     this.updatedAt = LocalDateTime.now();
    // }
    // public void expireCredentials() {
    //     this.credentialsNonExpired = false;
    //     this.updatedAt = LocalDateTime.now();
    // }
    // public void unexpireCredentials() {
    //     this.credentialsNonExpired = true;
    //     this.updatedAt = LocalDateTime.now();
    // }
    private User() {}

}
