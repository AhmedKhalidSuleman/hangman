package com.hangman.app;

import com.hangman.controller.MenuController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.InputStream;
import java.net.URL;
import java.util.Objects;

/**
 * Main JavaFX Application class for the Hangman game.
 * Entry point of the application — loads the main menu FXML.
 */
public class HangmanApp extends Application {

    public static final String APP_TITLE = "🎯 Hangman – Il Gioco dell'Impiccato";
    public static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;

        URL fxmlUrl = getClass().getResource("/fxml/menu.fxml");
        if (fxmlUrl == null) {
            throw new RuntimeException("Cannot find /fxml/menu.fxml on classpath");
        }

        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Parent root = loader.load();

        Scene scene = new Scene(root, 900, 680);

        URL cssUrl = getClass().getResource("/css/style.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }

        stage.setTitle(APP_TITLE);
        stage.setScene(scene);
        stage.setMinWidth(860);
        stage.setMinHeight(640);
        stage.setResizable(true);

        // Try to set app icon
        InputStream iconStream = getClass().getResourceAsStream("/images/icon.png");
        if (iconStream != null) {
            stage.getIcons().add(new Image(iconStream));
        }

        stage.show();
    }

    /**
     * Switches the scene on the primary stage.
     *
     * @param fxmlPath path to the FXML resource (e.g. "/fxml/game.fxml")
     */
    public static void switchScene(String fxmlPath) throws Exception {
        URL fxmlUrl = HangmanApp.class.getResource(fxmlPath);
        if (fxmlUrl == null) {
            throw new RuntimeException("Cannot find FXML: " + fxmlPath);
        }
        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Parent root = loader.load();
        Scene scene = new Scene(root, 900, 680);

        URL cssUrl = HangmanApp.class.getResource("/css/style.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }

        primaryStage.setScene(scene);
    }

    /**
     * Switches scene and returns the controller for further configuration.
     */
    public static <T> T switchSceneAndGetController(String fxmlPath) throws Exception {
        URL fxmlUrl = HangmanApp.class.getResource(fxmlPath);
        if (fxmlUrl == null) {
            throw new RuntimeException("Cannot find FXML: " + fxmlPath);
        }
        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Parent root = loader.load();
        Scene scene = new Scene(root, 900, 680);

        URL cssUrl = HangmanApp.class.getResource("/css/style.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }

        primaryStage.setScene(scene);
        return loader.getController();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
