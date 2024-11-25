/*
 * Bar Chart Show
 * Exercise: 14.12
 * Developed by PRINCE
 * ID: 12105007
 */

package application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class BarChart extends Application {
    @Override
    public void start(Stage primaryStage) {
        // Create a VBox for each bar
        VBox projectBar = createBar("Project -- 20%", 20, Color.RED);
        VBox quizBar = createBar("Quiz -- 10%", 10, Color.BLUE);
        VBox midtermBar = createBar("Midterm -- 30%", 30, Color.GREEN);
        VBox finalBar = createBar("Final -- 40%", 40, Color.ORANGE);

        // Create an HBox to hold all bars
        HBox hbox = new HBox(20); // Add spacing between bars
        hbox.getChildren().addAll(projectBar, quizBar, midtermBar, finalBar);

        // Set padding and alignment
        hbox.setStyle("-fx-padding: 20; -fx-alignment: center; -fx-background-color: #f4f4f4;");

        // Create a scene and place it in the stage
        Scene scene = new Scene(hbox, 500, 300);
        primaryStage.setTitle("Bar Chart");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Method to create a single bar with a label
    private VBox createBar(String labelText, int heightPercent, Color color) {
        // Create a rectangle for the bar
        Rectangle bar = new Rectangle();
        bar.setWidth(50); // Set the bar width
        bar.setHeight(heightPercent * 5); // Scale height (e.g., 10% = 50px)
        bar.setFill(color);

        // Create a text label
        Text label = new Text(labelText);

        // Adjust spacing to align all bars and labels
        VBox vbox = new VBox(); // Spacing between bar and label
        vbox.setSpacing(5); // Consistent spacing
        vbox.setStyle("-fx-alignment: bottom-center;"); // Align bars and labels at the bottom
        vbox.getChildren().addAll(label, bar); // Add label first, then the bar
        return vbox;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
