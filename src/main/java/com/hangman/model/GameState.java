package com.hangman.model;

import java.util.*;

/**
 * Core model representing the state of a single Hangman game session.
 * Manages the secret word, guessed letters, errors, score, streak, and hint.
 *
 * Design pattern: Plain Java Model (MVC) — no JavaFX dependencies here.
 */
public class GameState {

    // ── Configuration ──────────────────────────────────────────────────────────
    private final String secretWord;
    private final Difficulty difficulty;
    private final GameMode gameMode;

    // ── State ──────────────────────────────────────────────────────────────────
    private final Set<Character> guessedLetters = new LinkedHashSet<>();
    private final Set<Character> wrongLetters = new LinkedHashSet<>();
    private int errorCount = 0;
    private boolean hintUsed = false;
    private long startTimeMs;

    // ── Multiplayer ────────────────────────────────────────────────────────────
    private int currentPlayer = 1; // 1 or 2
    private final int[] playerScores = {0, 0};
    private final int[] playerWins   = {0, 0};
    private int roundNumber = 1;

    // ── Streak (single-player) ─────────────────────────────────────────────────
    private static int globalStreak = 0;
    private static int globalBestStreak = 0;

    // ── Constructor ────────────────────────────────────────────────────────────
    public GameState(String secretWord, Difficulty difficulty, GameMode gameMode) {
        this.secretWord = secretWord.toUpperCase(Locale.ROOT);
        this.difficulty = difficulty;
        this.gameMode   = gameMode;
        this.startTimeMs = System.currentTimeMillis();
    }

    // ── Core game logic ────────────────────────────────────────────────────────

    /**
     * Process a letter guess.
     * @return true if the letter is in the word, false otherwise
     */
    public boolean guess(char letter) {
        letter = Character.toUpperCase(letter);
        if (guessedLetters.contains(letter)) return true; // already guessed

        guessedLetters.add(letter);

        if (secretWord.indexOf(letter) >= 0) {
            return true;
        } else {
            wrongLetters.add(letter);
            errorCount++;
            return false;
        }
    }

    /**
     * Reveal a random unguessed letter from the secret word (hint).
     * @return the revealed character, or '\0' if none available
     */
    public char useHint() {
        if (hintUsed) return '\0';
        hintUsed = true;

        List<Character> hidden = new ArrayList<>();
        for (char c : secretWord.toCharArray()) {
            if (c != ' ' && !guessedLetters.contains(c)) {
                hidden.add(c);
            }
        }
        if (hidden.isEmpty()) return '\0';

        char revealed = hidden.get(new Random().nextInt(hidden.size()));
        guessedLetters.add(revealed);
        return revealed;
    }

    /**
     * Returns the masked word, e.g. "_ A _ G _ A N" for "HANGMAN".
     */
    public String getMaskedWord() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < secretWord.length(); i++) {
            char c = secretWord.charAt(i);
            if (c == ' ') {
                sb.append("  ");
            } else if (guessedLetters.contains(c)) {
                sb.append(c);
            } else {
                sb.append('_');
            }
            if (i < secretWord.length() - 1) sb.append(' ');
        }
        return sb.toString();
    }

    /** @return true if all letters have been guessed */
    public boolean isWordGuessed() {
        for (char c : secretWord.toCharArray()) {
            if (c != ' ' && !guessedLetters.contains(c)) return false;
        }
        return true;
    }

    /** @return true if the player has exceeded max allowed errors */
    public boolean isGameOver() {
        return errorCount >= difficulty.getMaxErrors();
    }

    /**
     * Compute the score for a win:
     * base points - (errors * penalty) - (hint penalty) + time bonus
     */
    public int computeWinScore() {
        int base       = difficulty.getPointsPerWin();
        int errPenalty = errorCount * 20;
        int hintPenalty= hintUsed ? 50 : 0;
        long elapsedSec= (System.currentTimeMillis() - startTimeMs) / 1000;
        int timeBonus  = (int) Math.max(0, 60 - elapsedSec) * 2; // up to 120 bonus
        return Math.max(10, base - errPenalty - hintPenalty + timeBonus);
    }

    // ── Multiplayer helpers ────────────────────────────────────────────────────

    public void addScoreToCurrentPlayer(int points) {
        playerScores[currentPlayer - 1] += points;
    }

    public void addWinToCurrentPlayer() {
        playerWins[currentPlayer - 1]++;
    }

    public void switchPlayer() {
        currentPlayer = (currentPlayer == 1) ? 2 : 1;
    }

    public void nextRound() {
        roundNumber++;
        currentPlayer = (roundNumber % 2 == 0) ? 2 : 1; // alternate who starts
    }

    // ── Streak ────────────────────────────────────────────────────────────────

    public static void incrementStreak() {
        globalStreak++;
        if (globalStreak > globalBestStreak) globalBestStreak = globalStreak;
    }

    public static void resetStreak() {
        globalStreak = 0;
    }

    public static int getGlobalStreak()     { return globalStreak; }
    public static int getGlobalBestStreak() { return globalBestStreak; }

    // ── Getters ────────────────────────────────────────────────────────────────

    public String     getSecretWord()       { return secretWord; }
    public Difficulty getDifficulty()       { return difficulty; }
    public GameMode   getGameMode()         { return gameMode; }
    public Set<Character> getGuessedLetters() { return Collections.unmodifiableSet(guessedLetters); }
    public Set<Character> getWrongLetters()   { return Collections.unmodifiableSet(wrongLetters); }
    public int        getErrorCount()       { return errorCount; }
    public int        getMaxErrors()        { return difficulty.getMaxErrors(); }
    public boolean    isHintUsed()          { return hintUsed; }
    public int        getCurrentPlayer()    { return currentPlayer; }
    public int[]      getPlayerScores()     { return playerScores; }
    public int[]      getPlayerWins()       { return playerWins; }
    public int        getRoundNumber()      { return roundNumber; }
    public long       getStartTimeMs()      { return startTimeMs; }
    public void       setStartTimeMs(long t){ this.startTimeMs = t; }
}
