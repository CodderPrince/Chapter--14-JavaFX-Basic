package application;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class BrickBreaker {

	private static final int WIDTH = 965;
    private static final int HEIGHT = 700;
    private static final int PADDLE_WIDTH = 300;
    private static final int PADDLE_HEIGHT = 15;
    private static final int BALL_SIZE = 20;
    private static final int BRICK_WIDTH = 12;
    private static final int BRICK_HEIGHT = 12;
    private static final int BRICK_SPACING = 8;

    private double paddleX = WIDTH / 2 - PADDLE_WIDTH / 2;
    private double ballX = WIDTH / 2;
    private double ballY = HEIGHT - 200;
    private double ballDX = 5; // Ball's speed in the x-direction
    private double ballDY = -5; // Ball's speed in the y-direction
    private static final double MAX_SPEED = 15; // Cap the ball's speed

    private boolean gameRunning = true;
    private final List<Brick> bricks = new ArrayList<>();
    private boolean moveLeft = false;
    private boolean moveRight = false;
    private AnimationTimer timer;
    private int score = 0;

    private Consumer<Integer> scoreCallback; // Callback to report the score

    public void setScoreCallback(Consumer<Integer> callback) {
        this.scoreCallback = callback;
    }

    public void launchGame(Stage primaryStage) {
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        canvas.setFocusTraversable(true);

        Button restartButton = new Button("Restart");
        restartButton.setFont(new Font(20));
        restartButton.setStyle("-fx-background-color: #9400d3; -fx-text-fill: #d3d3d3;");
        restartButton.setLayoutX(WIDTH - 90);
        restartButton.setLayoutY(HEIGHT + 20);

        Button backButton = new Button("Back");
        backButton.setFont(new Font(20));
        backButton.setStyle("-fx-background-color: #ffa500; -fx-text-fill: black;");
        backButton.setLayoutX(10);
        backButton.setLayoutY(HEIGHT + 20);

        Button scoreLabel = new Button("Score: 0");
        scoreLabel.setFont(new Font(25));
        scoreLabel.setStyle("-fx-background-color: transparent; -fx-text-fill: blue; -fx-font-weight: bold;");
        scoreLabel.setLayoutX(WIDTH / 2 - 50);
        scoreLabel.setLayoutY(HEIGHT + 20);
        scoreLabel.setDisable(true);

        Pane root = new Pane(canvas, restartButton, backButton, scoreLabel);
        root.setStyle("-fx-background-color: transparent;");
        Scene scene = new Scene(root, WIDTH, HEIGHT + 75);

        createBricks();

        canvas.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.LEFT) {
                moveLeft = true;
            } else if (e.getCode() == KeyCode.RIGHT) {
                moveRight = true;
            }
        });

        canvas.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.LEFT) {
                moveLeft = false;
            } else if (e.getCode() == KeyCode.RIGHT) {
                moveRight = false;
            }
        });

        restartButton.setOnAction(e -> restartGame(gc, canvas, scoreLabel));
        backButton.setOnAction(e -> {
            if (scoreCallback != null) {
                scoreCallback.accept(score); // Report the final score before closing
            }
            primaryStage.close();
        });

        primaryStage.setTitle("Brick Breaker");
        primaryStage.setScene(scene);
        primaryStage.show();

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
                render(gc);
                scoreLabel.setText("Score: " + score);
                if (!gameRunning) {
                    this.stop();
                    if (scoreCallback != null) {
                        scoreCallback.accept(score); // Report the final score when the game ends
                    }
                }
            }
        };

        timer.start();
    }

    private void restartGame(GraphicsContext gc, Canvas canvas, Button scoreLabel) {
        gameRunning = true;
        paddleX = WIDTH / 2 - PADDLE_WIDTH / 2;
        ballX = WIDTH / 2;
        ballY = HEIGHT - 200;
        ballDX = 5;
        ballDY = -5;
        score = 0;
        scoreLabel.setText("Score: 0");
        bricks.clear();
        createBricks();
        canvas.requestFocus();
        timer.start();
    }

    private void createBricks() {
        int patternCols = 8;
        int patternRows = 5;
        int gridCount = 5;
        double gridSpacing = 20;
        double xOffset = 50;
        double yOffset = 50;

        for (int gridRow = 0; gridRow < 3; gridRow++) {
            for (int gridColumn = 0; gridColumn < gridCount; gridColumn++) {
                for (int i = 0; i < patternRows; i++) {
                    for (int j = 0; j < patternCols; j++) {
                        double x = j * (BRICK_WIDTH + BRICK_SPACING) + xOffset + gridColumn * (patternCols * (BRICK_WIDTH + BRICK_SPACING) + gridSpacing);
                        double y = i * (BRICK_HEIGHT + BRICK_SPACING) + yOffset + gridRow * (patternRows * (BRICK_HEIGHT + BRICK_SPACING) + gridSpacing);
                        Color color = (i + j) % 2 == 0 ? Color.ORANGE : Color.LIMEGREEN;
                        bricks.add(new Brick(x, y, color));
                    }
                }
            }
        }
    }

    private void update() {
        if (moveLeft) {
            paddleX = Math.max(0, paddleX - 15);
        }
        if (moveRight) {
            paddleX = Math.min(WIDTH - PADDLE_WIDTH, paddleX + 15);
        }

        ballX += ballDX;
        ballY += ballDY;

        if (ballX <= 0 || ballX >= WIDTH - BALL_SIZE) {
            ballDX *= -1;
        }
        if (ballY <= 0) {
            ballDY *= -1;
        }

        if (ballY + BALL_SIZE >= HEIGHT - PADDLE_HEIGHT &&
            ballX + BALL_SIZE >= paddleX &&
            ballX <= paddleX + PADDLE_WIDTH) {
            ballDY = -Math.abs(ballDY); // Ensure the ball moves upward after hitting the paddle
        }

        if (ballY > HEIGHT) {
            gameRunning = false;
            if (scoreCallback != null) {
                scoreCallback.accept(score); // Report the final score once
            }
        }

        List<Brick> bricksToRemove = new ArrayList<>();
        for (Brick brick : bricks) {
            if (ballX + BALL_SIZE > brick.x && ballX < brick.x + BRICK_WIDTH &&
                ballY + BALL_SIZE > brick.y && ballY < brick.y + BRICK_HEIGHT) {
                ballDY *= -1; // Reverse ball direction
                bricksToRemove.add(brick); // Mark brick for removal
                score += 10; // Increment score for this brick hit
                break; // Ensure only one brick is hit per frame
            }
        }
        bricks.removeAll(bricksToRemove);

        if (bricks.isEmpty()) {
            gameRunning = false;
            if (scoreCallback != null) {
                scoreCallback.accept(score); // Report the final score once
            }
        }

        ballDX = Math.max(-MAX_SPEED, Math.min(MAX_SPEED, ballDX));
        ballDY = Math.max(-MAX_SPEED, Math.min(MAX_SPEED, ballDY));
    }

    private void render(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        gc.setFill(Color.LIGHTBLUE);
        gc.fillRect(paddleX, HEIGHT - PADDLE_HEIGHT, PADDLE_WIDTH, PADDLE_HEIGHT);

        gc.setFill(Color.RED);
        gc.fillOval(ballX, ballY, BALL_SIZE, BALL_SIZE);

        for (Brick brick : bricks) {
            gc.setFill(brick.color);
            gc.fillRect(brick.x, brick.y, BRICK_WIDTH, BRICK_HEIGHT);
        }

        if (!gameRunning) {
            gc.setFill(Color.WHITE);
            gc.setFont(new Font(36));
            String message = bricks.isEmpty() ? "You Win!" : "Game Over";
            gc.fillText(message, WIDTH / 2 - message.length() * 10, HEIGHT / 2);
        }
    }

    private static class Brick {
        double x, y;
        Color color;

        Brick(double x, double y, Color color) {
            this.x = x;
            this.y = y;
            this.color = color;
        }
    }
    
    
}
