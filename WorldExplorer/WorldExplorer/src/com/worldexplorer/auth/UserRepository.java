package com.worldexplorer.auth;

import java.util.Optional;

/** Where user accounts live. A database implementation could replace the file one without touching AuthService. */
public interface UserRepository {
    Optional<String> findPasswordHash(String username);

    void save(String username, String passwordHash);

    boolean isEmpty();
}
