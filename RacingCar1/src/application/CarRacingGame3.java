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

public class CarRacingGame3 extends Application {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int ROAD_WIDTH = 400;
    private static final int ROAD_X = (WIDTH - ROAD_WIDTH) / 2;
    private static final int CAR_WIDTH = 150;
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

    private Button runAgainButton;

    @Override
    public void start(Stage primaryStage) {
        Pane root = new Pane();
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        root.getChildren().add(canvas);

        GraphicsContext gc = canvas.getGraphicsContext2D();

        runAgainButton = new Button("Run Again");
        runAgainButton.setLayoutX(WIDTH / 2 - 50);
        runAgainButton.setLayoutY(HEIGHT / 2 + 100);
        runAgainButton.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 10px;");
        runAgainButton.setVisible(false);
        runAgainButton.setOnAction(e -> resetGame());

        root.getChildren().add(runAgainButton);

        Scene scene = new Scene(root);

        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case LEFT -> leftPressed = true;
                case RIGHT -> rightPressed = true;
            }
        });

        scene.setOnKeyReleased(event -> {
            switch (event.getCode()) {
                case LEFT -> leftPressed = false;
                case RIGHT -> rightPressed = false;
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

        primaryStage.setTitle("Car Racing Game");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void resetGame() {
        playerX = WIDTH / 2 - CAR_WIDTH / 2;
        playerY = HEIGHT - CAR_HEIGHT - 20;
        obstacles.clear();
        score = 0;
        gameOver = false;
        runAgainButton.setVisible(false);
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

        // Generate new obstacles periodically
        if (now - lastObstacleTime > 1_000_000_000) { // Every 1 second
            obstacles.add(new Obstacle(random.nextInt(ROAD_WIDTH - CAR_WIDTH) + ROAD_X, -CAR_HEIGHT));
            lastObstacleTime = now;
        }
        
        /*
         * 
         */

        // Update obstacle positions and check for collisions
        List<Obstacle> toRemove = new ArrayList<>();
        for (Obstacle obstacle : obstacles) {
            obstacle.y += 5; // Move obstacle down

            if (obstacle.y > HEIGHT) { // Remove obstacles that move off the screen
                toRemove.add(obstacle);
                score += 10; // Increase score for dodging an obstacle
            }

            // Check collision between player and obstacle
            if (checkCollision(obstacle)) {
                gameOver = true;
                runAgainButton.setVisible(true);
            }
        }
        obstacles.removeAll(toRemove); // Remove off-screen obstacles
    }


    private boolean checkCollision(Obstacle obstacle) {
        return playerX < obstacle.x + CAR_WIDTH &&
               playerX + CAR_WIDTH > obstacle.x &&
               playerY < obstacle.y + CAR_HEIGHT &&
               playerY + CAR_HEIGHT > obstacle.y;
    }

    private void render(GraphicsContext gc) {
        // Clear screen
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        // Draw road
        gc.setFill(Color.DARKGRAY);
        gc.fillRect(ROAD_X, 0, ROAD_WIDTH, HEIGHT);

        // Draw dashed line on the road
        gc.setFill(Color.YELLOW);
        for (int i = 0; i < HEIGHT; i += 40) {
            gc.fillRect(WIDTH / 2 - 5, i, 10, 20);
        }

        // Draw player car
        drawCar(gc, playerX, playerY, Color.BLUE);

        // Draw obstacles
        for (Obstacle obstacle : obstacles) {
            drawCar(gc, obstacle.x, obstacle.y, Color.RED);
        }

        // Draw score
        gc.setFont(new Font(20));
        gc.setFill(Color.BLACK);
        gc.fillText("Score: " + score, 10, 20);

        // Game over message
        if (gameOver) {
            gc.setFill(Color.BLACK);
            gc.setFont(new Font(50));
            gc.fillText("GAME OVER", WIDTH / 2 - 150, HEIGHT / 2);
        }
    }


    private void drawCar(GraphicsContext gc, double carX, double carY, Color bodyColor) {
        // Car body
        gc.setFill(bodyColor);
        gc.fillRect(carX, carY, 50, 100);

        // Cockpit
        gc.setFill(Color.BLACK);
        gc.fillRect(carX + 10, carY + 10, 30, 60);

        // Headlights
        gc.setFill(Color.YELLOW);
        gc.fillOval(carX + 5, carY - 5, 10, 10); // Left headlight
        gc.fillOval(carX + 35, carY - 5, 10, 10); // Right headlight

        // Rear lights
        gc.setFill(Color.RED);
        gc.fillOval(carX + 5, carY + 95, 10, 10); // Left rear light
        gc.fillOval(carX + 35, carY + 95, 10, 10); // Right rear light

        // Wheels
        gc.setFill(Color.BLACK);
        gc.fillOval(carX - 5, carY + 15, 10, 30); // Left front wheel
        gc.fillOval(carX + 45, carY + 15, 10, 30); // Right front wheel
        gc.fillOval(carX - 5, carY + 55, 10, 30); // Left rear wheel
        gc.fillOval(carX + 45, carY + 55, 10, 30); // Right rear wheel
    }


    private static class Obstacle {
        double x, y;

        Obstacle(double x, double y) {
            this.x = x;
            this.y = y;
        }

        void draw(GraphicsContext gc) {
            drawCar(gc, x, y);
        }

        private void drawCar(GraphicsContext gc, double carX, double carY) {
            // Use the same car design as the player
            gc.setFill(Color.CYAN);
            gc.fillOval(carX + 10, carY + 70, 120, 10);

            gc.setFill(Color.RED);
            gc.fillPolygon(
                new double[]{carX, carX + 150, carX + 130, carX + 20},
                new double[]{carY + 50, carY + 50, carY + 30, carY + 30},
                4
            );
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
