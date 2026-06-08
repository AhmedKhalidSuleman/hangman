package com.hangman.controller;

import com.hangman.app.HangmanApp;
import com.hangman.model.*;
import com.hangman.service.ScoreService;
import com.hangman.view.HangmanCanvas;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.net.URL;
import java.util.*;

/**
 * Main game controller. Handles the gameplay loop for both single and multiplayer.
 *
 * Responsibilities:
 * - Rendering the hangman canvas via HangmanCanvas
 * - Handling keyboard and on-screen letter buttons
 * - Updating the UI on each guess (masked word, errors, score)
 * - Triggering win/lose dialogs and score persistence
 */
public class GameController implements Initializable {

    // ── FXML nodes ──────────────────────────────────────────────────────────────
    @FXML private BorderPane rootPane;
    @FXML private HangmanCanvas hangmanCanvas;   // custom Canvas node
    @FXML private Label maskedWordLabel;
    @FXML private Label difficultyLabel;
    @FXML private Label errorLabel;
    @FXML private Label scoreLabel;
    @FXML private Label streakLabel;
    @FXML private Label playerTurnLabel;
    @FXML private Label p1ScoreLabel;
    @FXML private Label p2ScoreLabel;
    @FXML private VBox multiScoreBox;
    @FXML private FlowPane keyboardPane;
    @FXML private Button btnHint;
    @FXML private Button btnNewGame;
    @FXML private Button btnMenu;

