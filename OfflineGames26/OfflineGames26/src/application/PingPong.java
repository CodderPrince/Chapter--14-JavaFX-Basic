package application;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.util.Duration;

public class PingPong extends Application {

    private static final double width = 1000;
    private static final double height = 700;
    private static final double PLAYER_WIDTH = 15;
    private static final double BALL_R = 20;
    private double playerOneXPos = 0;
    private double playerOneYPos = height / 2;
    private double playerTwoXPos = width - PLAYER_WIDTH;
    private double playerTwoYPos = height / 2;
    private double ballXPos = width / 2;
    private double ballYPos = height / 2;
    private double playerHeight = 100;
    private double computerHeight = 100;
    private boolean gameStarted;
    private int ballXSpeed = 3; // Reduced initial speed
    private int ballYSpeed = 3; // Reduced initial speed
     private final int speedIncrement = 1; // Speed Increment after hitting a paddle
     private final int maxSpeed = 10; // MAX BALL SPEED
    private int scoresP1 = 0;
    private int scoresP2 = 0;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Ping-Pong Game | Shithi Rani Roy");
        primaryStage.setResizable(false);

        BorderPane borderPane = new BorderPane();
        Canvas canvas = new Canvas(width, height);
        borderPane.setCenter(canvas);

        HBox hbButtons = new HBox(20);
        HBox hbSliders = new HBox(20);
        borderPane.setBottom(hbButtons);
        borderPane.setTop(hbSliders);

        Scene scene1 = new Scene(new StackPane(borderPane));

        // Title label
        Label titleText = new Label("PING-PONG GAME");
        titleText.setFont(Font.font("Verdana", 30));
        titleText.setTextFill(Color.DARKBLUE);
        hbSliders.getChildren().add(titleText);
        hbSliders.setAlignment(Pos.CENTER);

        // Start button
        Button startButton = new Button("START GAME");
        String startDefaultStyle = "-fx-background-color: #8A2BE2; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 10px;";
        String startHoverStyle = "-fx-background-color: #d3d3d3; -fx-text-fill: black; -fx-font-size: 16px; -fx-padding: 10px;";
        addHoverEffect(startButton, startDefaultStyle, startHoverStyle);

        // Reset button
        Button resetButton = new Button("RESET GAME");
        String resetDefaultStyle = "-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 10px;";
        String resetHoverStyle = "-fx-background-color: #d3d3d3; -fx-text-fill: black; -fx-font-size: 16px; -fx-padding: 10px;";
        addHoverEffect(resetButton, resetDefaultStyle, resetHoverStyle);

        hbButtons.getChildren().addAll(startButton, resetButton);
        hbButtons.setAlignment(Pos.CENTER);

        // Player height slider
        Slider playerHeightSlider = new Slider(50, 500, 100);
        playerHeightSlider.setShowTickLabels(true);
        playerHeightSlider.setShowTickMarks(true);
        playerHeightSlider.setMajorTickUnit(100);
        playerHeightSlider.valueProperty().addListener((obs, oldVal, newVal) -> playerHeight = newVal.doubleValue());

        // Computer height slider
        Slider computerHeightSlider = new Slider(50, 500, 100);
        computerHeightSlider.setShowTickLabels(true);
        computerHeightSlider.setShowTickMarks(true);
        computerHeightSlider.setMajorTickUnit(50);
        computerHeightSlider.valueProperty().addListener((obs, oldVal, newVal) -> computerHeight = newVal.doubleValue());

