/*
 * SineCosinePlot
 * Ex: 14.19
 */

package application;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polyline;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class SineCosinePlot extends Application {
    @Override
    public void start(Stage primaryStage) {
        Pane pane = new Pane();

        // Creating polyline for sine function
        Polyline sinePolyline = new Polyline();
        ObservableList<Double> sineList = sinePolyline.getPoints();
        double scaleFactor = 50;
        for (int x = -170; x <= 170; x++) {
            sineList.add(x + 200.0);
            sineList.add(100 - scaleFactor * Math.sin((x / 100.0) * 2 * Math.PI));
        }
        sinePolyline.setStroke(Color.RED);

        // Creating polyline for cosine function
        Polyline cosinePolyline = new Polyline();
        ObservableList<Double> cosineList = cosinePolyline.getPoints();
        for (int x = -170; x <= 170; x++) {
            cosineList.add(x + 200.0);
            cosineList.add(100 - scaleFactor * Math.cos((x / 100.0) * 2 * Math.PI));
        }
        cosinePolyline.setStroke(Color.BLUE);

        // Adding x and y axes
        Line xAxis = new Line(10, 100, 390, 100);
        Line yAxis = new Line(200, 10, 200, 200);

        // Adding labels for -2π, -π, 0, π, 2π
        Text textMinus2Pi = new Text(60, 115, "-2\u03c0");
        Text textMinusPi = new Text(130, 115, "-\u03c0");
        Text textZero = new Text(200, 115, "0");
        Text textPi = new Text(270, 115, "\u03c0");
        Text text2Pi = new Text(340, 115, "2\u03c0");

        // Adding all elements to the pane
        pane.getChildren().addAll(sinePolyline, cosinePolyline, xAxis, yAxis,
                                  textMinus2Pi, textMinusPi, textZero, textPi, text2Pi);

        Scene scene = new Scene(pane, 400, 200);
        primaryStage.setTitle("Sine and Cosine Functions Ex14_19");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
