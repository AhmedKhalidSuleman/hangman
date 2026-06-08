package com.hangman.controller;

import com.hangman.app.HangmanApp;
import com.hangman.model.Difficulty;
import com.hangman.model.GameMode;
import com.hangman.model.GameState;
import com.hangman.model.WordBank;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the main menu screen.
 * Handles difficulty/mode selection and navigation to game or leaderboard.
 */
public class MenuController implements Initializable {

    @FXML private VBox rootVBox;
    @FXML private Label titleLabel;
    @FXML private Label streakLabel;
    @FXML private ToggleGroup difficultyGroup;
    @FXML private ToggleGroup modeGroup;
    @FXML private RadioButton rbEasy;
    @FXML private RadioButton rbMedium;
    @FXML private RadioButton rbHard;
    @FXML private RadioButton rbSingle;
    @FXML private RadioButton rbMulti;
    @FXML private TextField player1NameField;
    @FXML private TextField player2NameField;
    @FXML private Label player2NameLabel;
    @FXML private Button btnPlay;
    @FXML private Button btnLeaderboard;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        animateTitle();
        updateStreakLabel();

        // Show/hide Player 2 name based on mode
        rbMulti.selectedProperty().addListener((obs, oldVal, newVal) -> {
            player2NameField.setVisible(newVal);
            player2NameLabel.setVisible(newVal);
        });
        player2NameField.setVisible(false);
        player2NameLabel.setVisible(false);
    }

    private void animateTitle() {
        titleLabel.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.millis(1200), titleLabel);
        ft.setFromValue(0);
        ft.setToValue(1);

        ScaleTransition st = new ScaleTransition(Duration.millis(1200), titleLabel);
        st.setFromX(0.6);
        st.setToX(1.0);
        st.setFromY(0.6);
        st.setToY(1.0);

        ParallelTransition pt = new ParallelTransition(ft, st);
        pt.play();
    }

    private void updateStreakLabel() {
        int streak = GameState.getGlobalStreak();
        int best   = GameState.getGlobalBestStreak();
        if (streak > 0) {
            streakLabel.setText("🔥 Streak attuale: " + streak + "  |  🏆 Best: " + best);
        } else {
            streakLabel.setText("🏆 Best streak: " + best);
        }
    }

    @FXML
    private void onPlay() {
        Difficulty difficulty = getSelectedDifficulty();
        GameMode   mode       = getSelectedMode();

        String p1Name = player1NameField.getText().isBlank() ? "Giocatore 1" : player1NameField.getText().trim();
        String p2Name = player2NameField.getText().isBlank() ? "Giocatore 2" : player2NameField.getText().trim();

        try {
            if (mode == GameMode.TWO_PLAYERS) {
                // Go to multiplayer word-entry screen
                MultiSetupController ctrl = HangmanApp.switchSceneAndGetController("/fxml/multi_setup.fxml");
                ctrl.setup(p1Name, p2Name, difficulty);
            } else {
                // Single player: pick random word and go to game
                String word = WordBank.getRandomWord(difficulty);
                GameState gs = new GameState(word, difficulty, mode);
                GameController ctrl = HangmanApp.switchSceneAndGetController("/fxml/game.fxml");
                ctrl.initGame(gs, p1Name, "");
            }
        } catch (Exception e) {
            showError("Errore avvio partita: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void onLeaderboard() {
        try {
            HangmanApp.switchScene("/fxml/leaderboard.fxml");
        } catch (Exception e) {
            showError("Errore apertura classifica: " + e.getMessage());
        }
    }

    private Difficulty getSelectedDifficulty() {
        RadioButton selected = (RadioButton) difficultyGroup.getSelectedToggle();
        if (selected == null || selected == rbEasy)   return Difficulty.EASY;
        if (selected == rbMedium) return Difficulty.MEDIUM;
        if (selected == rbHard)   return Difficulty.HARD;
        return Difficulty.MEDIUM;
    }

    private GameMode getSelectedMode() {
        RadioButton selected = (RadioButton) modeGroup.getSelectedToggle();
        return (selected == rbMulti) ? GameMode.TWO_PLAYERS : GameMode.SINGLE_PLAYER;
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.setTitle("Errore");
        alert.showAndWait();
    }
}
