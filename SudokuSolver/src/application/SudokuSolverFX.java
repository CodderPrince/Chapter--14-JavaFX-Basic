package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SudokuSolverFX extends Application {

    private TextField[][] boardFields = new TextField[9][9];
    private char[][] board = new char[9][9];

    @Override
    public void start(Stage primaryStage) {
        // Create the Sudoku board grid
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setPadding(new Insets(20));
        gridPane.setHgap(5);
        gridPane.setVgap(5);


        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                TextField textField = new TextField(".");
                textField.setPrefWidth(40);
                textField.setAlignment(Pos.CENTER);

                // Limit input to digits and .
                textField.textProperty().addListener((observable, oldValue, newValue) -> {
                    if (!newValue.matches("[1-9\\.]*")) {
                        textField.setText(oldValue);
                    } else if (newValue.length() > 1) {
                         textField.setText(newValue.substring(0,1));
                    }
                });

                boardFields[i][j] = textField;
                gridPane.add(textField, j, i);


                // Style thicker borders around 3x3 subgrids
                if (i % 3 == 0) {
                    textField.setStyle("-fx-border-width: 1px 1px 1px 2px; -fx-border-color: black;");
                } else if (i % 3 == 2){
                    textField.setStyle("-fx-border-width: 1px 1px 2px 2px; -fx-border-color: black;");
                } else {
                    textField.setStyle("-fx-border-width: 1px 1px 1px 1px; -fx-border-color: black;");
                }
                  if (j % 3 == 0) {
                      if(i % 3 ==2){
                          textField.setStyle(textField.getStyle()+ " -fx-border-width: 2px 1px 2px 2px; -fx-border-color: black;");
                      } else {
                          textField.setStyle(textField.getStyle()+ " -fx-border-width: 1px 1px 1px 2px; -fx-border-color: black;");
                      }
                    
                } else if (j % 3 == 2){
                     if(i % 3 ==2){
                          textField.setStyle(textField.getStyle()+ " -fx-border-width: 2px 2px 2px 1px; -fx-border-color: black;");
                      } else {
                          textField.setStyle(textField.getStyle()+ " -fx-border-width: 1px 2px 1px 1px; -fx-border-color: black;");
                      }
                }
            }
        }

        Button solveButton = new Button("Solve");
        solveButton.setOnAction(e -> solveSudoku());

        HBox buttonBox = new HBox(solveButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10));


        VBox root = new VBox(gridPane, buttonBox);
        Scene scene = new Scene(root);

        primaryStage.setTitle("Sudoku Solver");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void solveSudoku() {
         for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                String text = boardFields[i][j].getText();
                board[i][j] = (text.isEmpty() || text.equals(".")) ? '.' : text.charAt(0);
            }
        }

        if (helper(board, 0, 0)) {
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    boardFields[i][j].setText(String.valueOf(board[i][j]));
                }
            }
        } else {
            // Handle the case where the Sudoku is unsolvable (e.g., show an alert)
            System.out.println("Unsolvable"); // Replace with appropriate error handling

        }
    }


    // ... (isSafe and helper methods remain the same as before) ...

    public static void main(String[] args) {
        launch(args);
    }
}