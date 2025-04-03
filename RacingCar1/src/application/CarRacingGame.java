package application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CarRacingGame extends Application {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int ROAD_WIDTH = 400;
    private static final int ROAD_X = (WIDTH - ROAD_WIDTH) / 2;
    private static final int CAR_WIDTH = 50;
    private static final int CAR_HEIGHT = 100;

    private double playerX = WIDTH / 2 - CAR_WIDTH / 2;
    private double playerY = HEIGHT - CAR_HEIGHT - 20;
    private double playerSpeed = 5;

    private boolean leftPressed = false;
    private boolean rightPressed = false;

    private List<Obstacle> obstacles = new ArrayList<>();
    private Random random = new Random();
    private long lastObstacleTime = 0;
    private long score = 0;

    private boolean gameOver = false;

    @Override
    public void start(Stage primaryStage) {
        Pane root = new Pane();
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        root.getChildren().add(canvas);

        Scene scene = new Scene(root);

        scene.setOnKeyPressed(this::handleKeyPressed);
        scene.setOnKeyReleased(this::handleKeyReleased);

        GraphicsContext gc = canvas.getGraphicsContext2D();

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update(now);
                render(gc);
            }
        };

        timer.start();

        primaryStage.setTitle("Car Racing Game");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void handleKeyPressed(KeyEvent event) {
        switch (event.getCode()) {
            case LEFT -> leftPressed = true;
            case RIGHT -> rightPressed = true;
        }
    }

    private void handleKeyReleased(KeyEvent event) {
        switch (event.getCode()) {
            case LEFT -> leftPressed = false;
            case RIGHT -> rightPressed = false;
        }
    }

    private void update(long now) {
        if (gameOver) return;

        if (leftPressed) {
            playerX -= playerSpeed;
            if (playerX < ROAD_X) playerX = ROAD_X;
        }
        if (rightPressed) {
            playerX += playerSpeed;
            if (playerX + CAR_WIDTH > ROAD_X + ROAD_WIDTH) playerX = ROAD_X + ROAD_WIDTH - CAR_WIDTH;
        }

        if (now - lastObstacleTime > 1_000_000_000) {
            obstacles.add(new Obstacle(random.nextInt(ROAD_WIDTH - CAR_WIDTH) + ROAD_X, -CAR_HEIGHT));
            lastObstacleTime = now;
        }

        List<Obstacle> toRemove = new ArrayList<>();
        for (Obstacle obstacle : obstacles) {
            obstacle.y += 5;
            if (obstacle.y > HEIGHT) {
                toRemove.add(obstacle);
                score += 10;
            }
            if (checkCollision(obstacle)) {
                gameOver = true;
            }
        }
        obstacles.removeAll(toRemove);
    }

    private boolean checkCollision(Obstacle obstacle) {
        return playerX < obstacle.x + CAR_WIDTH &&
               playerX + CAR_WIDTH > obstacle.x &&
               playerY < obstacle.y + CAR_HEIGHT &&
               playerY + CAR_HEIGHT > obstacle.y;
    }

    private void render(GraphicsContext gc) {
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        gc.setFill(Color.DARKGRAY);
        gc.fillRect(ROAD_X, 0, ROAD_WIDTH, HEIGHT);

        gc.setFill(Color.YELLOW);
        for (int i = 0; i < HEIGHT; i += 40) {
            gc.fillRect(WIDTH / 2 - 5, i, 10, 20);
        }

        gc.setFill(Color.RED);
        gc.fillRect(playerX, playerY, CAR_WIDTH, CAR_HEIGHT);

        gc.setFill(Color.BLUE);
        for (Obstacle obstacle : obstacles) {
            gc.fillRect(obstacle.x, obstacle.y, CAR_WIDTH, CAR_HEIGHT);
        }

        gc.setFill(Color.BLACK);
        gc.setFont(new Font(20));
        gc.fillText("Score: " + score, 10, 20);

        if (gameOver) {
            gc.setFill(Color.BLACK);
            gc.setFont(new Font(50));
            gc.fillText("GAME OVER", WIDTH / 2 - 150, HEIGHT / 2);
        }
    }

    private static class Obstacle {
        double x, y;

        Obstacle(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
