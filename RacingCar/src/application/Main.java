package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/application/views/MainMenu.fxml"));
            Scene scene = new Scene(root, 800, 600); // Adjust the width and height
            scene.getStylesheets().add(getClass().getResource("/application/resources/application.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.setTitle("RaceTrack Madness");
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
