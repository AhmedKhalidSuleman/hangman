package com.hangman.app;

/**
 * Launcher class used as the fat-jar entry point.
 * Needed because JavaFX requires the main class to NOT extend Application
 * when launched from a shaded/fat jar without module-info.
 */
public class Launcher {
    public static void main(String[] args) {
        HangmanApp.main(args);
    }
}
