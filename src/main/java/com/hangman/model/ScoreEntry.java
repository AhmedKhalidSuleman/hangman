package com.hangman.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Immutable record representing a single leaderboard entry.
 */
public class ScoreEntry implements Comparable<ScoreEntry> {

    private final String playerName;
    private final int score;
    private final String difficulty;
    private final String dateTime;
    private final int streak;

    public ScoreEntry(String playerName, int score, String difficulty, int streak) {
        this.playerName = playerName;
        this.score      = score;
        this.difficulty = difficulty;
        this.streak     = streak;
        this.dateTime   = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    // Deserialization constructor (Gson)
    public ScoreEntry(String playerName, int score, String difficulty, int streak, String dateTime) {
        this.playerName = playerName;
        this.score      = score;
        this.difficulty = difficulty;
        this.streak     = streak;
        this.dateTime   = dateTime;
    }

    public String getPlayerName() { return playerName; }
    public int    getScore()      { return score; }
    public String getDifficulty() { return difficulty; }
    public String getDateTime()   { return dateTime; }
    public int    getStreak()     { return streak; }

    @Override
    public int compareTo(ScoreEntry other) {
        return Integer.compare(other.score, this.score); // descending
    }

    @Override
    public String toString() {
        return String.format("%s | %d pts | %s | streak:%d | %s",
                playerName, score, difficulty, streak, dateTime);
    }
}
