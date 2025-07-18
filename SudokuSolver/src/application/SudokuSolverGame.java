package application;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Random;

public class SudokuSolverGame extends Application {

    private static final int SIZE = 9;
    private TextField[][] board = new TextField[SIZE][SIZE];

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Create the Sudoku grid
        GridPane gridPane = new GridPane();
        gridPane.setHgap(5);
        gridPane.setVgap(5);

        // Create a Sudoku board with text fields
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                TextField cell = new TextField();
                cell.setPrefWidth(50);
                cell.setPrefHeight(50);
                cell.setAlignment(Pos.CENTER);
                cell.setStyle("-fx-font-size: 16px;");
                board[row][col] = cell;
                gridPane.add(cell, col, row);
            }
        }

        // Generate a random Sudoku puzzle
        generateRandomSudokuPuzzle();

        // Button to solve the puzzle
        Button solveButton = new Button("Solve");
        solveButton.setOnAction(e -> solveSudoku());

        // Layout
        VBox vbox = new VBox(10, gridPane, solveButton);
        vbox.setPadding(new javafx.geometry.Insets(10));

        // Scene and stage
        Scene scene = new Scene(vbox, 450, 500);
        primaryStage.setTitle("Sudoku Solver");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Function to generate a random solvable Sudoku puzzle
    private void generateRandomSudokuPuzzle() {
        char[][] boardArray = new char[SIZE][SIZE];
        // Fill the board with a valid Sudoku solution
        fillSudokuBoard(boardArray);

        // Create a random puzzle by removing some numbers (set to '.')
        Random rand = new Random();
        int cellsToRemove = rand.nextInt(30) + 40; // Random number between 40 and 70 cells to remove
        for (int i = 0; i < cellsToRemove; i++) {
            int row = rand.nextInt(SIZE);
            int col = rand.nextInt(SIZE);
            boardArray[row][col] = '.'; // Set cell to empty
        }

        // Fill the UI with the puzzle
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (boardArray[row][col] == '.') {
                    board[row][col].setText(""); // Empty cell
                } else {
                    board[row][col].setText(String.valueOf(boardArray[row][col]));
                    board[row][col].setStyle("-fx-background-color: lightgray;");
                }
            }
        }
    }

    // Function to fill the Sudoku board with a valid solution
    private void fillSudokuBoard(char[][] board) {
        Random rand = new Random();
        // Try to fill the board with numbers 1-9 while keeping the Sudoku rules
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                while (true) {
                    int number = rand.nextInt(9) + 1; // Random number from 1 to 9
                    if (isSafe(board, i, j, number)) {
                        board[i][j] = (char) (number + '0');
                        break;
                    }
                }
            }
        }
    }

    // Function to check if it's safe to place a number in the given cell
    private boolean isSafe(char[][] board, int row, int col, int number) {
        // Check column
        for (int i = 0; i < SIZE; i++) {
            if (board[i][col] == (char) (number + '0')) {
                return false;
            }
        }
        // Check row
        for (int j = 0; j < SIZE; j++) {
            if (board[row][j] == (char) (number + '0')) {
                return false;
            }
        }
        // Check 3x3 grid
        int sr = 3 * (row / 3);
        int sc = 3 * (col / 3);
        for (int i = sr; i < sr + 3; i++) {
            for (int j = sc; j < sc + 3; j++) {
                if (board[i][j] == (char) (number + '0')) {
                    return false;
                }
            }
        }
        return true;
    }

    // Function to solve the Sudoku puzzle
    private boolean solveSudoku() {
        char[][] boardArray = new char[SIZE][SIZE];

        // Get values from the UI and put them in the boardArray
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                String value = board[row][col].getText();
                boardArray[row][col] = value.isEmpty() ? '.' : value.charAt(0);
            }
        }

        // Call the solver function
        if (solve(boardArray, 0, 0)) {
            // If the puzzle is solved, update the UI
            for (int row = 0; row < SIZE; row++) {
                for (int col = 0; col < SIZE; col++) {
                    board[row][col].setText(String.valueOf(boardArray[row][col]));
                    board[row][col].setStyle("-fx-background-color: lightgray;");
                }
            }
            return true;
        } else {
            showAlert("No solution exists.");
            return false;
        }
    }

    // Recursive function to solve the Sudoku puzzle
    private boolean solve(char[][] board, int row, int col) {
        if (row == SIZE) {
            return true;
        }

        // Move to the next column
        int nextRow = (col == SIZE - 1) ? row + 1 : row;
        int nextCol = (col == SIZE - 1) ? 0 : col + 1;

        if (board[row][col] != '.') {
            return solve(board, nextRow, nextCol);
        }

        for (int num = 1; num <= 9; num++) {
            if (isSafe(board, row, col, num)) {
                board[row][col] = (char) (num + '0');
                if (solve(board, nextRow, nextCol)) {
                    return true;
                }
                board[row][col] = '.'; // Backtrack
            }
        }
        return false;
    }

    // Show an alert if there's no solution
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Sudoku Solver");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
