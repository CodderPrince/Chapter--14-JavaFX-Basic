package application.controllers;

import javafx.fxml.FXML;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

public class GameScreenController {

    @FXML
    private Rectangle car; // Red car rectangle in FXML
    @FXML
    private Pane gamePane; // Pane from FXML to handle key events

    private double carSpeed = 5;

    @FXML
    public void initialize() {
        // Add a key listener to the game pane for car movement
        gamePane.setOnKeyPressed(this::handleKeyPressed);
        gamePane.setFocusTraversable(true); // Make sure it listens for key events
    }

    private void handleKeyPressed(KeyEvent event) {
        switch (event.getCode()) {
            case UP:
                car.setLayoutY(car.getLayoutY() - carSpeed);
                break;
            case DOWN:
                car.setLayoutY(car.getLayoutY() + carSpeed);
                break;
            case LEFT:
                car.setLayoutX(car.getLayoutX() - carSpeed);
                break;
            case RIGHT:
                car.setLayoutX(car.getLayoutX() + carSpeed);
                break;
            default:
                break;
        }
    }
}
