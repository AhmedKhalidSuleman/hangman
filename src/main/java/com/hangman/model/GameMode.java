package com.hangman.model;

/**
 * Game mode: single player vs two-player (multiplayer on same machine).
 */
public enum GameMode {
    SINGLE_PLAYER("Giocatore Singolo"),
    TWO_PLAYERS("Due Giocatori (Multiplayer)");

    private final String label;

    GameMode(String label) {
        this.label = label;
    }

    public String getLabel() { return label; }

    @Override
    public String toString() { return label; }
}
