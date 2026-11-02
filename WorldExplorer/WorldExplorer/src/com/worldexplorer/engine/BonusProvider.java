package com.worldexplorer.engine;

/** A source of bonus questions (the Fish API). */
public interface BonusProvider {
    /** Returns a bonus question or null if the service is unavailable. */
    Game getBonusGame();
}
