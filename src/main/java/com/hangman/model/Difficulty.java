package com.hangman.model;

/**
 * Enum representing the difficulty levels of the game.
 * Each level defines:
 * - maxErrors: how many wrong guesses are allowed
 * - minWordLength / maxWordLength: filtering for the word pool
 * - label: display name
 * - pointsPerWin: base points awarded for winning
 */
public enum Difficulty {

    EASY("Facile 😊", 8, 3, 6, 100),
    MEDIUM("Medio 😐", 6, 5, 9, 200),
    HARD("Difficile 😈", 4, 8, 20, 400);

    private final String label;
    private final int maxErrors;
    private final int minWordLength;
    private final int maxWordLength;
    private final int pointsPerWin;

    Difficulty(String label, int maxErrors, int minWordLength, int maxWordLength, int pointsPerWin) {
        this.label = label;
        this.maxErrors = maxErrors;
        this.minWordLength = minWordLength;
        this.maxWordLength = maxWordLength;
        this.pointsPerWin = pointsPerWin;
    }

    public String getLabel() { return label; }
    public int getMaxErrors() { return maxErrors; }
    public int getMinWordLength() { return minWordLength; }
    public int getMaxWordLength() { return maxWordLength; }
    public int getPointsPerWin() { return pointsPerWin; }

    @Override
    public String toString() { return label; }
}