    // ── State ───────────────────────────────────────────────────────────────────
    private GameState gameState;
    private String player1Name;
    private String player2Name;
    private final Map<Character, Button> keyButtons = new LinkedHashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        buildKeyboard();
    }

    /**
     * Called by the previous controller to inject the GameState.
     */
    public void initGame(GameState gs, String p1Name, String p2Name) {
        this.gameState   = gs;
        this.player1Name = p1Name;
        this.player2Name = p2Name.isBlank() ? "Giocatore 2" : p2Name;

        boolean isMulti = gs.getGameMode() == GameMode.TWO_PLAYERS;
        multiScoreBox.setVisible(isMulti);
        multiScoreBox.setManaged(isMulti);

        refreshUI();
        hangmanCanvas.draw(0, gs.getMaxErrors());
        rootPane.requestFocus();

        // Keyboard shortcut: type letters
        rootPane.setOnKeyPressed(this::handleKeyPress);
    }

    // ── Keyboard ────────────────────────────────────────────────────────────────

    private void buildKeyboard() {
        String rows = "QWERTYUIOP|ASDFGHJKL|ZXCVBNM";
        keyboardPane.getChildren().clear();
        keyButtons.clear();

        for (String row : rows.split("\\|")) {
            HBox hbox = new HBox(5);
            hbox.setPadding(new Insets(2));
            for (char c : row.toCharArray()) {
                Button btn = new Button(String.valueOf(c));
                btn.getStyleClass().add("key-button");
                btn.setPrefWidth(44);
                btn.setPrefHeight(44);
                btn.setOnAction(e -> onLetterGuessed(c));
                keyButtons.put(c, btn);
                hbox.getChildren().add(btn);
            }
            keyboardPane.getChildren().add(hbox);
        }
    }

    private void handleKeyPress(KeyEvent event) {
        String text = event.getText().toUpperCase();
        if (text.length() == 1) {
            char c = text.charAt(0);
            if (c >= 'A' && c <= 'Z') {
                onLetterGuessed(c);
            }
        }
    }

    // ── Core guess logic ────────────────────────────────────────────────────────

    private void onLetterGuessed(char letter) {
        if (gameState.isGameOver() || gameState.isWordGuessed()) return;
        if (gameState.getGuessedLetters().contains(letter)) return;

        boolean correct = gameState.guess(letter);
        updateKeyButton(letter, correct);
        refreshUI();
        hangmanCanvas.draw(gameState.getErrorCount(), gameState.getMaxErrors());

        animateMaskedWord(correct);

        if (gameState.isWordGuessed()) {
            onWin();
        } else if (gameState.isGameOver()) {
            onLoss();
        }
    }

    private void updateKeyButton(char letter, boolean correct) {
        Button btn = keyButtons.get(letter);
        if (btn != null) {
            btn.setDisable(true);
            btn.getStyleClass().removeAll("key-correct", "key-wrong");
            btn.getStyleClass().add(correct ? "key-correct" : "key-wrong");
        }
    }

    // ── Win / Loss ──────────────────────────────────────────────────────────────

    private void onWin() {
        int points = gameState.computeWinScore();
        gameState.addScoreToCurrentPlayer(points);
        gameState.addWinToCurrentPlayer();

        if (gameState.getGameMode() == GameMode.SINGLE_PLAYER) {
            GameState.incrementStreak();
        }

        // Save score
        String currentName = getCurrentPlayerName();
        ScoreService.getInstance().saveScore(
                new ScoreEntry(currentName, points, gameState.getDifficulty().name(),
                               GameState.getGlobalStreak()));

        hangmanCanvas.drawWin();

        String msg = "🎉 " + currentName + " ha vinto!\n\n" +
                     "Parola: " + gameState.getSecretWord() + "\n" +
                     "Punteggio: +" + points + " punti\n" +
                     (gameState.getGameMode() == GameMode.SINGLE_PLAYER
                        ? "🔥 Streak: " + GameState.getGlobalStreak()
                        : "Punteggio " + player1Name + ": " + gameState.getPlayerScores()[0] +
                          "\nPunteggio " + player2Name + ": " + gameState.getPlayerScores()[1]);

        refreshUI();
        disableKeyboard();
        showResultDialog("Vittoria! 🏆", msg, true);
    }

    private void onLoss() {
        if (gameState.getGameMode() == GameMode.SINGLE_PLAYER) {
            GameState.resetStreak();
        }
        hangmanCanvas.draw(gameState.getMaxErrors(), gameState.getMaxErrors());

        String msg = "💀 " + getCurrentPlayerName() + " ha perso!\n\n" +
                     "La parola era: " + gameState.getSecretWord() + "\n" +
                     (gameState.getGameMode() == GameMode.SINGLE_PLAYER
                        ? "🔥 Streak azzerata!"
                        : "Punteggio " + player1Name + ": " + gameState.getPlayerScores()[0] +
                          "\nPunteggio " + player2Name + ": " + gameState.getPlayerScores()[1]);

        refreshUI();
        disableKeyboard();
        showResultDialog("Game Over 💀", msg, false);
    }

    private void showResultDialog(String title, String content, boolean won) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        ButtonType btnNewRound = new ButtonType("🔁 Nuova partita");
        ButtonType btnMenu     = new ButtonType("🏠 Menu principale");
        alert.getButtonTypes().setAll(btnNewRound, btnMenu);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == btnNewRound) {
            onNewGame();
        } else {
            onMenu();
        }
    }

    // ── Hint ─────────────────────────────────────────────────────────────────────

    @FXML
    private void onHint() {
        if (gameState.isHintUsed()) return;
        char revealed = gameState.useHint();
        if (revealed != '\0') {
            updateKeyButton(revealed, true);
            refreshUI();
            hangmanCanvas.draw(gameState.getErrorCount(), gameState.getMaxErrors());
            btnHint.setDisable(true);
            btnHint.setText("💡 Suggerimento usato");

            // Shake the masked word label to attract attention
            animateMaskedWord(true);

            if (gameState.isWordGuessed()) onWin();
        }
    }

    // ── Navigation ───────────────────────────────────────────────────────────────

    @FXML
    private void onNewGame() {
        try {
            if (gameState.getGameMode() == GameMode.TWO_PLAYERS) {
                // Ask Player 2 to enter a word for Player 1 now
                MultiSetupController ctrl = HangmanApp.switchSceneAndGetController("/fxml/multi_setup.fxml");
                // Swap players for the new round
                ctrl.setup(player2Name, player1Name, gameState.getDifficulty());
            } else {
                String word = WordBank.getRandomWord(gameState.getDifficulty());
                GameState newGs = new GameState(word, gameState.getDifficulty(), gameState.getGameMode());
                GameController ctrl = HangmanApp.switchSceneAndGetController("/fxml/game.fxml");
                ctrl.initGame(newGs, player1Name, player2Name);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onMenu() {
        try {
            HangmanApp.switchScene("/fxml/menu.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ── UI refresh ───────────────────────────────────────────────────────────────

    private void refreshUI() {
        maskedWordLabel.setText(gameState.getMaskedWord());
        difficultyLabel.setText("Difficoltà: " + gameState.getDifficulty().getLabel());

        int maxErr = gameState.getMaxErrors();
        int curErr = gameState.getErrorCount();
        errorLabel.setText("Errori: " + curErr + " / " + maxErr);

        // Color errors red when close
        if (curErr >= maxErr - 1) {
            errorLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
        } else if (curErr >= maxErr / 2) {
            errorLabel.setStyle("-fx-text-fill: #e67e22; -fx-font-weight: bold;");
        } else {
            errorLabel.setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold;");
        }

        int streak = GameState.getGlobalStreak();
        streakLabel.setText(streak > 0 ? "🔥 Streak: " + streak : "");

        boolean isMulti = gameState.getGameMode() == GameMode.TWO_PLAYERS;
        if (isMulti) {
            playerTurnLabel.setText("👤 Turno di: " + getCurrentPlayerName());
            p1ScoreLabel.setText(player1Name + ": " + gameState.getPlayerScores()[0] + " pt  (wins: " + gameState.getPlayerWins()[0] + ")");
            p2ScoreLabel.setText(player2Name + ": " + gameState.getPlayerScores()[1] + " pt  (wins: " + gameState.getPlayerWins()[1] + ")");
        } else {
            playerTurnLabel.setText("👤 " + player1Name);
        }

        scoreLabel.setText("Punteggio: " + (gameState.getPlayerScores()[gameState.getCurrentPlayer() - 1]));

        btnHint.setDisable(gameState.isHintUsed());
        if (gameState.isHintUsed()) btnHint.setText("💡 Suggerimento usato");
    }

    private void disableKeyboard() {
        keyButtons.values().forEach(b -> b.setDisable(true));
    }

    private void animateMaskedWord(boolean correct) {
        if (correct) {
            // Pulse green
            ScaleTransition st = new ScaleTransition(Duration.millis(150), maskedWordLabel);
            st.setFromX(1.0); st.setToX(1.08);
            st.setFromY(1.0); st.setToY(1.08);
            st.setAutoReverse(true);
            st.setCycleCount(2);
            st.play();
        } else {
            // Shake red
            TranslateTransition tt = new TranslateTransition(Duration.millis(60), maskedWordLabel);
            tt.setFromX(0); tt.setToX(-10);
            tt.setAutoReverse(true);
            tt.setCycleCount(4);
            tt.play();
        }
    }

    private String getCurrentPlayerName() {
        return gameState.getCurrentPlayer() == 1 ? player1Name : player2Name;
    }
}
