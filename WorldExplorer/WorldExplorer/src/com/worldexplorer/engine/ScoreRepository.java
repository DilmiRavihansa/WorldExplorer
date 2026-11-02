package com.worldexplorer.engine;

import java.util.List;

/** Where final scores are stored. The engine only knows this interface, not the file format. */
public interface ScoreRepository {
    void add(String player, int score);

    /** Best scores, highest first. */
    List<ScoreEntry> top(int n);
}
