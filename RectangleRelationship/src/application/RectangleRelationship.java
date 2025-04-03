/*
 * Rectangle Relation
 * Ex: 14.23
 */

package application;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class RectangleRelationship extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Input fields for rectangle properties
        TextField tfX1 = new TextField();
        TextField tfY1 = new TextField();
        TextField tfWidth1 = new TextField();
        TextField tfHeight1 = new TextField();
        TextField tfX2 = new TextField();
        TextField tfY2 = new TextField();
        TextField tfWidth2 = new TextField();
        TextField tfHeight2 = new TextField();

        // Button to display rectangles
        Button btShow = new Button("Show Rectangles");

        // Layout for input fields
        GridPane gridPane = new GridPane();
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        gridPane.add(new Label("Rectangle 1 Center X:"), 0, 0);
        gridPane.add(tfX1, 1, 0);
        gridPane.add(new Label("Rectangle 1 Center Y:"), 0, 1);
        gridPane.add(tfY1, 1, 1);
        gridPane.add(new Label("Rectangle 1 Width:"), 0, 2);
        gridPane.add(tfWidth1, 1, 2);
        gridPane.add(new Label("Rectangle 1 Height:"), 0, 3);
        gridPane.add(tfHeight1, 1, 3);
        gridPane.add(new Label("Rectangle 2 Center X:"), 0, 4);
        gridPane.add(tfX2, 1, 4);
        gridPane.add(new Label("Rectangle 2 Center Y:"), 0, 5);
        gridPane.add(tfY2, 1, 5);
        gridPane.add(new Label("Rectangle 2 Width:"), 0, 6);
        gridPane.add(tfWidth2, 1, 6);
        gridPane.add(new Label("Rectangle 2 Height:"), 0, 7);
        gridPane.add(tfHeight2, 1, 7);
        gridPane.add(btShow, 1, 8);
        gridPane.setAlignment(Pos.CENTER);

        Pane rectanglePane = new Pane();
        Text resultText = new Text();
        
        btShow.setOnAction(e -> {
            try {
                // Get input values
                double x1 = Double.parseDouble(tfX1.getText());
                double y1 = Double.parseDouble(tfY1.getText());
                double width1 = Double.parseDouble(tfWidth1.getText());
                double height1 = Double.parseDouble(tfHeight1.getText());

                double x2 = Double.parseDouble(tfX2.getText());
                double y2 = Double.parseDouble(tfY2.getText());
                double width2 = Double.parseDouble(tfWidth2.getText());
                double height2 = Double.parseDouble(tfHeight2.getText());

                // Calculate top-left corners based on center coordinates
                double rect1X = x1 - width1 / 2;
                double rect1Y = y1 - height1 / 2;
                double rect2X = x2 - width2 / 2;
                double rect2Y = y2 - height2 / 2;

                // Create rectangles
                Rectangle rect1 = new Rectangle(rect1X, rect1Y, width1, height1);
                Rectangle rect2 = new Rectangle(rect2X, rect2Y, width2, height2);
                rect1.setStyle("-fx-stroke: black; -fx-fill: transparent;");
                rect2.setStyle("-fx-stroke: black; -fx-fill: transparent;");

                rectanglePane.getChildren().clear(); // Clear previous rectangles and text
                rectanglePane.getChildren().addAll(rect1, rect2);

                // Check relationship and display result
                String relationship = getRectangleRelationship(rect1X, rect1Y, width1, height1, rect2X, rect2Y, width2, height2);
                resultText.setText(relationship);

                // Calculate bottom-most position of rectangles
                double maxY = Math.max(rect1Y + height1, rect2Y + height2);

                // Center the text below the rectangles
                resultText.setX((rect1X + rect2X) / 2); // Centered between rectangles
                resultText.setY(maxY + 20); // 20 pixels below the lowest rectangle
                
                rectanglePane.getChildren().add(resultText);

            } catch (NumberFormatException ex) {
                Alert alert = new Alert(AlertType.ERROR, "Please enter valid numbers.");
                alert.showAndWait();
            }
        });

        // Layout for the scene
        VBox mainLayout = new VBox(10);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.getChildren().addAll(gridPane, rectanglePane);

        Scene scene = new Scene(mainLayout, 400, 400);
        primaryStage.setTitle("Rectangle Relationship");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Method to determine the relationship between two rectangles
    private String getRectangleRelationship(double x1, double y1, double w1, double h1,
                                            double x2, double y2, double w2, double h2) {
        // Check if one rectangle is contained in the other
        if (x2 >= x1 && y2 >= y1 && x2 + w2 <= x1 + w1 && y2 + h2 <= y1 + h1) {
            return "One rectangle is contained in another";
        } else if (x1 >= x2 && y1 >= y2 && x1 + w1 <= x2 + w2 && y1 + h1 <= y2 + h2) {
            return "One rectangle is contained in another";
        }

        // Check if they overlap
        boolean overlap = x1 < x2 + w2 && x1 + w1 > x2 && y1 < y2 + h2 && y1 + h1 > y2;
        if (overlap) {
            return "The rectangles overlap";
        }

        // If no overlap and no containment, they don't overlap
        return "The rectangles do not overlap";
    }

    public static void main(String[] args) {
        launch(args);
    }
}
