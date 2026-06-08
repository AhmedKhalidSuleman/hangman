package com.hangman.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.hangman.model.ScoreEntry;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.util.*;

/**
 * Service responsible for persisting and retrieving leaderboard scores.
 * Scores are stored as JSON in the user's home directory under ".hangman/scores.json".
 *
 * Uses Gson for JSON serialization.
 */
public class ScoreService {

    private static final String SCORES_DIR  = System.getProperty("user.home") + File.separator + ".hangman";
    private static final String SCORES_FILE = SCORES_DIR + File.separator + "scores.json";
    private static final int    MAX_ENTRIES = 20;

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private static ScoreService instance;

    private ScoreService() {
        // Ensure directory exists
        try {
            Files.createDirectories(Paths.get(SCORES_DIR));
        } catch (IOException e) {
            System.err.println("[ScoreService] Cannot create scores directory: " + e.getMessage());
        }
    }

    public static synchronized ScoreService getInstance() {
        if (instance == null) instance = new ScoreService();
        return instance;
    }

    /**
     * Save a new score entry and persist to disk.
     */
    public void saveScore(ScoreEntry entry) {
        List<ScoreEntry> scores = loadScores();
        scores.add(entry);
        Collections.sort(scores);
        if (scores.size() > MAX_ENTRIES) {
            scores = scores.subList(0, MAX_ENTRIES);
        }
        persist(scores);
    }

    /**
     * Load and return all scores, sorted descending by score.
     */
    public List<ScoreEntry> loadScores() {
        Path path = Paths.get(SCORES_FILE);
        if (!Files.exists(path)) return new ArrayList<>();
        try (Reader reader = Files.newBufferedReader(path)) {
            Type listType = new TypeToken<List<ScoreEntry>>() {}.getType();
            List<ScoreEntry> scores = gson.fromJson(reader, listType);
            return scores != null ? scores : new ArrayList<>();
        } catch (IOException | com.google.gson.JsonSyntaxException e) {
            System.err.println("[ScoreService] Error reading scores: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Clear all stored scores.
     */
    public void clearScores() {
        persist(new ArrayList<>());
    }

    private void persist(List<ScoreEntry> scores) {
        try (Writer writer = Files.newBufferedWriter(Paths.get(SCORES_FILE))) {
            gson.toJson(scores, writer);
        } catch (IOException e) {
            System.err.println("[ScoreService] Error writing scores: " + e.getMessage());
        }
    }
}
