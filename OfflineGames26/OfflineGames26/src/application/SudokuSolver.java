package application;

import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

public class SudokuSolver {

    private static final int SIZE = 9; // Sudoku board size
    private TextField[][] textFields = new TextField[SIZE][SIZE];

    private Font defaultFont = Font.font("Arial", FontWeight.BOLD, 30);
    private Color defaultFontColor = Color.BLACK;
    private int currentRow = 0;
    private int currentCol = 0;

    private Color[] colors = new Color[] { Color.PINK, Color.LIGHTCORAL, Color.LIGHTSALMON, Color.LAVENDER,
            Color.LIGHTSEAGREEN, Color.PALEVIOLETRED, Color.PALEGOLDENROD, Color.PALETURQUOISE, Color.PALEGREEN,
            Color.THISTLE };

    public void startApp(Stage primaryStage) {
        primaryStage.setTitle("Sudoku Solver | PRINCE");

        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(5);
        gridPane.setVgap(5);

        List<Color> colorList = new ArrayList<>();
        Collections.addAll(colorList, colors);
        Collections.shuffle(colorList);

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                TextField textField = new TextField();
                textField.setPrefWidth(75);
                textField.setPrefHeight(75);
                textField.setAlignment(Pos.CENTER);
                textField.setFont(defaultFont);

                // Allow only one number (1-9) in the cell
                textField.textProperty().addListener((observable, oldValue, newValue) -> {
                    if (!newValue.matches("[1-9]")) {
                        textField.setText("");
                    }
                });

                int blockRow = row / 3;
                int blockCol = col / 3;
                Color blockColor = colorList.get(blockRow * 3 + blockCol);
                textField.setStyle("-fx-background-color: " + colorToHex(blockColor) + "; "
                        + "-fx-border-color: #333; -fx-border-width: 1px; "
                        + "-fx-text-fill: " + colorToHex(defaultFontColor) + ";");

                textFields[row][col] = textField;
                gridPane.add(textField, col, row);

                final int r = row;
                final int c = col;
                textField.setOnKeyPressed(event -> {
                    switch (event.getCode()) {
                        case RIGHT:
                            if (c < SIZE - 1)
                                focusCell(r, c + 1);
                            break;
                        case LEFT:
                            if (c > 0)
                                focusCell(r, c - 1);
                            break;
                        case UP:
                            if (r > 0)
                                focusCell(r - 1, c);
                            break;
                        case DOWN:
                            if (r < SIZE - 1)
                                focusCell(r + 1, c);
                            break;
                        default:
                            break;
                    }
                });

            }
        }

        // Buttons with enhanced effects
        Button backButton = createStyledButton("Back", "#32CD32", "#228B22");
        backButton.setOnAction(event -> primaryStage.close()); // Close or navigate back

        Button solveButton = createStyledButton("Solve", "#FF7F50", "#FF6347");
        solveButton.setOnAction(event -> solveSudoku());

        Button runAgainButton = createStyledButton("Run Again", "#6A0DAD", "#1E90FF");
        runAgainButton.setOnAction(event -> resetBoard());

        // Align buttons horizontally
        GridPane buttonPane = new GridPane();
        buttonPane.setAlignment(Pos.CENTER);
        buttonPane.setHgap(160); // Horizontal gap between buttons
        buttonPane.add(backButton, 0, 0); // Back Button on the left
        buttonPane.add(solveButton, 1, 0); // Solve Button in the middle
        buttonPane.add(runAgainButton, 2, 0); // Run Again Button on the right

        VBox root = new VBox(20, gridPane, buttonPane); // Add grid and button pane to root
        root.setAlignment(Pos.CENTER);

        LinearGradient gradient2 = new LinearGradient(0, 0, 1, 1, true, null,
                new Stop(0, Color.LIGHTYELLOW), new Stop(1, Color.LIGHTGREEN));
        root.setStyle("-fx-background-color: " + colorToHex(gradient2.getStops().get(0).getColor()) + ";");

        Scene scene = new Scene(root, 800, 800);
        primaryStage.setScene(scene);
        primaryStage.show();

        focusCell(currentRow, currentCol);
    }

    // Helper method to create styled buttons
    private Button createStyledButton(String text, String startColor, String endColor) {
        Button button = new Button(text);

        // Button size and styling
        button.setPrefWidth(140);
        button.setPrefHeight(50);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        button.setTextFill(Color.WHITE);
        button.setStyle("-fx-background-color: linear-gradient(" + startColor + ", " + endColor + "); "
                + "-fx-background-radius: 30; -fx-border-radius: 30; -fx-border-color: white; -fx-border-width: 2px;");

        // Drop shadow effect
        DropShadow shadow = new DropShadow();
        shadow.setRadius(10);
        shadow.setColor(Color.GRAY);
        button.setEffect(shadow);

        // Hover animations
        button.setOnMouseEntered(e -> {
            ScaleTransition scaleUp = new ScaleTransition(Duration.millis(200), button);
            scaleUp.setToX(1.1);
            scaleUp.setToY(1.1);
            scaleUp.play();

            button.setStyle("-fx-background-color: linear-gradient(" + endColor + ", " + startColor + "); "
                    + "-fx-background-radius: 30; -fx-border-radius: 30; -fx-border-color: white; -fx-border-width: 2px; "
                    + "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.7), 10, 0, 3, 3);");
        });
        button.setOnMouseExited(e -> {
            ScaleTransition scaleDown = new ScaleTransition(Duration.millis(200), button);
            scaleDown.setToX(1);
            scaleDown.setToY(1);
            scaleDown.play();

            button.setStyle("-fx-background-color: linear-gradient(" + startColor + ", " + endColor + "); "
                    + "-fx-background-radius: 30; -fx-border-radius: 30; -fx-border-color: white; -fx-border-width: 2px;");
        });

        return button;
    }

    private String colorToHex(Color color) {
        return String.format("#%02X%02X%02X", (int) (color.getRed() * 255), (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    private void focusCell(int row, int col) {
        textFields[row][col].requestFocus();
        currentRow = row;
        currentCol = col;
    }

    private void solveSudoku() {
         char[][] board = new char[SIZE][SIZE];
         boolean invalidInput = false;

         // Read input from the text fields and validate
         for (int row = 0; row < SIZE; row++) {
             for (int col = 0; col < SIZE; col++) {
                 String text = textFields[row][col].getText();

                 // Validate the input
                 if (!text.isEmpty() && !(text.equals(".") || (text.length() == 1 && Character.isDigit(text.charAt(0)) && text.charAt(0) >= '1' && text.charAt(0) <= '9'))) {
                     invalidInput = true;
                     break; // Break out of the loop if any invalid input is found
                 }

                 // Assign the value to the board
                 board[row][col] = (text.isEmpty() || text.equals(".")) ? '.' : text.charAt(0);
             }
             if (invalidInput) {
                 break; // Break the outer loop if invalid input is found
             }
         }

         if (invalidInput) {
             // Show error if invalid input is found
             Alert alert = new Alert(Alert.AlertType.ERROR);
             alert.setTitle("Invalid Input");
             alert.setHeaderText("Sudoku Solver");
             alert.setContentText("Please enter only numbers (1-9) or '.' for empty cells.");
             alert.showAndWait();
         } else {
             // Check for duplicate numbers in rows, columns, or 3x3 subgrids
             if (hasDuplicates(board)) {
                 Alert alert = new Alert(Alert.AlertType.ERROR);
                 alert.setTitle("Duplicate Found");
                 alert.setHeaderText("Sudoku Solver");
                 alert.setContentText("Duplicate numbers found in a row, column, or subgrid. Please correct and try again.");
                 alert.showAndWait();
             } else {
                 // Proceed to solve the puzzle if input is valid
                 if (solve(board)) {
                     // Display the solved puzzle in text fields
                     displayBoard(board);
                 } else {
                     Alert alert = new Alert(Alert.AlertType.ERROR);
                     alert.setTitle("No Solution");
                     alert.setHeaderText("Sudoku Solver");
                     alert.setContentText("No solution exists for this puzzle.");
                     alert.showAndWait();
                 }
             }
         }
    }

    private boolean solve(char[][] board) {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (board[row][col] == '.') {
                    for (char num = '1'; num <= '9'; num++) {
                        if (isSafe(board, row, col, num)) {
                            board[row][col] = num;
                            if (solve(board)) {
                                return true;
                            } else {
                                board[row][col] = '.'; // Backtrack
                            }
                        }
                    }
                    return false; // Backtrack if no number can be placed
                }
            }
        }
        return true; // Puzzle solved
    }

    private boolean isSafe(char[][] board, int row, int col, char num) {
        // Check the row, column, and 3x3 subgrid for the same number
        return !isInRow(board, row, num) && !isInCol(board, col, num) && !isInSubgrid(board, row, col, num);
    }

    private boolean isInRow(char[][] board, int row, char num) {
        for (int col = 0; col < SIZE; col++) {
            if (board[row][col] == num) {
                return true;
            }
        }
        return false;
    }

    private boolean isInCol(char[][] board, int col, char num) {
        for (int row = 0; row < SIZE; row++) {
            if (board[row][col] == num) {
                return true;
            }
        }
        return false;
    }

    private boolean isInSubgrid(char[][] board, int row, int col, char num) {
        int startRow = row - row % 3;
        int startCol = col - col % 3;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i + startRow][j + startCol] == num) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasDuplicates(char[][] board) {
        Set<Character> seen = new HashSet<>();
        // Check rows
        for (int row = 0; row < SIZE; row++) {
            seen.clear();
            for (int col = 0; col < SIZE; col++) {
                if (board[row][col] != '.' && !seen.add(board[row][col])) {
                    return true; // Duplicate found in row
                }
            }
        }

        // Check columns
        for (int col = 0; col < SIZE; col++) {
            seen.clear();
            for (int row = 0; row < SIZE; row++) {
                if (board[row][col] != '.' && !seen.add(board[row][col])) {
                    return true; // Duplicate found in column
                }
            }
        }

        // Check 3x3 subgrids
        for (int i = 0; i < SIZE; i += 3) {
            for (int j = 0; j < SIZE; j += 3) {
                seen.clear();
                for (int row = i; row < i + 3; row++) {
                    for (int col = j; col < j + 3; col++) {
                        if (board[row][col] != '.' && !seen.add(board[row][col])) {
                            return true; // Duplicate found in 3x3 subgrid
                        }
                    }
                }
            }
        }
        return false;
    }

    private void displayBoard(char[][] board) {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                textFields[row][col].setText(String.valueOf(board[row][col]));
            }
        }
    }

    private void resetBoard() {
        // Shuffle colors to reassign new random colors to 3x3 blocks
        List<Color> colorList = new ArrayList<>();
        Collections.addAll(colorList, colors);
        Collections.shuffle(colorList);

        // Reset the board and apply new colors
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                textFields[row][col].setText(""); // Clear text fields

                // Determine the 3x3 block and assign a new random color
                int blockRow = row / 3;
                int blockCol = col / 3;
                Color blockColor = colorList.get(blockRow * 3 + blockCol);

                // Update the style with the new color
                textFields[row][col].setStyle("-fx-background-color: " + colorToHex(blockColor) + "; "
                        + "-fx-border-color: #333; -fx-border-width: 1px; "
                        + "-fx-text-fill: " + colorToHex(defaultFontColor) + ";");
            }
        }
    }

}
