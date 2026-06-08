package com.hangman.model;

import java.util.*;

/**
 * Provides the word pool for the game.
 * Words are categorized. A random word is picked based on difficulty constraints.
 *
 * Extends the base word list with Italian and English words for variety.
 */
public class WordBank {

    private static final List<String> ALL_WORDS = Arrays.asList(
            // 3-5 letters (Easy)
            "GATO", "SOLE", "LUNA", "MARE", "ARIA", "MELA", "PANE", "CANE", "ROSA", "PERA",
            "LAGO", "NEVE", "VELA", "BICI", "FICO", "UOVO", "ALTO", "BUCO", "DADO", "EROE",
            "FISH", "BIRD", "TREE", "BOOK", "DOOR", "RAIN", "FIRE", "WIND", "GOLD", "KING",
            // 5-8 letters (Medium)
            "PIZZA", "GIOCO", "SCUOLA", "MUSICA", "FIORE", "ALBERO", "STRADA", "PRANZO",
            "AMICO", "ESTATE", "INVERNO", "PRIMAVERA", "AUTUNNO", "CASELLO", "PALAZZO",
            "FORMULA", "MONITOR", "BATTERY", "PROGRAM", "DIAMOND", "FREEDOM", "JOURNEY",
            "SCIENCE", "VICTORY", "MYSTERY", "VOLCANO", "PENGUIN", "DOLPHIN", "PANTHER",
            "CASTELLO", "DRAGO", "SPIAGGIA", "MONTAGNA", "CARTELLA",
            // 8+ letters (Hard)
            "INTELLIGENZA", "PROGRAMMAZIONE", "AVVENTURA", "ASTRONAUTA", "INFORMATICA",
            "IMMAGINAZIONE", "RESPONSABILITA", "COMUNICAZIONE", "ARCHITETTURA", "BIODIVERSITA",
            "PSYCHOLOGY", "MATHEMATICS", "PHILOSOPHY", "ARCHITECTURE", "CIVILIZATION",
            "EXTRAORDINARY", "CONSTITUTION", "DEMONSTRATION", "INDEPENDENCE", "ADMINISTRATION",
            "TEMPERATURE", "UNDERGROUND", "CHAMPIONSHIP", "ENVIRONMENT", "THUNDERSTORM",
            "CAMPIONATO", "LABORATORIO", "CLASSIFICAZIONE", "COLLABORAZIONE", "TRASCENDENZA",
            "FOTOSINTESI", "MAGNETISMO", "RIVOLUZIONE", "ILLUSTRAZIONE", "ORCHESTRAZIONE"
    );

    private WordBank() {}

    /**
     * Returns a random word matching the difficulty constraints.
     */
    public static String getRandomWord(Difficulty difficulty) {
        List<String> candidates = new ArrayList<>();
        for (String w : ALL_WORDS) {
            if (w.length() >= difficulty.getMinWordLength()
                    && w.length() <= difficulty.getMaxWordLength()) {
                candidates.add(w);
            }
        }
        if (candidates.isEmpty()) {
            // Fallback: just return any random word
            candidates.addAll(ALL_WORDS);
        }
        return candidates.get(new Random().nextInt(candidates.size()));
    }

    /**
     * In multiplayer mode, one player types the secret word for the other.
     * This method validates the input.
     */
    public static boolean isValidCustomWord(String word) {
        if (word == null || word.isBlank()) return false;
        String trimmed = word.trim();
        return trimmed.length() >= 2 && trimmed.matches("[A-Za-zÀ-ÿ ]+");
    }

    public static List<String> getAllWords() {
        return Collections.unmodifiableList(ALL_WORDS);
    }
}
