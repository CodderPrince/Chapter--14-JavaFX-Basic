package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.animation.ScaleTransition;
import javafx.animation.FadeTransition;
import javafx.util.Duration;

import java.util.Stack;

public class TicTacToe extends Application {

    private GameLogic gameLogic;

    @Override
    public void start(Stage primaryStage) {
        gameLogic = new GameLogic(primaryStage);
        gameLogic.initGame();
    }

    public static void main(String[] args) {
        TicTacToe game = new TicTacToe();
        game.runGame(args);
    }

    public void runGame(String[] args) {
        launch(args);
    }
}

class GameLogic {

    private Stage primaryStage;
    private String currentPlayer = "❌";
    private Button[][] buttons = new Button[3][3];
    private boolean isPlayerVsComputer = false;
    private Stack<int[]> moveHistory = new Stack<>();

    public GameLogic(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void initGame() {
        primaryStage.setTitle("Tic Tac Toe | Developed by PRINCE");
        VBox menu = createStartMenu();
        Scene scene = new Scene(menu, 450, 450);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createStartMenu() {
        Label chooseModeLabel = new Label("Choose Game Mode");
        chooseModeLabel.setStyle("-fx-font-size: 40px; -fx-font-weight: bold; -fx-text-fill: white;");

        Button pvpButton = createStyledButton("Player vs Player", "#4CAF50");
        pvpButton.setPrefWidth(250);
        pvpButton.setPrefHeight(70);
        pvpButton.setOnAction(e -> startGame(false));

        Button pvcButton = createStyledButton("Player vs Computer", "#2196F3");
        pvcButton.setPrefWidth(250);
        pvcButton.setPrefHeight(70);
        pvcButton.setOnAction(e -> startGame(true));

        Button backButton = createStyledButton("Back", "#9C27B0");
        backButton.setPrefWidth(200);
        backButton.setPrefHeight(50);
        backButton.setOnAction(e -> {
            // Close or go to the previous screen
            primaryStage.close(); // Replace this action with your desired behavior
        });

        VBox menu = new VBox(50, chooseModeLabel, pvpButton, pvcButton, backButton);
        menu.setAlignment(Pos.CENTER);
        menu.setStyle("-fx-background-color: linear-gradient(to bottom right, #0B486B, #F56217);");
        return menu;
    }

    private void startGame(boolean playerVsComputer) {
        isPlayerVsComputer = playerVsComputer;
        currentPlayer = "❌";

        Label modeLabel = new Label(isPlayerVsComputer ? "Player vs Computer" : "Player vs Player");
        modeLabel.setStyle("-fx-font-size: 25px; -fx-text-fill: blue; -fx-font-family: 'Arial, MT Rounded'; -fx-font-weight: bold;");

        Button backButton = createStyledButton("Back", "#4CAF50");
        backButton.setOnAction(e -> initGame());

        AnchorPane topBar = new AnchorPane();
        topBar.setPadding(new Insets(10));
        topBar.getChildren().addAll(backButton, modeLabel);
        AnchorPane.setLeftAnchor(backButton, 10.0);
        AnchorPane.setTopAnchor(backButton, 5.0);
        AnchorPane.setTopAnchor(modeLabel, 5.0);
        AnchorPane.setLeftAnchor(modeLabel, 130.0);

        GridPane grid = createGameGrid();

        Button undoButton = createStyledButton("Undo", "#FF1744");
        undoButton.setOnAction(e -> undoLastMove());

        VBox gameContent = new VBox(9, grid);
        if (!isPlayerVsComputer) {
            gameContent.getChildren().add(undoButton);
        }
        gameContent.setAlignment(Pos.CENTER);

        VBox root = new VBox(10, topBar, gameContent);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(10));
        root.setStyle(isPlayerVsComputer ? "-fx-background-color: linear-gradient(to bottom right, #FFE0B2, #FFCC80);" : "-fx-background-color: linear-gradient(to bottom right, #F3E5F5, #E1BEE7);");

        Scene scene = new Scene(root, 450, 580);
        primaryStage.setScene(scene);
    }

    private GridPane createGameGrid() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setPadding(new Insets(20));

        grid.setStyle(isPlayerVsComputer ? "-fx-background-color: linear-gradient(to bottom right, #3E1E00, #5C3C00); -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 10);" : "-fx-background-color: linear-gradient(to bottom right, #0D1F2D, #1B3B5F); -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 10);");

        DropShadow neonEffectX = new DropShadow(20, Color.RED);
        DropShadow neonEffectO = new DropShadow(20, Color.CYAN);

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                Button button = new Button("");
                button.setMinSize(120, 120);
                button.setStyle("-fx-font-size: 40px; -fx-font-weight: bold; -fx-text-fill: #000000;");
                int r = row, c = col;
                button.setOnAction(e -> handleButtonPress(button, r, c, neonEffectX, neonEffectO));
                buttons[row][col] = button;
                grid.add(button, col, row);
            }
        }
        return grid;
    }

    private Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle("-fx-font-size: 22px; -fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-family: 'Arial, MT Rounded'; -fx-font-weight: bold;");

        DropShadow shadow = new DropShadow();
        shadow.setRadius(10);
        shadow.setColor(Color.GRAY);
        button.setEffect(shadow);

        // Button Hover Animation with Glow Effect
        button.setOnMouseEntered(e -> {
            ScaleTransition scaleUp = new ScaleTransition(Duration.millis(200), button);
            scaleUp.setToX(1.2);
            scaleUp.setToY(1.2);
            scaleUp.play();

            DropShadow glow = new DropShadow();
            glow.setRadius(20);
            glow.setColor(Color.GOLD);
            button.setEffect(glow);
        });
        button.setOnMouseExited(e -> {
            ScaleTransition scaleDown = new ScaleTransition(Duration.millis(200), button);
            scaleDown.setToX(1);
            scaleDown.setToY(1);
            scaleDown.play();

            DropShadow shadowReset = new DropShadow();
            shadowReset.setRadius(10);
            shadowReset.setColor(Color.GRAY);
            button.setEffect(shadowReset);
        });

        return button;
    }

    private void handleButtonPress(Button button, int row, int col, DropShadow neonEffectX, DropShadow neonEffectO) {
        if (button.getText().isEmpty()) {
            button.setText(currentPlayer);
            button.setStyle(currentPlayer.equals("❌") ? "-fx-font-size: 40px; -fx-font-weight: bold; -fx-text-fill: #FF0000;" : "-fx-font-size: 40px; -fx-font-weight: bold; -fx-text-fill: #0000FF;");
            button.setEffect(currentPlayer.equals("❌") ? neonEffectX : neonEffectO);

            moveHistory.push(new int[]{row, col});

            if (checkWin()) {
                showAlert("Player " + currentPlayer + " wins!");
                resetBoard();
            } else if (isBoardFull()) {
                showAlert("It's a draw!");
                resetBoard();
            } else {
                switchPlayer();
            }
        }
    }

    private boolean checkWin() {
        for (int i = 0; i < 3; i++) {
            if (!buttons[i][0].getText().isEmpty() && buttons[i][0].getText().equals(buttons[i][1].getText()) && buttons[i][1].getText().equals(buttons[i][2].getText())) {
                return true;
            }
            if (!buttons[0][i].getText().isEmpty() && buttons[0][i].getText().equals(buttons[1][i].getText()) && buttons[1][i].getText().equals(buttons[2][i].getText())) {
                return true;
            }
        }
        if (!buttons[0][0].getText().isEmpty() && buttons[0][0].getText().equals(buttons[1][1].getText()) && buttons[1][1].getText().equals(buttons[2][2].getText())) {
            return true;
        }
        if (!buttons[0][2].getText().isEmpty() && buttons[0][2].getText().equals(buttons[1][1].getText()) && buttons[1][1].getText().equals(buttons[2][0].getText())) {
            return true;
        }
        return false;
    }

    private boolean isBoardFull() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (buttons[i][j].getText().isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    private void resetBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText("");
                buttons[i][j].setEffect(null);
            }
        }
        currentPlayer = "❌";
        moveHistory.clear();
    }

    private void switchPlayer() {
        currentPlayer = currentPlayer.equals("❌") ? "⭕" : "❌";
        if (isPlayerVsComputer && currentPlayer.equals("⭕")) {
            computerMove();
        }
    }

    private void computerMove() {
        int[] bestMove = findBestMove();
        handleButtonPress(buttons[bestMove[0]][bestMove[1]], bestMove[0], bestMove[1], null, new DropShadow(20, Color.CYAN));
    }

    private int[] findBestMove() {
        int bestScore = Integer.MIN_VALUE;
        int[] bestMove = {-1, -1};

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (buttons[row][col].getText().isEmpty()) {
                    buttons[row][col].setText("⭕");
                    int score = minimax(false);
                    buttons[row][col].setText("");
                    if (score > bestScore) {
                        bestScore = score;
                        bestMove = new int[]{row, col};
                    }
                }
            }
        }
        return bestMove;
    }

    private int minimax(boolean isMaximizing) {
        if (checkWin()) {
            return isMaximizing ? -1 : 1;
        }
        if (isBoardFull()) {
            return 0;
        }

        int bestScore = isMaximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (buttons[row][col].getText().isEmpty()) {
                    buttons[row][col].setText(isMaximizing ? "⭕" : "❌");
                    int score = minimax(!isMaximizing);
                    buttons[row][col].setText("");
                    bestScore = isMaximizing ? Math.max(score, bestScore) : Math.min(score, bestScore);
                }
            }
        }
        return bestScore;
    }

    private void undoLastMove() {
        if (!moveHistory.isEmpty()) {
            int[] lastMove = moveHistory.pop();
            Button lastButton = buttons[lastMove[0]][lastMove[1]];
            lastButton.setText("");
            lastButton.setEffect(null);
            switchPlayer();
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText(null);

        Label contentLabel = new Label(message);
        contentLabel.setStyle(message.contains("wins") ? (message.contains("❌") ? "-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #FF0000;" : "-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #0000FF;") : "-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #666666;");

        alert.getDialogPane().setContent(contentLabel);
        alert.showAndWait();
    }
}
