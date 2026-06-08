package com.hangman.controller;

import com.hangman.app.HangmanApp;
import com.hangman.model.ScoreEntry;
import com.hangman.service.ScoreService;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for the leaderboard/scores screen.
 * Shows the top scores stored in ~/.hangman/scores.json.
 */
public class LeaderboardController implements Initializable {

    @FXML private TableView<ScoreEntry> tableView;
    @FXML private TableColumn<ScoreEntry, String> colName;
    @FXML private TableColumn<ScoreEntry, Integer> colScore;
    @FXML private TableColumn<ScoreEntry, String>  colDifficulty;
    @FXML private TableColumn<ScoreEntry, Integer> colStreak;
    @FXML private TableColumn<ScoreEntry, String>  colDate;
    @FXML private Button btnClear;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPlayerName()));
        colScore.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getScore()).asObject());
        colDifficulty.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDifficulty()));
        colStreak.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getStreak()).asObject());
        colDate.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDateTime()));

        // Center-align numeric columns
        colScore.setStyle("-fx-alignment: CENTER;");
        colStreak.setStyle("-fx-alignment: CENTER;");

        // Alternating row colors
        tableView.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(ScoreEntry item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setStyle("");
                } else if (getIndex() == 0) {
                    setStyle("-fx-background-color: #ffd70044;"); // gold tint for #1
                } else {
                    setStyle("");
                }
            }
        });

        loadData();
    }

    private void loadData() {
        List<ScoreEntry> scores = ScoreService.getInstance().loadScores();
        tableView.setItems(FXCollections.observableArrayList(scores));
        if (scores.isEmpty()) {
            tableView.setPlaceholder(new Label("Nessun punteggio salvato. Gioca una partita! 🎮"));
        }
    }

    @FXML
    private void onClearScores() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Sei sicuro di voler cancellare tutti i punteggi?",
                ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Conferma cancellazione");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                ScoreService.getInstance().clearScores();
                loadData();
            }
        });
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
