package com.worldexplorer.engine;

/** Read-only result of a finished round, handed to the results screen. */
public class GameSummary {
    private final String player, mode;
    private final int score, level, correct, incorrect;

    public GameSummary(String player, String mode, int score, int level, int correct, int incorrect) {
        this.player = player;
        this.mode = mode;
        this.score = score;
        this.level = level;
        this.correct = correct;
        this.incorrect = incorrect;
    }

    public String getPlayer() { return player; }
    public String getMode() { return mode; }
    public int getScore() { return score; }
    public int getLevel() { return level; }
    public int getCorrect() { return correct; }
    public int getIncorrect() { return incorrect; }
    public int getAnswered() { return correct + incorrect; }

    /** Percentage of correct answers (0-100). */
    public int getAccuracy() {
        return getAnswered() == 0 ? 0 : Math.round(100f * correct / getAnswered());
    }
}
