package application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;

import java.util.Random;

public class SierpinskiTriangle1 extends Application {

    @Override
    public void start(Stage primaryStage) {
        SierpinskiPane pane = new SierpinskiPane();

        
     // Input field and label for the order
        Label instructionLabel = new Label("Enter the triangle number:");
        instructionLabel.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-size: 16; -fx-text-fill: blue;");

        TextField tfOrder = new TextField();
        tfOrder.setPrefColumnCount(4);
        tfOrder.setPromptText("Enter an order:");
        tfOrder.setStyle("-fx-font-family: 'Verdana'; -fx-font-size: 14; -fx-text-fill: black; -fx-prompt-text-fill: gray; -fx-border-color: gray; -fx-border-radius: 5; -fx-background-radius: 5; -fx-background-color: lightyellow;");

        
        tfOrder.setOnAction(e -> {
            try {
                int order = Integer.parseInt(tfOrder.getText());
                pane.setOrder(order);
            } catch (NumberFormatException ex) {
                tfOrder.setText("0"); // Reset to 0 if invalid input
                pane.setOrder(0);
            }
        });

        VBox inputBox = new VBox(5, instructionLabel, tfOrder);
        inputBox.setStyle("-fx-alignment: center; -fx-padding: 10;");

        BorderPane borderPane = new BorderPane();
        borderPane.setTop(inputBox);
        borderPane.setCenter(pane);

        Scene scene = new Scene(borderPane, 500, 500);
        primaryStage.setTitle("Colorful Sierpinski Triangle");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    class SierpinskiPane extends Pane {
        private int order = 0;
        private final Random random = new Random();

        public void setOrder(int order) {
            this.order = order;
            draw();
        }

        private void draw() {
            getChildren().clear();
            setStyle("-fx-background-color: linear-gradient(to bottom, #e0f7fa, #ffffff);");
            double width = getWidth();
            double height = getHeight();

            // Define the three main points of the triangle
            double[] points = {
                width / 2, 10,
                10, height - 10,
                width - 10, height - 10
            };

            displayTriangles(order, points[0], points[1], points[2], points[3], points[4], points[5]);
        }


        private void displayTriangles(int order, double x1, double y1, double x2, double y2, double x3, double y3) {
            if (order == 0) {
                // Base case: Draw a filled triangle with random color
                Polygon triangle = new Polygon();
                triangle.getPoints().addAll(x1, y1, x2, y2, x3, y3);
                triangle.setFill(Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
                getChildren().add(triangle);
            } else {
                // Recursive case: Divide the triangle into smaller triangles
                double midX1 = (x1 + x2) / 2;
                double midY1 = (y1 + y2) / 2;
                double midX2 = (x2 + x3) / 2;
                double midY2 = (y2 + y3) / 2;
                double midX3 = (x1 + x3) / 2;
                double midY3 = (y1 + y3) / 2;

                displayTriangles(order - 1, x1, y1, midX1, midY1, midX3, midY3);
                displayTriangles(order - 1, midX1, midY1, x2, y2, midX2, midY2);
                displayTriangles(order - 1, midX3, midY3, midX2, midY2, x3, y3);
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
