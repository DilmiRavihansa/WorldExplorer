package com.worldexplorer.auth;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Authentication: checks a username/password against the UserRepository.
 * - passwords are verified against salted PBKDF2 hashes
 * - after MAX_ATTEMPTS wrong passwords the account is locked for LOCK_MILLIS (slows down guessing)
 * - the same message is used for "unknown user" and "wrong password" so usernames cannot be probed
 */
public class AuthService {
    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCK_MILLIS = 30_000;

    private final UserRepository users;
    private final Map<String, Integer> failures = new HashMap<>();
    private final Map<String, Long> lockedUntil = new HashMap<>();

    public AuthService(UserRepository users) {
        this.users = users;
        seedDemoUsersIfEmpty();
    }

    /** First run only: creates the demo accounts (stored hashed). Delete users.db to reset. */
    private void seedDemoUsersIfEmpty() {
        if (!users.isEmpty()) return;
        users.save("player1", PasswordHasher.hash("pass1".toCharArray()));
        users.save("player2", PasswordHasher.hash("pass2".toCharArray()));
        users.save("Marc", PasswordHasher.hash("hello26".toCharArray()));
    }

    public synchronized AuthResult login(String username, char[] password) {
        if (username == null || username.isBlank() || password == null || password.length == 0)
            return AuthResult.fail(AuthResult.Status.EMPTY_INPUT, "Please enter your username and password.");
        username = username.trim();

        long wait = lockedUntil.getOrDefault(username, 0L) - System.currentTimeMillis();
        if (wait > 0)
            return AuthResult.fail(AuthResult.Status.LOCKED,
                    "Too many attempts. Try again in " + (wait / 1000 + 1) + " seconds.");

        Optional<String> hash = users.findPasswordHash(username);
        boolean ok = hash.isPresent() && PasswordHasher.verify(password, hash.get());
        if (ok) {
            failures.remove(username);
            lockedUntil.remove(username);
            return AuthResult.ok(username);
        }
        int n = failures.merge(username, 1, Integer::sum);
        if (n >= MAX_ATTEMPTS) {
            failures.remove(username);
            lockedUntil.put(username, System.currentTimeMillis() + LOCK_MILLIS);
        }
        return AuthResult.fail(AuthResult.Status.BAD_CREDENTIALS, "Wrong username or password.");
    }

    public boolean userExists(String username) {
        return username != null && users.findPasswordHash(username.trim()).isPresent();
    }
}
