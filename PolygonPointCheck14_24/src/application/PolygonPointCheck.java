/*
 * Point check in Polygon
 * Ex: 14.24
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
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class PolygonPointCheck extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Input fields for the points
        TextField tfX1 = new TextField();
        TextField tfY1 = new TextField();
        TextField tfX2 = new TextField();
        TextField tfY2 = new TextField();
        TextField tfX3 = new TextField();
        TextField tfY3 = new TextField();
        TextField tfX4 = new TextField();
        TextField tfY4 = new TextField();
        TextField tfX5 = new TextField();
        TextField tfY5 = new TextField();

        // Button to display the polygon and check the point
        Button btShow = new Button("Show Polygon and Check Point");

        // Layout for input fields
        GridPane gridPane = new GridPane();
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        gridPane.add(new Label("Point 1 X:"), 0, 0);
        gridPane.add(tfX1, 1, 0);
        gridPane.add(new Label("Point 1 Y:"), 0, 1);
        gridPane.add(tfY1, 1, 1);
        gridPane.add(new Label("Point 2 X:"), 0, 2);
        gridPane.add(tfX2, 1, 2);
        gridPane.add(new Label("Point 2 Y:"), 0, 3);
        gridPane.add(tfY2, 1, 3);
        gridPane.add(new Label("Point 3 X:"), 0, 4);
        gridPane.add(tfX3, 1, 4);
        gridPane.add(new Label("Point 3 Y:"), 0, 5);
        gridPane.add(tfY3, 1, 5);
        gridPane.add(new Label("Point 4 X:"), 0, 6);
        gridPane.add(tfX4, 1, 6);
        gridPane.add(new Label("Point 4 Y:"), 0, 7);
        gridPane.add(tfY4, 1, 7);
        gridPane.add(new Label("Point 5 X (Check):"), 0, 8);
        gridPane.add(tfX5, 1, 8);
        gridPane.add(new Label("Point 5 Y (Check):"), 0, 9);
        gridPane.add(tfY5, 1, 9);
        gridPane.add(btShow, 1, 10);
        gridPane.setAlignment(Pos.CENTER);

        Pane pane = new Pane();
        Text resultText = new Text();

        btShow.setOnAction(e -> {
            try {
                // Get input values
                double x1 = Double.parseDouble(tfX1.getText());
                double y1 = Double.parseDouble(tfY1.getText());
                double x2 = Double.parseDouble(tfX2.getText());
                double y2 = Double.parseDouble(tfY2.getText());
                double x3 = Double.parseDouble(tfX3.getText());
                double y3 = Double.parseDouble(tfY3.getText());
                double x4 = Double.parseDouble(tfX4.getText());
                double y4 = Double.parseDouble(tfY4.getText());
                double x5 = Double.parseDouble(tfX5.getText());
                double y5 = Double.parseDouble(tfY5.getText());

                // Create the polygon
                Polygon polygon = new Polygon();
                polygon.getPoints().addAll(x1, y1, x2, y2, x3, y3, x4, y4);
                polygon.setStroke(Color.BLACK);
                polygon.setFill(Color.TRANSPARENT);

                // Create the point as a circle
                Circle point = new Circle(x5, y5, 5, Color.BLACK);

                // Check if the point is inside the polygon
                boolean contains = polygon.contains(x5, y5);

                // Set the result text
                if (contains) {
                    resultText.setText("The point is inside the polygon");
                } else {
                    resultText.setText("The point is outside the polygon");
                }

                // Clear and add elements to the pane
                pane.getChildren().clear();
                pane.getChildren().addAll(polygon, point, resultText);

                // Calculate the bottom-most point of the polygon
                double maxY = Math.max(Math.max(Math.max(y1, y2), y3), y4);

                // Center the text horizontally and position it below the polygon
                resultText.setX((x1 + x2 + x3 + x4) / 4 - resultText.getLayoutBounds().getWidth() / 2);
                resultText.setY(maxY + 30); // 30 pixels below the polygon

            } catch (NumberFormatException ex) {
                Alert alert = new Alert(AlertType.ERROR, "Please enter valid numbers.");
                alert.showAndWait();
            }
        });

        // Layout for the scene
        VBox mainLayout = new VBox(10);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.getChildren().addAll(gridPane, pane);

        Scene scene = new Scene(mainLayout, 600, 600);
        primaryStage.setTitle("Polygon and Point Check");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
