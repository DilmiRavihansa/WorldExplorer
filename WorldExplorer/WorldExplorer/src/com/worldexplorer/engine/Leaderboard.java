package com.worldexplorer.engine;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/** Stores scores in a text file (leaderboard.txt). Could be replaced by a database or a web service. */
public class Leaderboard implements ScoreRepository {
    private static final Path FILE = Paths.get("leaderboard.txt");

    @Override
    public synchronized void add(String player, int score) {
        try {
            Files.writeString(FILE, player.replace(",", " ") + "," + score + System.lineSeparator(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.out.println("Could not save score: " + e);
        }
    }

    @Override
    public synchronized List<ScoreEntry> top(int n) {
        List<ScoreEntry> rows = new ArrayList<>();
        try {
            if (Files.exists(FILE)) {
                for (String line : Files.readAllLines(FILE, StandardCharsets.UTF_8)) {
                    String[] p = line.split(",");
                    if (p.length != 2) continue;
                    try {
                        rows.add(new ScoreEntry(p[0].trim(), Integer.parseInt(p[1].trim())));
                    } catch (NumberFormatException ignored) {
                        // skip damaged line
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Could not read leaderboard: " + e);
        }
        rows.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));
        return new ArrayList<>(rows.subList(0, Math.min(n, rows.size())));
    }
}