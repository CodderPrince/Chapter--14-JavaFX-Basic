package application;

import javafx.animation.AnimationTimer;
import javafx.animation.ScaleTransition;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.util.Duration;

import java.util.Collections;
import java.util.ArrayList;

public class MemoryMatchGame {

    private int gridSize = 4;
    private Button[][] buttons;
    private String[][] values;
    private Button firstSelected = null;
    private Button secondSelected = null;
    private boolean waiting = false;
    private int matchedPairs = 0;
    private long startTime;
    private Text timerText = new Text("Time: 0 seconds");

    private Button easyButton;
    private Button mediumButton;
    private Button hardButton;

    public void start(Stage primaryStage) {
        VBox root = new VBox();
        root.setAlignment(Pos.CENTER);
        root.setSpacing(20);
        root.setStyle("-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #87cefa, #4682b4);");

        Text difficultyText = new Text("Choose Difficulty Level");
        difficultyText.setFont(Font.font("Arial", FontWeight.BOLD, 50));
        difficultyText.setFill(Color.WHITE);

        HBox difficultyButtons = new HBox(10);
        difficultyButtons.setAlignment(Pos.CENTER);

        easyButton = createStyledButton("Easy", "#8a2be2", "#4b0082"); // Blue Violet and Indigo

        mediumButton = createStyledButton("Medium", "#ffa500", "#ff8c00");
        hardButton = createStyledButton("Hard", "#ff6347", "#ff4500");

        easyButton.setOnAction(e -> selectDifficulty("Easy", 4));
        mediumButton.setOnAction(e -> selectDifficulty("Medium", 6));
        hardButton.setOnAction(e -> selectDifficulty("Hard", 8));

        difficultyButtons.getChildren().addAll(easyButton, mediumButton, hardButton);

        Button runButton = createStyledButton("Run", "#006400", "#228b22");
        runButton.setOnAction(e -> startGame(primaryStage));

        Button exitButton = createStyledButton("Exit", "#8b0000", "#ff0000");
        exitButton.setOnAction(e -> primaryStage.close());

        root.getChildren().addAll(difficultyText, difficultyButtons, runButton, exitButton);

        Scene scene = new Scene(root, 700, 500); //Reduced starting size
        primaryStage.setScene(scene);
        primaryStage.setTitle("Memory Match Game");
        primaryStage.show();
    }

    private void selectDifficulty(String level, int size) {
        gridSize = size;
        easyButton.setStyle("-fx-background-color: linear-gradient(#8a2be2, #4b0082); -fx-border-color: transparent;");
        mediumButton.setStyle("-fx-background-color: linear-gradient(#ffa500, #ff8c00); -fx-border-color: transparent;");
        hardButton.setStyle("-fx-background-color: linear-gradient(#ff6347, #ff4500); -fx-border-color: transparent;");

        if (level.equals("Easy")) {
            easyButton.setStyle(easyButton.getStyle() + " -fx-border-color: yellow; -fx-border-width: 3px;");
        } else if (level.equals("Medium")) {
            mediumButton.setStyle(mediumButton.getStyle() + " -fx-border-color: yellow; -fx-border-width: 3px;");
        } else if (level.equals("Hard")) {
            hardButton.setStyle(hardButton.getStyle() + " -fx-border-color: yellow; -fx-border-width: 3px;");
        }
    }

    private void startGame(Stage primaryStage) {
        // Reset game state
        buttons = null;
        values = null;
        firstSelected = null;
        secondSelected = null;
        waiting = false;
        matchedPairs = 0;

        VBox root = new VBox();
        root.setAlignment(Pos.CENTER);
        root.setSpacing(20);

        // Set background color based on difficulty level
        String backgroundColor = "#f0e68c"; // Default for Easy
        if (gridSize == 6) {
            backgroundColor = "#add8e6"; // Medium
        } else if (gridSize == 8) {
            backgroundColor = "#ffb6c1"; // Hard
        }
        root.setStyle("-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, " + backgroundColor + ", #ffd700);");

        timerText.setFont(Font.font(20)); //Reduced font size
        timerText.setFill(Color.DARKBLUE);

        Button backButton = createStyledButton("Back", "#f08080", "#b22222"); // Light Coral and Firebrick

        backButton.setOnAction(e -> start(primaryStage));

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);

