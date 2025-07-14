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

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

public class SudokuSolverApp06 extends Application {

	private static final int SIZE = 9; // Sudoku board size
	private TextField[][] textFields = new TextField[SIZE][SIZE];

	// Default font properties
	private Font defaultFont = Font.font("Arial", FontWeight.BOLD, 30);
	private Color defaultFontColor = Color.BLACK;
	private int currentRow = 0;
	private int currentCol = 0;

	// Light colors for blocks
	private Color[] colors = new Color[] { Color.PINK, Color.LIGHTCORAL, Color.LIGHTSALMON, Color.LAVENDER,
			Color.LIGHTSEAGREEN, Color.PALEVIOLETRED, Color.PALEGOLDENROD, Color.PALETURQUOISE, Color.PALEGREEN,
			Color.THISTLE };

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Sudoku Solver | PRINCE");

		GridPane gridPane = new GridPane();
		gridPane.setAlignment(Pos.CENTER);
		gridPane.setHgap(5);
		gridPane.setVgap(5);

		// Shuffle colors to apply randomly to 3x3 parts
		List<Color> colorList = new ArrayList<>();
		Collections.addAll(colorList, colors);
		Collections.shuffle(colorList);

		// Create the 9x9 grid of text fields with random colors for each 3x3 part
		for (int row = 0; row < SIZE; row++) {
			for (int col = 0; col < SIZE; col++) {
				TextField textField = new TextField();
				textField.setPrefWidth(75);
				textField.setPrefHeight(75);
				textField.setAlignment(Pos.CENTER);
				textField.setFont(defaultFont);
				textField.setText(""); // Default value

				// Assign color based on 3x3 block (row/3 and col/3 determines the block)
				int blockRow = row / 3;
				int blockCol = col / 3;
				Color blockColor = colorList.get(blockRow * 3 + blockCol);
				textField.setStyle("-fx-background-color: " + colorToHex(blockColor) + "; "
						+ "-fx-border-color: #333; -fx-border-width: 1px; " + "-fx-text-fill: "
						+ colorToHex(defaultFontColor) + ";");

				textFields[row][col] = textField;
				gridPane.add(textField, col, row);

				// Set key event handler for navigation
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

		// Solve button with gradient fill, font, and improved style
		Button solveButton = new Button("Solve");
		solveButton.setFont(Font.font("Arial", FontWeight.BOLD, 16)); // Font size and style
		solveButton.setTextFill(Color.WHITE); // Font color

		// Set a gradient fill for the button
		solveButton.setStyle("-fx-background-color: linear-gradient(to right, #ff7f50, #ff6347); " + // Gradient color
				"-fx-background-radius: 30; " + // Rounded corners
				"-fx-padding: 10 20; " + // Padding
				"-fx-border-radius: 30; " + // Rounded border
				"-fx-border-color: #ff4500; " + // Border color
				"-fx-border-width: 2px;"); // Border width

		// Set the action for the button
		solveButton.setOnAction(event -> solveSudoku());

		Button runAgainButton = new Button("Run Again");
		runAgainButton.setFont(Font.font("Arial", FontWeight.BOLD, 18)); // Font size and style
		runAgainButton.setTextFill(Color.WHITE); // Font color

		runAgainButton.setStyle("-fx-background-color: linear-gradient(to right, #ff0000, #8b0000); " + // Red to Dark
																										 // Red gradient
				"-fx-background-radius: 40; " + // Rounded corners
				"-fx-padding: 12 25; " + // Padding
				"-fx-border-radius: 40; " + // Rounded border
				"-fx-border-color: #a52a2a; " + // Border color (Brown)
				"-fx-border-width: 3px; " + // Border width
				"-fx-effect: dropshadow(gaussian, rgba(255, 255, 255, 0.5), 5, 0, 0, 3);"); // Shadow effect
		runAgainButton.setOnAction(event -> resetBoard());

		// Layout
		VBox root = new VBox(10, gridPane, solveButton, runAgainButton);
		root.setAlignment(Pos.CENTER);

		Scene scene = new Scene(root, 850, 850);
		primaryStage.setScene(scene);
		primaryStage.show();

		// Focus the first cell initially
		focusCell(currentRow, currentCol);
	}

	private String colorToHex(Color color) {
		return String.format("#%02X%02X%02X", (int) (color.getRed() * 255), (int) (color.getGreen() * 255),
				(int) (color.getBlue() * 255));
	}

	private void focusCell(int row, int col) {
		// Focus the specific cell (TextField)
		textFields[row][col].requestFocus();
		currentRow = row;
		currentCol = col;
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
		// Shuffle colors to apply randomly to 3x3 parts again
		List<Color> colorList = new ArrayList<>();
		Collections.addAll(colorList, colors);
		Collections.shuffle(colorList);

		// Clear the board and reset to default state
		for (int row = 0; row < SIZE; row++) {
			for (int col = 0; col < SIZE; col++) {
				textFields[row][col].setText("");
				// Assign color to each 3x3 part again based on shuffled colors
				int blockRow = row / 3;
				int blockCol = col / 3;
				Color blockColor = colorList.get(blockRow * 3 + blockCol);
				textFields[row][col].setStyle("-fx-background-color: " + colorToHex(blockColor) + "; "
						+ "-fx-border-color: #333; -fx-border-width: 1px; " + "-fx-text-fill: "
						+ colorToHex(defaultFontColor) + ";");
			}
		}
	}

	private boolean isSafe(char[][] board, int row, int col, int number) {
		for (int i = 0; i < SIZE; i++) {
			if (board[i][col] == (char) (number + '0'))
				return false;
			if (board[row][i] == (char) (number + '0'))
				return false;
		}

		int sr = 3 * (row / 3);
		int sc = 3 * (col / 3);
		for (int i = sr; i < sr + 3; i++) {
			for (int j = sc; j < sc + 3; j++) {
				if (board[i][j] == (char) (number + '0'))
					return false;
			}
		}
		return true;
	}

	private boolean helper(char[][] board, int row, int col) {
		if (row == SIZE)
			return true;

		int nrow = (col == SIZE - 1) ? row + 1 : row;
		int ncol = (col == SIZE - 1) ? 0 : col + 1;

		if (board[row][col] != '.') {
			return helper(board, nrow, ncol);
		}

		for (int i = 1; i <= 9; i++) {
			if (isSafe(board, row, col, i)) {
				board[row][col] = (char) (i + '0');
				if (helper(board, nrow, ncol))
					return true;
				board[row][col] = '.';
			}
		}

		return false;
	}
}
