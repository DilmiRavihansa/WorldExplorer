package com.worldexplorer.auth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermissions;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;
import java.util.Properties;

/**
 * Cookie-style sessions for the desktop game.
 *
 * After a successful login a random session token is created.
 *  - The CLIENT part (the "cookie") is the file session.cookie holding the token, the user and an expiry time.
 *    It is only written when the player ticks "Remember me"; otherwise the session lives in memory only
 *    (like a browser session cookie that disappears when the browser closes).
 *  - The SERVER part (sessions.db) keeps only a SHA-256 hash of each token, so a stolen sessions.db cannot be used.
 * On start-up restore() reads the cookie, checks the token hash and the expiry and signs the player in again.
 * Signing out deletes the cookie and invalidates the token on the "server" side.
 */
public class SessionManager {
    private static final String COOKIE_NAME = "we_session";
    private static final long LIFETIME_MILLIS = 7L * 24 * 60 * 60 * 1000; // 7 days

    private final Path cookieFile;
    private final Path serverFile;
    private final SecureRandom random = new SecureRandom();
    private String currentUser = null;
    private String currentToken = null;

    public SessionManager() { this(Paths.get("session.cookie"), Paths.get("sessions.db")); }

    public SessionManager(Path cookieFile, Path serverFile) {
        this.cookieFile = cookieFile;
        this.serverFile = serverFile;
    }

    public synchronized String getCurrentUser() { return currentUser; }

    /** Starts a session. persistent = "Remember me". */
    public synchronized void start(String username, boolean persistent) {
        end(); // never keep an old session alive
        byte[] raw = new byte[32];
        random.nextBytes(raw);
        currentToken = Base64.getUrlEncoder().withoutPadding().encodeToString(raw);
        currentUser = username;
        if (persistent) {
            long expires = System.currentTimeMillis() + LIFETIME_MILLIS;
            Properties server = new Properties();
            server.setProperty(sha256(currentToken), username + "|" + expires);
            store(serverFile, server, "World Explorer sessions (token hashes only)");

            Properties cookie = new Properties();
            cookie.setProperty("name", COOKIE_NAME);
            cookie.setProperty("value", currentToken);
            cookie.setProperty("user", username);
            cookie.setProperty("expires", String.valueOf(expires));
            store(cookieFile, cookie, "World Explorer session cookie - do not share");
        }
    }

    /** Signs in again from a saved cookie, if it is still valid. */
    public synchronized Optional<String> restore() {
        Properties cookie = read(cookieFile);
        String token = cookie.getProperty("value");
        String user = cookie.getProperty("user");
        if (!COOKIE_NAME.equals(cookie.getProperty("name")) || token == null || user == null) return Optional.empty();
        try {
            long expires = Long.parseLong(cookie.getProperty("expires", "0"));
            String record = read(serverFile).getProperty(sha256(token));
            boolean valid = record != null && record.equals(user + "|" + expires)
                    && expires > System.currentTimeMillis();
            if (!valid) {
                deleteCookie();
                return Optional.empty();
            }
        } catch (NumberFormatException e) {
            deleteCookie();
            return Optional.empty();
        }
        currentUser = user;
        currentToken = token;
        return Optional.of(user);
    }

    /** Sign out: remove the cookie and the server-side record. */
    public synchronized void end() {
        if (currentToken != null) {
            Properties server = read(serverFile);
            if (server.remove(sha256(currentToken)) != null) store(serverFile, server, "World Explorer sessions (token hashes only)");
        }
        deleteCookie();
        currentUser = null;
        currentToken = null;
    }

    private void deleteCookie() {
        try {
            Files.deleteIfExists(cookieFile);
        } catch (IOException e) {
            System.out.println("Could not delete session cookie: " + e);
        }
    }

    private static Properties read(Path f) {
        Properties p = new Properties();
        try {
            if (Files.exists(f)) try (InputStream in = Files.newInputStream(f)) { p.load(in); }
        } catch (IOException e) {
            System.out.println("Could not read " + f + ": " + e);
        }
        return p;
    }

    private static void store(Path f, Properties p, String comment) {
        try (OutputStream out = Files.newOutputStream(f)) {
            p.store(out, comment);
            try {
                Files.setPosixFilePermissions(f, PosixFilePermissions.fromString("rw-------"));
            } catch (UnsupportedOperationException | IOException ignored) {
                // not a POSIX file system (Windows): nothing to do
            }
        } catch (IOException e) {
            System.out.println("Could not write " + f + ": " + e);
        }
    }

    private static String sha256(String s) {
        try {
            byte[] d = MessageDigest.getInstance("SHA-256").digest(s.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(d).replace("=", "");
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
