package com.worldexplorer.engine;

/** What the engine needs from a source of country questions (REST Countries today, anything tomorrow). */
public interface QuestionProvider {
    /** True when the service has everything it needs (for example an API key). */
    boolean isConfigured();

    /** Gives the service its credentials and clears cached data. */
    void configure(String apiKey);

    /** Builds one question, or returns null if it could not (see getLastError). */
    Game getRandomGame(String mode, int numOptions);

    /** Human readable reason for the last failure. */
    String getLastError();
}
