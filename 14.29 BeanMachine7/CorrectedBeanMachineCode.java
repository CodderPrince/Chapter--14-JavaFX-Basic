
package application;

import javafx.animation.PauseTransition;
import javafx.animation.PathTransition;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.QuadCurveTo;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Random;

public class BMF28 extends Application {

    private static final int BALL_RADIUS = 10;
    private static final int PINS = 7;
    private ArrayList<Circle> pegs; // Store pegs for reuse
    private Rectangle pot; // The pot shape
    private int collectedBalls = 0; // Number of balls collected in the pot
    private Label resultLabel; // Label to update collected balls

    @Override
    public void start(Stage primaryStage) {
        Pane pane = new Pane();
        pane.setStyle("-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, peachpuff, ivory);");

        VBox controlBox = new VBox(10);
        controlBox.setLayoutX(20);
        controlBox.setLayoutY(20);

        Label timerLabel = new Label("Game Running");
        timerLabel.setStyle("-fx-text-fill: red;" +
                "-fx-font-size: 24px;" +
                "-fx-font-family: 'Arial'; " +
                "-fx-font-weight: bold;");
        timerLabel.setLayoutX(350);
        timerLabel.setLayoutY(20);
        pane.getChildren().add(timerLabel);

        resultLabel = new Label("Balls Collected: 0");
        resultLabel.setStyle("-fx-text-fill: blue;" +
                "-fx-font-size: 20px;" +
                "-fx-font-family: 'Arial'; " +
                "-fx-font-weight: bold;");
        resultLabel.setLayoutX(350);
        resultLabel.setLayoutY(60);
        pane.getChildren().add(resultLabel);

        ComboBox<String> durationDropdown = new ComboBox<>();
        durationDropdown.getItems().addAll("30 Seconds", "1 Minute", "1.3 Minutes", "2 Minutes");
        durationDropdown.setValue("30 Seconds");
        durationDropdown.setStyle("-fx-background-color: lightblue;" +
                "-fx-text-fill: black;" +
                "-fx-font-family: 'Arial'; " +
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;");
        durationDropdown.setPrefWidth(222);

        Button startButton = new Button("Start Game");
        startButton.setStyle("-fx-background-color: darkgreen; " +
                "-fx-text-fill: white;" +
                "-fx-font-family: 'Arial'; " +
                "-fx-font-size: 18px; " +
                "-fx-font-weight: bold;");

        controlBox.getChildren().addAll(new Label("Galton Game | Developed by PRINCE"),
                new Label("Select Play Duration:"), durationDropdown, startButton);

        pegs = drawMachine(pane);

        // Create the pot at the bottom
        pot = createPot(400, 720, 150, 50);
        pane.getChildren().add(pot);

        startButton.setOnAction(e -> {
            collectedBalls = 0;
            resultLabel.setText("Balls Collected: 0");
            pane.getChildren().removeIf(node -> node instanceof Circle && !pegs.contains(node));

            dropBallsContinuously(pane);
        });

        pane.getChildren().add(controlBox);

        Scene scene = new Scene(pane, 900, 850);

        primaryStage.setTitle("Bean Machine with Pot");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private ArrayList<Circle> drawMachine(Pane pane) {
        ArrayList<Circle> pegList = new ArrayList<>();
        int paneCenterX = 450;

        for (int i = 0; i < PINS; i++) {
            for (int j = 0; j <= i; j++) {
                double x = paneCenterX - i * 20 + j * 40;
                double y = 150 + i * 50;
                Circle pin = new Circle(x, y, 7, Color.BLACK);
                pegList.add(pin);
                pane.getChildren().add(pin);
            }
        }

        return pegList;
    }

    private Rectangle createPot(double x, double y, double width, double height) {
        Rectangle pot = new Rectangle(x - width / 2, y - height / 2, width, height);
        pot.setFill(Color.DARKCYAN); // Change color if needed
        pot.setStroke(Color.BLACK);
        pot.setStrokeWidth(3);
        return pot;
    }

    private void dropBallsContinuously(Pane pane) {
        Random random = new Random();
        Timeline dropTimeline = new Timeline(new KeyFrame(Duration.seconds(0.5), event -> {
            Circle ball = new Circle(450, 100, BALL_RADIUS);
            ball.setFill(randomColor());
            pane.getChildren().add(ball);

            Path path = new Path();
            path.getElements().add(new MoveTo(ball.getCenterX(), ball.getCenterY()));

            double currentX = ball.getCenterX();
            double currentY = ball.getCenterY();

            for (Circle peg : pegs) {
                double pegX = peg.getCenterX();
                double pegY = peg.getCenterY();

                double bounceDirection = random.nextBoolean() ? -30 : 30;
                double newX = currentX + bounceDirection;

                path.getElements().add(new QuadCurveTo(pegX, pegY, newX, pegY + 20));

                currentX = newX;
                currentY = pegY + 20;
            }

            PathTransition pathTransition = new PathTransition(Duration.seconds(4), path, ball);
            pathTransition.setOnFinished(ev -> {
                if (isBallInPot(ball)) {
                    collectedBalls++;
                    resultLabel.setText("Balls Collected: " + collectedBalls);
                }
                pane.getChildren().remove(ball); // Remove ball from screen
            });
            pathTransition.play();
        }));

        dropTimeline.setCycleCount(Timeline.INDEFINITE);
        dropTimeline.play();
    }

    private boolean isBallInPot(Circle ball) {
        double ballX = ball.getTranslateX() + ball.getCenterX();
        double ballY = ball.getTranslateY() + ball.getCenterY();

        return ballX >= pot.getX() && ballX <= pot.getX() + pot.getWidth() &&
               ballY >= pot.getY() && ballY <= pot.getY() + pot.getHeight();
    }

    private Color randomColor() {
        Random random = new Random();
        return Color.color(random.nextDouble(), random.nextDouble(), random.nextDouble());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
