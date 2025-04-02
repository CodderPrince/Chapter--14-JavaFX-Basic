package application.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import java.io.IOException;

public class MainMenuController {

    @FXML
    private Button playButton;

    @FXML
    private Button settingsButton;

    @FXML
    private Button exitButton;

    @FXML
    private void handlePlayGame() {
        try {
            Parent gameRoot = FXMLLoader.load(getClass().getResource("/application/views/GameScreen.fxml"));
            Stage stage = (Stage) playButton.getScene().getWindow();
            stage.setScene(new Scene(gameRoot, 800, 600));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSettings() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Settings");
        alert.setHeaderText("Settings");
        alert.setContentText("Settings are not implemented yet!");
        alert.showAndWait();
    }

    @FXML
    private void handleExit() {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }
}
