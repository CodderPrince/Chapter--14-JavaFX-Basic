/*
 * EX: 14.3
 * DISPLAY THREE CARDS AMONG 52 CARDS
 */

package application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Collections;

public class DisplayThreeCards extends Application {
    @Override
    public void start(Stage primaryStage) {
        // Create a GridPane to hold the card images
        GridPane gridPane = new GridPane();

        // Set background color with a gradient
        gridPane.setStyle("-fx-background-color: linear-gradient(to bottom, #f4d03f, #16a085);");

        // Set spacing and padding
        gridPane.setHgap(15); // Horizontal gap between cards
        gridPane.setVgap(15); // Vertical gap between cards
        gridPane.setPadding(new javafx.geometry.Insets(20)); // Padding around the grid

        // Create a list of card numbers from 1 to 54
        ArrayList<Integer> cardNumbers = new ArrayList<>();
        for (int i = 1; i <= 54; i++) {
            cardNumbers.add(i);
        }

        // Shuffle the cards randomly
        Collections.shuffle(cardNumbers);

        // Add all 54 cards to the grid, 9 cards per row
        int cardIndex = 0;
        for (int row = 0; row < 6; row++) { // 6 rows (54 / 9 = 6)
            for (int col = 0; col < 9; col++) { // 9 columns per row
                if (cardIndex < cardNumbers.size()) {
                    int cardNumber = cardNumbers.get(cardIndex++);
                    String imagePath;

                    // Determine the file extension based on card number
                    if (cardNumber <= 52) {
                        imagePath = getClass().getResource("/image/card/" + cardNumber + ".png").toExternalForm();
                    } else {
                        imagePath = getClass().getResource("/image/card/" + cardNumber + ".jpg").toExternalForm();
                    }

                    // Create an ImageView for the card
                    Image cardImage = new Image(imagePath);
                    ImageView imageView = new ImageView(cardImage);
                    imageView.setFitWidth(80); // Set the width of the card
                    imageView.setFitHeight(130); // Set the height of the card
                    imageView.setPreserveRatio(true);

                    // Add the ImageView to the GridPane
                    gridPane.add(imageView, col, row);
                }
            }
        }

        // Create a scene and place it in the stage
        Scene scene = new Scene(gridPane);
        primaryStage.setTitle("Display 54 Cards");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
