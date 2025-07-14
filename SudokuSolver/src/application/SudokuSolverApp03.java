package application;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class SudokuSolverApp03 extends Application {

    private static final int SIZE = 9; // Sudoku board size
    private TextField[][] textFields = new TextField[SIZE][SIZE];
    
    // Default font properties
    private Font defaultFont = Font.font("Arial", FontWeight.BOLD, 30);
    private Color defaultFontColor = Color.BLACK;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Sudoku Solver");

        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(5);
        gridPane.setVgap(5);

        // Create the 9x9 grid of text fields with color differentiation for 3x3 grids
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                TextField textField = new TextField();
                textField.setPrefWidth(75);
                textField.setPrefHeight(75);
                textField.setAlignment(Pos.CENTER);
                textField.setFont(defaultFont);
                textField.setText(""); // Default value
                
                // Determine the 3x3 subgrid and apply different colors
                int blockRow = row / 3;
                int blockCol = col / 3;
                String blockColor = getBlockColor(blockRow, blockCol);
                
                textField.setStyle("-fx-background-color: " + blockColor + "; " +
                                   "-fx-border-color: #333; -fx-border-width: 1px; " +
                                   "-fx-text-fill: " + colorToHex(defaultFontColor) + ";");

                textFields[row][col] = textField;
                gridPane.add(textField, col, row);
            }
        }

        // Solve button
        Button solveButton = new Button("Solve");
        solveButton.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        solveButton.setOnAction(event -> solveSudoku());

        // Run Again button
        Button runAgainButton = new Button("Run Again");
        runAgainButton.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        runAgainButton.setOnAction(event -> resetBoard());

        // Layout
        VBox root = new VBox(10, gridPane, solveButton, runAgainButton);
        root.setAlignment(Pos.CENTER);

        Scene scene = new Scene(root, 850, 850);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private String getBlockColor(int blockRow, int blockCol) {
        // Provide different background colors for each 3x3 block
        String[] colors = {
            "#FFDDC1", "#FFABAB", "#FFC3A0", // Block 1 colors
            "#A3D2CA", "#FFE156", "#6A0572", // Block 2 colors
            "#4ECDC4", "#1D3557", "#F1FAEE"  // Block 3 colors
        };

        return colors[blockRow * 3 + blockCol];
    }

    private String colorToHex(Color color) {
        return String.format("#%02X%02X%02X", (int)(color.getRed() * 255), (int)(color.getGreen() * 255), (int)(color.getBlue() * 255));
    }

    private void solveSudoku() {
        char[][] board = new char[SIZE][SIZE];

        // Read input from the text fields
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                String text = textFields[row][col].getText();
                board[row][col] = (text.isEmpty() || text.equals(".")) ? '.' : text.charAt(0);
            }
        }

        // Solve the Sudoku
        if (helper(board, 0, 0)) {
            // Update the text fields with the solution
            for (int row = 0; row < SIZE; row++) {
                for (int col = 0; col < SIZE; col++) {
                    textFields[row][col].setText(String.valueOf(board[row][col]));
                }
            }
        } else {
            // Show error if the board is unsolvable
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Sudoku Solver");
            alert.setContentText("The Sudoku board is unsolvable!");
            alert.showAndWait();
        }
    }

    private void resetBoard() {
        // Clear the board and reset to default state
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                textFields[row][col].setText("");
                // Reset back to the default color and font
                textFields[row][col].setStyle("-fx-background-color: " + getBlockColor(row / 3, col / 3) + "; " +
                                              "-fx-border-color: #333; -fx-border-width: 1px; " +
                                              "-fx-text-fill: " + colorToHex(defaultFontColor) + ";");
            }
        }
    }

    private boolean isSafe(char[][] board, int row, int col, int number) {
        for (int i = 0; i < SIZE; i++) {
            if (board[i][col] == (char) (number + '0')) return false;
            if (board[row][i] == (char) (number + '0')) return false;
        }

        int sr = 3 * (row / 3);
        int sc = 3 * (col / 3);
        for (int i = sr; i < sr + 3; i++) {
            for (int j = sc; j < sc + 3; j++) {
                if (board[i][j] == (char) (number + '0')) return false;
            }
        }
        return true;
    }

    private boolean helper(char[][] board, int row, int col) {
        if (row == SIZE) return true;

        int nrow = (col == SIZE - 1) ? row + 1 : row;
        int ncol = (col == SIZE - 1) ? 0 : col + 1;

        if (board[row][col] != '.') {
            return helper(board, nrow, ncol);
        }

        for (int i = 1; i <= 9; i++) {
            if (isSafe(board, row, col, i)) {
                board[row][col] = (char) (i + '0');
                if (helper(board, nrow, ncol)) return true;
                board[row][col] = '.';
            }
        }

        return false;
    }
}
