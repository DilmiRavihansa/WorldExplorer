package com.worldexplorer.engine;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class Config {
    private static final String NAME = "config.properties";
    private static Path resolved;

    /**
     * Finds config.properties no matter how the game was started (run.bat, an IDE, a jar...).
     * Looks in the working folder, its WorldExplorer subfolder, then next to the compiled classes and their parents.
     */
    private static synchronized Path file() {
        if (resolved != null && Files.exists(resolved)) return resolved;
        java.util.List<Path> dirs = new java.util.ArrayList<>();
        Path cwd = Paths.get("").toAbsolutePath();
        dirs.add(cwd);
        dirs.add(cwd.resolve("WorldExplorer"));
        for (Path d = cwd.getParent(); d != null && dirs.size() < 6; d = d.getParent()) dirs.add(d);
        try {
            Path code = Paths.get(Config.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            if (Files.isRegularFile(code)) code = code.getParent();
            for (int i = 0; code != null && i < 5; i++, code = code.getParent()) {
                dirs.add(code);
                dirs.add(code.resolve("WorldExplorer"));
            }
        } catch (Exception e) { /* fall through to the working folder */ }
        for (Path d : dirs) {
            Path f = d.resolve(NAME);
            if (Files.isRegularFile(f)) { resolved = f; return f; }
        }
        return cwd.resolve(NAME);
    }

    private static Properties load() {
        Properties p = new Properties();
        try {
            if (Files.exists(file())) {
                try (InputStream in = Files.newInputStream(file())) {
                    p.load(in);
                }
            }
        } catch (Exception e) {
            System.out.println("Could not read config.properties: " + e);
        }
        return p;
    }

    /** Path of the config file in use (for error messages). */
    public static String describe() { return file().toString(); }

    public static String get(String key, String defaultValue) {
        String v = load().getProperty(key);
        return (v == null || v.isBlank()) ? defaultValue : v.trim();
    }

    public static int getInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(get(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        return Boolean.parseBoolean(get(key, String.valueOf(defaultValue)));
    }

    /** Stores one setting, keeping all the others. */
    public static synchronized void set(String key, String value) {
        try {
            Properties p = load();
            p.setProperty(key, value);
            try (OutputStream out = Files.newOutputStream(file())) {
                p.store(out, "World Explorer configuration");
            }
        } catch (Exception e) {
            System.out.println("Could not save config.properties: " + e);
        }
    }
}
