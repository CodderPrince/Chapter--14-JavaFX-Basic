package application;

import javafx.animation.KeyFrame;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseButton;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.Random;

public class Minesweeper extends Application {
    private int gridSize = 10;
    private int numMines = 15;
    private Button[][] cells;
    private boolean[][] minePlaced;
    private boolean[][] revealed;
    private int flagsUsed = 0;
    private int secondsElapsed = 0;
    private boolean gameStarted = false;
    private Timeline timer;
    private Label flagCounter;
    private Label timerLabel;
    private Stage primaryStage;
    private boolean firstClick = true;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        showMenu();
    }

    private void showMenu() {
        BorderPane menuRoot = new BorderPane();
        menuRoot.setStyle("-fx-background-color: linear-gradient(to bottom, #0f2027, #2c5364);"); // Deep ocean gradient
        Label chooseModeLabel = new Label("Choose Difficulty Level");
        chooseModeLabel.setStyle("-fx-font-size: 50px; -fx-font-weight: bold; -fx-text-fill: YELLOW;");

        // Create a VBox for all components (spacing above difficulty buttons)
        VBox container = new VBox(30); // Add spacing between elements
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(50, 20, 50, 50)); // Padding for spacing from the top

        // Container for easy, medium, and hard buttons
        HBox difficultyOptions = new HBox(20);
        difficultyOptions.setAlignment(Pos.CENTER);

        Button easyButton = createStyledButton("Easy", "#7fffd4", "#40e0d0", "30", "Arial", Color.GRAY);
        Button mediumButton = createStyledButton("Medium", "#ffb6c1", "#ff69b4", "30", "Verdana", Color.DARKGRAY);
        Button hardButton = createStyledButton("Hard", "#ffa07a", "#ff4500", "30", "Tahoma", Color.SLATEGRAY);

        easyButton.setOnAction(e -> setupGame(8, 5, 720, 550)); //Reduced grid size
        mediumButton.setOnAction(e -> setupGame(10, 20, 755, 650));
        hardButton.setOnAction(e -> setupGame(12, 35, 820, 750));

        difficultyOptions.getChildren().addAll(easyButton, mediumButton, hardButton);

        // Back button container
        HBox backButtonContainer = new HBox();
        backButtonContainer.setAlignment(Pos.CENTER);
        Button backButton = createStyledButton("Back", "#087f23 ", "#087f23 ", "25", "Arial", Color.BLUE);
        backButton.setOnAction(e -> primaryStage.close()); // Close application for now
        backButtonContainer.getChildren().add(backButton);
        backButtonContainer.setPadding(new Insets(100, 0, 0, 0)); // Padding for spacing from difficulty buttons

        // Add difficulty options and back button to the container
        container.getChildren().addAll(chooseModeLabel,difficultyOptions, backButtonContainer);

        // Add the container to the center of the layout
        menuRoot.setCenter(container);

        // Create and display the scene
        Scene menuScene = new Scene(menuRoot, 800, 500);
        primaryStage.setScene(menuScene);
        primaryStage.setTitle("Minesweeper - Select Difficulty");
        primaryStage.show();
    }





    private Button createStyledButton(String text, String startColor, String endColor, String fontSize, String fontStyle, Color shadowColor) {
        Button button = new Button(text);

        // Set button size
        button.setPrefWidth(180);  // Set preferred width
        button.setPrefHeight(40); // Set preferred height

        // Styling
        button.setFont(Font.font(fontStyle, Double.parseDouble(fontSize)));
        button.setTextFill(Color.WHITE);
        button.setStyle("-fx-background-color: linear-gradient(" + startColor + ", " + endColor + ");" +
                        "-fx-background-radius: 15;" +
                        "-fx-border-radius: 15;" +
                        "-fx-border-color: white; -fx-border-width: 2;");

        // Drop Shadow Effect
        DropShadow shadow = new DropShadow();
        shadow.setRadius(10);
        shadow.setColor(shadowColor);
        button.setEffect(shadow);

        // Button Hover Animation
        button.setOnMouseEntered(e -> {
            ScaleTransition scaleUp = new ScaleTransition(Duration.millis(200), button);
            scaleUp.setToX(1.1);
            scaleUp.setToY(1.1);
            scaleUp.play();
        });

        button.setOnMouseExited(e -> {
            ScaleTransition scaleDown = new ScaleTransition(Duration.millis(200), button);
            scaleDown.setToX(1);
            scaleDown.setToY(1);
            scaleDown.play();
        });

        return button;
    }


    private void setupGame(int size, int mines, int windowWidth, int windowHeight) {
        this.gridSize = size;
        this.numMines = mines;
        this.cells = new Button[gridSize][gridSize];
        this.minePlaced = new boolean[gridSize][gridSize];
        this.revealed = new boolean[gridSize][gridSize];
        this.flagsUsed = 0;
        this.secondsElapsed = 0;
        this.gameStarted = false;
        this.firstClick = true;

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f0fff0;");

        // Top bar with labels and buttons
        HBox topBar = new HBox(20);
        topBar.setAlignment(Pos.CENTER);
        topBar.setPadding(new Insets(20, 20, 20, 20)); // Add padding to separate from the title bar

        flagCounter = new Label("Flags: " + flagsUsed + " / " + numMines);
        flagCounter.setStyle("-fx-font-size: 16pt; -fx-text-fill: darkgreen;");

        timerLabel = new Label("Time: 0");
        timerLabel.setStyle("-fx-font-size: 16pt; -fx-text-fill: darkblue;");

        Button runButton = createStyledButton("Run", "#ff6347", "#ff4500", "16", "Arial", Color.RED);
        runButton.setPrefWidth(80);
        runButton.setPrefHeight(30);
        runButton.setOnAction(e -> startGame());

        Button backButton = createStyledButton("Back", "#4682b4", "#5f9ea0", "16", "Arial", Color.BLUE);
        backButton.setPrefWidth(80);
        backButton.setPrefHeight(30);
        backButton.setOnAction(e -> showMenu());

        Label tooltip = new Label("Right-click to place/remove flags");
        tooltip.setStyle("-fx-font-size: 12pt; -fx-text-fill: gray;");

        topBar.getChildren().addAll(flagCounter, timerLabel, runButton, backButton, tooltip);

        // Center grid for the game cells
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setPadding(new Insets(10, 10, 10, 10)); // Add padding around the grid
        grid.setVgap(2); // Add vertical spacing between cells
        grid.setHgap(2); // Add horizontal spacing between cells

        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                Button cell = new Button();
                cell.setPrefSize(40, 40); //Reduced cell size
                cell.setStyle("-fx-font-size: 16pt; -fx-background-color: #b0e0e6; -fx-border-color: black; -fx-border-width: 1px;");
                int finalI = i;
                int finalJ = j;
                cell.setOnMouseClicked(e -> handleCellClick(finalI, finalJ, e.getButton() == MouseButton.SECONDARY));
                cells[i][j] = cell;
                grid.add(cell, j, i);
            }
        }

        // Place top bar and grid in the root layout
        root.setTop(topBar); // Add the top bar to the top
        root.setCenter(grid); // Add the grid to the center

        // Scene setup
        Scene scene = new Scene(root, windowWidth, windowHeight);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Minesweeper");
        primaryStage.show();
    }




    private void startGame() {
        if (!gameStarted) {
            timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
                secondsElapsed++;
                timerLabel.setText("Time: " + secondsElapsed);
            }));
            timer.setCycleCount(Timeline.INDEFINITE);
            timer.play();
            gameStarted = true;
        }
    }

    private void setUpMines(int excludeX, int excludeY) {
        Random random = new Random();
        int placedMines = 0;

        while (placedMines < numMines) {
            int x = random.nextInt(gridSize);
            int y = random.nextInt(gridSize);

            if (!minePlaced[x][y] && !(x == excludeX && y == excludeY)) {
                minePlaced[x][y] = true;
                placedMines++;
            }
        }
    }

    private void handleCellClick(int x, int y, boolean isFlagging) {
        if (firstClick) {
            setUpMines(x, y);
            firstClick = false;
        }

        if (!gameStarted) return;

        if (isFlagging) {
            if (!revealed[x][y]) {
                if (cells[x][y].getText().equals("F")) {
                    cells[x][y].setText("");
                    cells[x][y].setStyle("-fx-background-color: #b0e0e6; -fx-border-color: black;");
                    flagsUsed--;
                } else if (flagsUsed < numMines) {
                    cells[x][y].setText("F");
                    cells[x][y].setStyle("-fx-background-color: yellow; -fx-text-fill: red; -fx-font-size: 16pt; -fx-border-color: black;");
                    flagsUsed++;
                }
                flagCounter.setText("Flags: " + flagsUsed + " / " + numMines);
            }
            return;
        }

        if (revealed[x][y]) return;
        if (minePlaced[x][y]) {
            cells[x][y].setText("X");
            cells[x][y].setStyle("-fx-background-color: red; -fx-font-size: 16pt; -fx-text-fill: white; -fx-border-color: black;");
            revealBoard();
            showGameOver(false);
            timer.stop();
            return;
        }
        reveal(x, y);
        checkWinCondition();
    }

    private void reveal(int x, int y) {
        if (x < 0 || y < 0 || x >= gridSize || y >= gridSize) return;
        if (revealed[x][y]) return;

        revealed[x][y] = true;
        cells[x][y].setStyle("-fx-background-color: white; -fx-border-color: black; -fx-border-width: 1px;");
        int mineCount = countMines(x, y);

        if (mineCount > 0) {
            cells[x][y].setText(String.valueOf(mineCount));
            cells[x][y].setStyle(getNumberColorStyle(mineCount));
        } else {
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    reveal(x + i, y + j);
                }
            }
        }
    }

    private int countMines(int x, int y) {
        int count = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int nx = x + i;
                int ny = y + j;
                if (nx >= 0 && nx < gridSize && ny >= 0 && ny < gridSize) {
                    if (minePlaced[nx][ny]) count++;
                }
            }
        }
        return count;
    }

    private void revealBoard() {
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                if (minePlaced[i][j]) {
                    cells[i][j].setText("X");
                    cells[i][j].setStyle("-fx-background-color: red; -fx-font-size: 16pt; -fx-text-fill: white; -fx-border-color: black;");
                }
            }
        }
    }

    private void checkWinCondition() {
        int revealedCount = 0;
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                if (revealed[i][j]) {
                    revealedCount++;
                }
            }
        }

        if (revealedCount == gridSize * gridSize - numMines) {
            timer.stop();
            showGameOver(true);
        }
    }

    private void showGameOver(boolean win) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(win ? "Congratulations!" : "Game Over");
        alert.setHeaderText(null);
        alert.setContentText(win ? "You won! Great job." : "You hit a mine! Game over.");
        alert.showAndWait();
        showMenu();
    }

    private String getNumberColorStyle(int number) {
        switch (number) {
            case 1: return "-fx-text-fill: blue; -fx-font-size: 16pt; -fx-font-weight: bold;";
            case 2: return "-fx-text-fill: green; -fx-font-size: 16pt; -fx-font-weight: bold;";
            case 3: return "-fx-text-fill: red; -fx-font-size: 16pt; -fx-font-weight: bold;";
            case 4: return "-fx-text-fill: purple; -fx-font-size: 16pt; -fx-font-weight: bold;";
            case 5: return "-fx-text-fill: maroon; -fx-font-size: 16pt; -fx-font-weight: bold;";
            case 6: return "-fx-text-fill: teal; -fx-font-size: 16pt; -fx-font-weight: bold;";
            case 7: return "-fx-text-fill: black; -fx-font-size: 16pt; -fx-font-weight: bold;";
            case 8: return "-fx-text-fill: gray; -fx-font-size: 16pt; -fx-font-weight: bold;";
            default: return "-fx-text-fill: black; -fx-font-size: 16pt; -fx-font-weight: bold;";
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}