package com.worldexplorer.engine;

/** One line of the leaderboard. */
public class ScoreEntry {
    private final String player;
    private final int score;

    public ScoreEntry(String player, int score) {
        this.player = player;
        this.score = score;
    }

    public String getPlayer() { return player; }
    public int getScore() { return score; }
}