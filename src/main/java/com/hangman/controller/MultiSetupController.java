package com.hangman.controller;

import com.hangman.app.HangmanApp;
import com.hangman.model.Difficulty;
import com.hangman.model.GameMode;
import com.hangman.model.GameState;
import com.hangman.model.WordBank;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the multiplayer word-entry screen.
 * Player 1 secretly types a word for Player 2 to guess (and vice versa per round).
 */
public class MultiSetupController implements Initializable {

    @FXML private Label instructionLabel;
    @FXML private Label playerLabel;
    @FXML private PasswordField wordField;  // PasswordField hides input
    @FXML private Label errorLabel;
    @FXML private Button btnStart;
    @FXML private Button btnBack;

    private String player1Name;
    private String player2Name;
    private Difficulty difficulty;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        errorLabel.setVisible(false);
    }

    /**
     * Called by MenuController to pass data before the screen is shown.
     */
    public void setup(String p1, String p2, Difficulty diff) {
        this.player1Name = p1;
        this.player2Name = p2;
        this.difficulty  = diff;
        playerLabel.setText("👤 " + p1 + " — inserisci la parola segreta per " + p2 + ":");
        instructionLabel.setText(
                "Modalità Due Giocatori\n" +
                "Difficoltà: " + diff.getLabel() + "\n\n" +
                p1 + " digita una parola che " + p2 + " dovrà indovinare.\n" +
                "Il campo è nascosto così nessuno può vedere cosa scrivi.\n" +
                "Poi passa il dispositivo a " + p2 + "!");
    }

    @FXML
    private void onStart() {
        String raw = wordField.getText().trim().toUpperCase();
        if (!WordBank.isValidCustomWord(raw)) {
            errorLabel.setText("⚠ Parola non valida! Usa solo lettere, min 2 caratteri.");
            errorLabel.setVisible(true);
            return;
        }
        errorLabel.setVisible(false);

        GameState gs = new GameState(raw, difficulty, GameMode.TWO_PLAYERS);
        try {
            GameController ctrl = HangmanApp.switchSceneAndGetController("/fxml/game.fxml");
            ctrl.initGame(gs, player1Name, player2Name);
        } catch (Exception e) {
            errorLabel.setText("Errore: " + e.getMessage());
            errorLabel.setVisible(true);
        }
    }

    @FXML
    private void onBack() {
        try {
            HangmanApp.switchScene("/fxml/menu.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
