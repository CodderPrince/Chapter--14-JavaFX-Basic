package application;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SudokuSolverApp extends Application {

    private static final int SIZE = 9; // Sudoku board size
    private TextField[][] textFields = new TextField[SIZE][SIZE];

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

        // Create the 9x9 grid of text fields
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                TextField textField = new TextField();
                textField.setPrefWidth(40);
                textField.setAlignment(Pos.CENTER);
                textField.setText("."); // Default value
                textFields[row][col] = textField;
                gridPane.add(textField, col, row);
            }
        }

        // Solve button
        Button solveButton = new Button("Solve");
        solveButton.setOnAction(event -> solveSudoku());

        // Layout
        VBox root = new VBox(10, gridPane, solveButton);
        root.setAlignment(Pos.CENTER);

        Scene scene = new Scene(root, 400, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
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
