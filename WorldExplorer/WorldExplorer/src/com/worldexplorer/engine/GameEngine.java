package com.worldexplorer.engine;

import java.nio.file.Paths;

/**
 * Each player has their own game engine: keeps score, level and the current question.
 * The engine contains NO Swing code and does not know where questions or scores come from:
 * those services are passed in through interfaces (low coupling).
 */
public class GameEngine {
    public static final int POINTS_PER_ANSWER = 10;
    public static final int POINTS_PER_LEVEL = 50;
    public static final int MIN_OPTIONS = 4;
    public static final int MAX_OPTIONS = 6;

    private final String thePlayer;
    private final String mode;
    private final QuestionProvider questions;
    private final BonusProvider bonuses;
    private final ScoreRepository scores;

    // Settings (config.properties)
    private final boolean bonusEnabled = Config.getBoolean("bonus.enabled", true);
    private final int bonusEvery = Math.max(1, Config.getInt("bonus.every", 5));
    private final int bonusPoints = Config.getInt("bonus.points", 20);
    private final int roundLength = Math.max(1, Config.getInt("round.length", 15));
    private final int secondsPerQuestion = Math.max(0, Config.getInt("question.seconds", 20));

    private Game current = null;
    private int score = 0;
    private int answered = 0;      // country questions answered (bonus rounds not counted)
    private int lastBonusAt = 0;   // value of 'answered' when the last bonus was offered
    private int lastPoints = 0;
    private int correctCount = 0;
    private int incorrectCount = 0;
    private boolean saved = false;

    public GameEngine(String player, String mode, QuestionProvider questions, BonusProvider bonuses,
                      ScoreRepository scores) {
        this.thePlayer = player;
        this.mode = mode;
        this.questions = questions;
        this.bonuses = bonuses;
        this.scores = scores;
    }

    /** Level goes up every 50 points: 1 = 0-49, 2 = 50-99, ... */
    public int getLevel() { return score / POINTS_PER_LEVEL + 1; }

    /** Higher levels show more answer options (4, 5, then 6). */
    public static int optionsForLevel(int level) { return Math.min(MIN_OPTIONS - 1 + level, MAX_OPTIONS); }

    /** Next question. After every few questions this is a Fish API bonus round (if available). */
    public Game nextGame() {
        if (bonusEnabled && answered > 0 && answered % bonusEvery == 0 && answered != lastBonusAt) {
            lastBonusAt = answered;
            Game bonus = bonuses.getBonusGame();
            if (bonus != null) {
                current = bonus;
                return current;
            }
        }
        current = questions.getRandomGame(mode, optionsForLevel(getLevel()));
        return current;
    }

    /** Checks the chosen option (-1 = no answer / time out). Correct answers add 10 points (bonus round: 20). */
    public boolean checkSolution(int i) {
        lastPoints = 0;
        boolean bonus = current != null && current.isBonus();
        if (!bonus) answered++;
        if (current != null && i == current.getSolution()) {
            lastPoints = bonus ? bonusPoints : POINTS_PER_ANSWER;
            score += lastPoints;
            correctCount++;
            return true;
        }
        incorrectCount++;
        return false;
    }

    /** True once the set number of country questions has been answered. */
    public boolean isRoundOver() { return answered >= roundLength; }

    public int getScore() { return score; }
    public int getLastPoints() { return lastPoints; }
    public String getPlayer() { return thePlayer; }
    public String getMode() { return mode; }
    public Game getCurrent() { return current; }
    public int getAnswered() { return answered; }
    public int getRoundLength() { return roundLength; }
    public int getBonusEvery() { return bonusEvery; }
    public int getBonusPoints() { return bonusPoints; }
    public int getSecondsPerQuestion() { return secondsPerQuestion; }
    public String getLastError() { return questions.getLastError(); }

    public GameSummary getSummary() {
        return new GameSummary(thePlayer, mode, score, getLevel(), correctCount, incorrectCount);
    }

    /** Saves the final score to the leaderboard (only once) and refreshes the JSON export. */
    public GameSummary endGame() {
        if (!saved) {
            saved = true;
            scores.add(thePlayer, score);
            ScoreExporter.exportJson(scores.top(100), Paths.get(Config.get("leaderboard.json", "leaderboard.json")));
        }
        return getSummary();
    }
}
