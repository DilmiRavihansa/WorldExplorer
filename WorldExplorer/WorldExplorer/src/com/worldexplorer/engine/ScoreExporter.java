package com.worldexplorer.engine;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Interoperability: writes the leaderboard as JSON so other programs
 * (a website, a spreadsheet import, another game) can read it without knowing our text format.
 */
public final class ScoreExporter {
    private ScoreExporter() { }

    public static void exportJson(List<ScoreEntry> entries, Path target) {
        StringBuilder sb = new StringBuilder("{\n  \"game\": \"World Explorer\",\n  \"scores\": [\n");
        for (int i = 0; i < entries.size(); i++) {
            ScoreEntry e = entries.get(i);
            sb.append("    {\"rank\": ").append(i + 1)
                    .append(", \"player\": \"").append(escape(e.getPlayer()))
                    .append("\", \"score\": ").append(e.getScore()).append('}')
                    .append(i < entries.size() - 1 ? ",\n" : "\n");
        }
        sb.append("  ]\n}\n");
        try {
            Files.writeString(target, sb.toString(), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            System.out.println("Could not export leaderboard: " + ex);
        }
    }

    private static String escape(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
