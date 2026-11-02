package com.worldexplorer.auth;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/** Stores "username:passwordHash" lines in users.db (hashes only, no plain passwords). */
public class FileUserRepository implements UserRepository {
    private final Path file;
    private final Map<String, String> users = new LinkedHashMap<>();

    public FileUserRepository() { this(Paths.get("users.db")); }

    public FileUserRepository(Path file) {
        this.file = file;
        load();
    }

    private void load() {
        try {
            if (!Files.exists(file)) return;
            for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
                int i = line.indexOf(':');
                if (i > 0) users.put(line.substring(0, i), line.substring(i + 1));
            }
        } catch (IOException e) {
            System.out.println("Could not read users.db: " + e);
        }
    }

    @Override
    public synchronized Optional<String> findPasswordHash(String username) {
        return Optional.ofNullable(users.get(username));
    }

    @Override
    public synchronized void save(String username, String passwordHash) {
        users.put(username, passwordHash);
        StringBuilder sb = new StringBuilder();
        users.forEach((u, h) -> sb.append(u).append(':').append(h).append(System.lineSeparator()));
        try {
            Files.writeString(file, sb.toString(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("Could not write users.db: " + e);
        }
    }

    @Override
    public synchronized boolean isEmpty() { return users.isEmpty(); }
}