        hbSliders.getChildren().addAll(new Label("Player Height"), playerHeightSlider, new Label("Computer Height"), computerHeightSlider);

        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(10), e -> runGame(graphicsContext, canvas)));
        timeline.setCycleCount(Timeline.INDEFINITE);

        startButton.setOnAction(e -> {
            gameStarted = true; // Start the game
            ballXPos = width / 2; // Reset ball position
            ballYPos = height / 2; // Reset ball position
            if (scoresP1 >= 100 || scoresP2 >= 100) {
                scoresP1 = 0; // Reset scores
                scoresP2 = 0;
            }
        });

        resetButton.setOnAction(e -> {
            gameStarted = false; // Pause the game
            resetGame();
        });

        primaryStage.setScene(scene1);
        primaryStage.show();
        timeline.play();
    }

    private void runGame(GraphicsContext graphicsContext, Canvas canvas) {
        graphicsContext.setFill(Color.DARKBLUE);
        graphicsContext.fillRect(0, 0, width, height);

        graphicsContext.setFill(Color.WHITE);
        graphicsContext.setFont(Font.font("Verdana", 25));
        graphicsContext.fillText("User: " + scoresP1, width / 4, 50);
        graphicsContext.fillText("Computer: " + scoresP2, 3 * width / 4, 50);

        graphicsContext.fillRect(playerTwoXPos, playerTwoYPos, PLAYER_WIDTH, computerHeight);
        graphicsContext.fillRect(playerOneXPos, playerOneYPos, PLAYER_WIDTH, playerHeight);

        if (gameStarted) {
            graphicsContext.setStroke(Color.WHITE);
            graphicsContext.setLineWidth(1);
            graphicsContext.strokeLine(width / 2, 0, width / 2, height);

            canvas.setOnMouseMoved(e -> playerOneYPos = e.getY());

            // Aggressive and Fast Computer Movement
            double computerCenterY = playerTwoYPos + computerHeight / 2;
             if (Math.abs(ballYPos - computerCenterY) > 5) { // Move if ball is farther than 5px
                playerTwoYPos += (ballYPos > computerCenterY) ? 10 : -10; // Directly move to the ball's position with a greater speed
            }

             // Keep the computer paddle within bounds
            playerTwoYPos = Math.max(0, Math.min(height - computerHeight, playerTwoYPos));

            graphicsContext.setFill(Color.RED);
            graphicsContext.fillOval(ballXPos, ballYPos, BALL_R, BALL_R);

           // Ball Movement
             ballXPos += ballXSpeed;
             ballYPos += ballYSpeed;
        } else {
            graphicsContext.setFill(Color.YELLOW);
            graphicsContext.setFont(Font.font("Verdana", 30));
            graphicsContext.setTextAlign(TextAlignment.CENTER);
            graphicsContext.fillText("Click the button to start game", width / 2, height / 2);
        }

        if (ballYPos > height || ballYPos < 0) ballYSpeed *= -1;

        if (((ballXPos + BALL_R > playerTwoXPos) && ballYPos >= playerTwoYPos && ballYPos <= playerTwoYPos + computerHeight) ||
            ((ballXPos < playerOneXPos + PLAYER_WIDTH) && ballYPos >= playerOneYPos && ballYPos <= playerOneYPos + playerHeight)) {
            // Increment speed only if not at max
            if(Math.abs(ballXSpeed) < maxSpeed){
                ballXSpeed += speedIncrement * Math.signum(ballXSpeed);
            }
             if(Math.abs(ballYSpeed) < maxSpeed){
               ballYSpeed += speedIncrement * Math.signum(ballYSpeed);
            }
            ballXSpeed *= -1;

        }

        if (ballXPos < playerOneXPos - PLAYER_WIDTH) {
            scoresP2 += 10;
            gameStarted = false;
            resetBall();
            checkWinner();
        }

        if (ballXPos > playerTwoXPos + PLAYER_WIDTH) {
            scoresP1 += 10;
            gameStarted = false;
            resetBall();
            checkWinner();
        }
    }


    private void resetBall() {
        ballXPos = width / 2;
        ballYPos = height / 2;
        ballXSpeed = 3; // Reset speed
        ballYSpeed = 3; // Reset Speed
    }

    private void checkWinner() {
        if (scoresP1 >= 100) {
            showWinnerDialog("User");
            resetGame();
        } else if (scoresP2 >= 100) {
            showWinnerDialog("Computer");
            resetGame();
        }
    }

    private void showWinnerDialog(String winner) {
        Stage dialog = new Stage();
        dialog.setTitle("Game Over");
        dialog.initModality(Modality.APPLICATION_MODAL);

        Label label = new Label(winner + " wins the game!");
        label.setFont(Font.font("Verdana", 20));
        label.setTextFill(Color.DARKBLUE);
        label.setAlignment(Pos.CENTER);

        Button okButton = new Button("OK");
        okButton.setOnAction(e -> dialog.close());
        okButton.setStyle("-fx-background-color: #8A2BE2; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px;");

        StackPane pane = new StackPane();
        pane.getChildren().addAll(label, okButton);
        StackPane.setAlignment(okButton, Pos.BOTTOM_CENTER);
        StackPane.setAlignment(label, Pos.CENTER);

        Scene scene = new Scene(pane, 500, 400);
        dialog.setScene(scene);
        dialog.show();
    }

    private void resetGame() {
        scoresP1 = 0;
        scoresP2 = 0;
        resetBall();
    }

    private void addHoverEffect(Button button, String defaultStyle, String hoverStyle) {
        button.setStyle(defaultStyle);
        button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
        button.setOnMouseExited(e -> button.setStyle(defaultStyle));
    }

    
}