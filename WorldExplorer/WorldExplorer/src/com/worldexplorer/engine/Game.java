package com.worldexplorer.engine;

import java.awt.image.BufferedImage;
import java.util.List;

/**
 * One question: a text, an optional image (flag), the answer options and
 * the index of the correct option.
 */
public class Game {
    private final String question;
    private final BufferedImage image;
    private final List<String> options;
    private final int solution;
    private final boolean bonus;

    public Game(String question, BufferedImage image, List<String> options, int solution)
    {
        this(question, image, options, solution, false);
    }

    public Game(String question, BufferedImage image, List<String> options, int solution, boolean bonus)
    {
        this.bonus = bonus;
        this.question = question;
        this.image = image;
        this.options = options;
        this.solution = solution;
    }

    public String getQuestion()
    {
        return question;
    }
    public BufferedImage getImage()
    {
        return image;
    }
    public List<String> getOptions()
    {
        return options;
    }
    public int getSolution()
    {
        return solution;
    }
    /** True for the Fish API bonus round. */
    public boolean isBonus()
    {
        return bonus;
    }
    public String getCorrectAnswer()
    {
        return options.get(solution);
    }
}