        buttons = new Button[gridSize][gridSize];
        values = new String[gridSize][gridSize];
        initializeValues();

        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                Button button = new Button();
                button.setPrefSize(90, 90); // Reduced button size
                button.setFont(Font.font("Arial Rounded MT Bold", 35)); // Set font to Arial Rounded MT Bold
                button.setStyle("-fx-background-color: lightblue; -fx-border-color: darkblue; -fx-border-width: 2px;");

                final int r = row, c = col;
                button.setOnAction(e -> handleButtonClick(button, r, c));

                buttons[row][col] = button;
                grid.add(button, col, row);
            }
        }

        root.getChildren().addAll(timerText, grid, backButton);

        // Adjust window size dynamically based on the grid size
        int windowSize = 180 + gridSize * 80; // Reduced base size and multiplier
        Scene scene = new Scene(root, windowSize, windowSize);
        primaryStage.setScene(scene);
        primaryStage.show();

        startTimer();
    }

    private Button createStyledButton(String text, String startColor, String endColor) {
        Button button = new Button(text);

        button.setPrefWidth(180); //Reduce the preferred width
        button.setPrefHeight(80); //Reduce the preferred height


        button.setFont(Font.font("Arial Rounded MT Bold", 30)); //Reduced font size
        button.setTextFill(Color.WHITE);
        button.setStyle("-fx-background-color: linear-gradient(" + startColor + ", " + endColor + ");" +
                        "-fx-background-radius: 15;" +
                        "-fx-border-radius: 15;" +
                        "-fx-border-color: white; -fx-border-width: 2;");

        button.setOnMouseEntered(e -> {
            ScaleTransition scaleUp = new ScaleTransition(Duration.millis(100), button);
            scaleUp.setToX(1.1);
            scaleUp.setToY(1.1);
            scaleUp.play();
        });
        button.setOnMouseExited(e -> {
            ScaleTransition scaleDown = new ScaleTransition(Duration.millis(100), button);
            scaleDown.setToX(1);
            scaleDown.setToY(1);
            scaleDown.play();
        });

        return button;
    }

    private void initializeValues() {
        ArrayList<String> cardValues = new ArrayList<>();

        for (int i = 1; i <= (gridSize * gridSize) / 2; i++) {
            cardValues.add(String.valueOf(i));
            cardValues.add(String.valueOf(i));
        }

        Collections.shuffle(cardValues);

        int index = 0;
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                values[row][col] = cardValues.get(index);
                index++;
            }
        }
    }

    private void handleButtonClick(Button button, int row, int col) {
        if (waiting || button.getText().length() > 0) {
            return;
        }

        button.setText(values[row][col]);
        button.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-border-width: 2px;");

        if (firstSelected == null) {
            firstSelected = button;
        } else {
            secondSelected = button;
            waiting = true;

            if (firstSelected.getText().equals(secondSelected.getText())) {
                firstSelected.setDisable(true);
                secondSelected.setDisable(true);
                matchedPairs++;
                resetSelection();

                if (matchedPairs == (gridSize * gridSize) / 2) {
                    showCongratulationsDialog();
                }
            } else {
                new Thread(() -> {
                    try {
                        Thread.sleep(300);//how many times stay
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    javafx.application.Platform.runLater(() -> {
                        firstSelected.setText("");
                        firstSelected.setStyle("-fx-background-color: lightblue; -fx-border-color: darkblue; -fx-border-width: 2px;");
                        secondSelected.setText("");
                        secondSelected.setStyle("-fx-background-color: lightblue; -fx-border-color: darkblue; -fx-border-width: 2px;");
                        resetSelection();
                    });
                }).start();
            }
        }
    }

    private void resetSelection() {
        firstSelected = null;
        secondSelected = null;
        waiting = false;
    }

    private void startTimer() {
        startTime = System.currentTimeMillis();
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                long elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
                timerText.setText("Time: " + elapsedTime + " seconds");

                if (matchedPairs == (gridSize * gridSize) / 2) {
                    stop();
                }
            }
        };
        timer.start();
    }

    private void showCongratulationsDialog() {
        long elapsedTime = (System.currentTimeMillis() - startTime) / 1000;

        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Congratulations!");
        alert.setHeaderText(null);
        alert.setContentText("You completed the game in " + elapsedTime + " seconds!");

        Text content = new Text("Congratulations!\nYou completed the game in " + elapsedTime + " seconds!");
        content.setFont(Font.font(24));
        content.setFill(Color.DARKGREEN);
        content.setTextAlignment(TextAlignment.CENTER);

        alert.getDialogPane().setContent(content);
        alert.showAndWait();
    }


}