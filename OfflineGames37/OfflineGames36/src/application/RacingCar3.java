/**********************
 * Car Racing Game
 * Developed by PRINCE
 * ID: 12105007
 **********************/

package application;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RacingCar3 extends Application {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 800;
    private static final int ROAD_WIDTH = 400;
    private static final int ROAD_X = (WIDTH - ROAD_WIDTH) / 2;
    private static final int CAR_WIDTH = 50;
    private static final int CAR_HEIGHT = 100;

    private double playerX = WIDTH / 2 - CAR_WIDTH / 2;
    private double playerY = HEIGHT - CAR_HEIGHT - 20;

    private double playerSpeed = 10;

    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean upPressed = false;
    private boolean downPressed = false;
    private boolean jumpPressed = false;

    private List<Obstacle> obstacles = new ArrayList<>();
    private Random random = new Random();
    private long lastObstacleTime = 0;
    private long score = 0;

    private boolean gameOver = false;
    private Button runAgainButton;

    private boolean isJumping = false;
    private double jumpY = 0;
    private double jumpSpeed = -35;
    private double gravity = 1;

    @Override
    public void start(Stage primaryStage) {
        Pane root = new Pane();
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        root.getChildren().add(canvas);

        GraphicsContext gc = canvas.getGraphicsContext2D();

        runAgainButton = new Button("Run Again");
        runAgainButton.setLayoutX(WIDTH / 2 - 50);
        runAgainButton.setLayoutY(HEIGHT / 2 + 100);
        runAgainButton
                .setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 10px;");
        runAgainButton.setVisible(false);// intially hidden
        runAgainButton.setOnAction(e -> resetGame());
        /*
         * when click then start the new game
         */

        root.getChildren().add(runAgainButton);

        Scene scene = new Scene(root);

        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case LEFT -> leftPressed = true;
                case RIGHT -> rightPressed = true;
                case UP -> upPressed = true;
                case DOWN -> downPressed = true;
                case SPACE -> jumpPressed = true;
            }
        });

        scene.setOnKeyReleased(event -> {
            switch (event.getCode()) {
                case LEFT -> leftPressed = false;
                case RIGHT -> rightPressed = false;
                case UP -> upPressed = false;
                case DOWN -> downPressed = false;
                case SPACE -> jumpPressed = false;
            }
        });

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update(now);
                render(gc);
            }
        };

        timer.start();

        primaryStage.setTitle("Car Racing Game | Developed by PRINCE");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void resetGame() {
        playerX = WIDTH / 2 - CAR_WIDTH / 2;
        playerY = HEIGHT - CAR_HEIGHT - 20;
        obstacles.clear();
        score = 0;
        gameOver = false;
        isJumping = false;
        jumpY = 0;
        runAgainButton.setVisible(false);
    }

    private void update(long now) {
        if (gameOver)
            return;

        // X-axis movement
        if (leftPressed) {
            playerX -= playerSpeed;
            if (playerX < ROAD_X)
                playerX = ROAD_X;
        }

        if (rightPressed) {
            playerX += playerSpeed;
            if (playerX + CAR_WIDTH > ROAD_X + ROAD_WIDTH) {
                playerX = ROAD_X + ROAD_WIDTH - CAR_WIDTH;
            }
        }
         // Y-axis movement (handling jump)
         if (upPressed) {
                playerY -= playerSpeed;
                if (playerY < 0)
                    playerY = 0;
            }

            if (downPressed) {
                playerY += playerSpeed;
                if (playerY + CAR_HEIGHT > HEIGHT) {
                    playerY = HEIGHT - CAR_HEIGHT;
                }
            }


        if (jumpPressed && !isJumping) {
            isJumping = true;
            jumpY = playerY;
        }

        if (isJumping) {
            playerY += jumpSpeed;
            jumpSpeed += gravity;

            if (playerY >= jumpY) {
                playerY = jumpY;
                isJumping = false;
                jumpSpeed = -35;
            }
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

            if (!isJumping && checkCollision(obstacle)) {
                gameOver = true;
                runAgainButton.setVisible(true);
            }
        }
        obstacles.removeAll(toRemove);
    }

    private boolean checkCollision(Obstacle obstacle) {
        if (playerX >= obstacle.x + CAR_WIDTH) {
            return false;
        }

        if (playerX + CAR_WIDTH <= obstacle.x) {
            return false;
        }

        if (playerY >= obstacle.y + CAR_HEIGHT) {
            return false;
        }

        if (playerY + CAR_HEIGHT <= obstacle.y) {
            return false;
        }

        return true;
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

        drawCar(gc, playerX, playerY, Color.BLUE);

        for (Obstacle obstacle : obstacles) {
            drawCar(gc, obstacle.x, obstacle.y, Color.RED);
        }

        gc.setFont(new Font(20));
        gc.setFill(Color.BLACK);
        gc.fillText("Score: " + score, 10, 20);

        if (gameOver) {
            gc.setFill(Color.BLACK);
            gc.setFont(new Font(50));
            gc.fillText("GAME OVER", WIDTH / 2 - 150, HEIGHT / 2);
        }
    }

    private void drawCar(GraphicsContext gc, double carX, double carY, Color bodyColor) {
        gc.setFill(bodyColor);
        gc.fillRect(carX, carY, CAR_WIDTH, CAR_HEIGHT);

        gc.setFill(Color.BLACK);
        gc.fillRect(carX + 10, carY + 10, 30, 60);

        gc.setFill(Color.YELLOW);
        gc.fillOval(carX + 5, carY - 5, 10, 10);
        gc.fillOval(carX + 35, carY - 5, 10, 10);

        gc.setFill(Color.RED);
        gc.fillOval(carX + 5, carY + CAR_HEIGHT - 5, 10, 10);
        gc.fillOval(carX + 35, carY + CAR_HEIGHT - 5, 10, 10);

        gc.setFill(Color.BLACK);
        gc.fillOval(carX - 5, carY + 15, 10, 30);
        gc.fillOval(carX + CAR_WIDTH - 5, carY + 15, 10, 30);
        gc.fillOval(carX - 5, carY + 55, 10, 30);
        gc.fillOval(carX + CAR_WIDTH - 5, carY + 55, 10, 30);
    }

    private static class Obstacle {
        double x, y;

        Obstacle(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    
}
