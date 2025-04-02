package application;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class FlappyBird {

    private static final int WIDTH = 1500;
    private static final int HEIGHT = 750;
    private static final int PIPE_WIDTH = 50;
    private static final int PIPE_GAP = 350;

    private double birdY = HEIGHT / 2;
    private double birdVelocity = 0;
    private final double gravity = 0.3;
    private final double jumpStrength = -8;
    private boolean gameOver = false;
    private int score = 0;

    private final List<double[]> pipes = new ArrayList<>();
    private long lastPipeTime = 0;

    private Image birdImage;
    private Image backgroundImage;
    private double birdBounceVelocity = 0.5;

    private Consumer<Integer> scoreCallback;

    public void setScoreCallback(Consumer<Integer> callback) {
        this.scoreCallback = callback;
    }

    public void start(Stage primaryStage) {
        Pane root = new Pane();
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        root.getChildren().add(canvas);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Load images
        birdImage = new Image(getClass().getResource("/resources/bird.png").toString());
        backgroundImage = new Image(getClass().getResource("/resources/background.png").toString());

        // Back button
        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-font-size: 16px;");
        backButton.setLayoutX(10);
        backButton.setLayoutY(40);
        backButton.setOnAction(e -> primaryStage.close());
        root.getChildren().add(backButton);

        Scene scene = new Scene(root);

        // Ensure canvas gets focus for key events
        canvas.setFocusTraversable(true);
        canvas.requestFocus();

        // Key press handler
        scene.setOnKeyPressed(event -> {
            if (!gameOver) {
                if (event.getCode() == KeyCode.SPACE || event.getCode() == KeyCode.UP) {
                    birdVelocity = jumpStrength;
                }
            } else if (event.getCode() == KeyCode.R) {
                resetGame();
            }
        });

        primaryStage.setScene(scene);
        primaryStage.setTitle("Flappy Bird");
        primaryStage.show();

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update(gc);
            }
        };
        timer.start();
    }

    private void update(GraphicsContext gc) {
        // Draw background
        gc.drawImage(backgroundImage, 0, 0, WIDTH, HEIGHT);

        if (gameOver) {
            birdY += birdBounceVelocity;
            if (birdY < HEIGHT / 2 - 20 || birdY > HEIGHT / 2 + 20) {
                birdBounceVelocity = -birdBounceVelocity;
            }

            gc.setFill(Color.RED);
            gc.setFont(new Font(50));
            gc.fillText("Game Over", WIDTH / 2 - 140, HEIGHT / 2 - 50);

            gc.setFont(new Font(40));
            gc.setFill(Color.BLUE);
            gc.fillText("Score: " + score, WIDTH / 2 - 100, HEIGHT / 2);

            gc.setFont(new Font(30));
            gc.setFill(Color.BLACK);
            gc.fillText("Press R to Restart", WIDTH / 2 - 130, HEIGHT / 2 + 50);

            if (scoreCallback != null) {
                scoreCallback.accept(score);
            }

            return;
        }

        // Update bird position
        birdVelocity += gravity;
        birdY += birdVelocity;

        // Check collision with ground or ceiling
        if (birdY > HEIGHT - birdImage.getHeight() || birdY < 0) {
            gameOver = true;
        }

        // Draw bird
        gc.drawImage(birdImage, 100, birdY, 60, 60);

        // Manage pipes
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastPipeTime > 2500) {
            double pipeY = Math.random() * (HEIGHT - PIPE_GAP - 100) + 50;
            pipes.add(new double[]{WIDTH, pipeY});
            lastPipeTime = currentTime;
        }

        // Update and draw pipes
        Iterator<double[]> iterator = pipes.iterator();
        while (iterator.hasNext()) {
            double[] pipe = iterator.next();
            pipe[0] -= 2;

            // Create gradient for pipes
            LinearGradient gradient = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                    new Stop(0, Color.DARKVIOLET), new Stop(1, Color.BLUE));
            gc.setFill(gradient);

            // Draw top and bottom pipes with fade effect
            gc.setGlobalAlpha(0.8);
            gc.fillRoundRect(pipe[0], 0, PIPE_WIDTH, pipe[1], 10, 10);
            gc.fillRoundRect(pipe[0], pipe[1] + PIPE_GAP, PIPE_WIDTH, HEIGHT - pipe[1] - PIPE_GAP, 10, 10);
            gc.setGlobalAlpha(1.0);

            // Check for collisions
            if (pipe[0] < 120 && pipe[0] + PIPE_WIDTH > 100) {
                if (birdY < pipe[1] || birdY + 30 > pipe[1] + PIPE_GAP) {
                    gameOver = true;
                }
            }

            // Remove off-screen pipes
            if (pipe[0] + PIPE_WIDTH < 0) {
                iterator.remove();
                score++;
            }
        }

        // Display score during gameplay
        gc.setFill(Color.DARKBLUE);
        gc.setFont(new Font(20));
        gc.fillText("Score: " + score, 10, 20);
    }

    private void resetGame() {
        birdY = HEIGHT / 2;
        birdVelocity = 0;
        gameOver = false;
        score = 0;
        pipes.clear();
        lastPipeTime = 0;
    }
    
    
}
