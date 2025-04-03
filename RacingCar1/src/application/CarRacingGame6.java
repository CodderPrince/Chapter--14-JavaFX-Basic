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

public class CarRacingGame6 extends Application {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 850;
    private static final int ROAD_WIDTH = 400;
    private static final int ROAD_X = (WIDTH - ROAD_WIDTH) / 2;
    private static final int CAR_WIDTH = 50;
    private static final int CAR_HEIGHT = 100;

    private double playerX = WIDTH / 2 - CAR_WIDTH / 2;
    private double playerY = HEIGHT - CAR_HEIGHT - 20;

    // my car speed when press the arrow
    private double playerSpeed = 10;

    /*
     * initialize all button are false
     * so that until press any arrow it not work
     */
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean upPressed = false;
    private boolean downPressed = false;

    // use arraylist to store how many obstacles passed
    private List<Obstacle> obstacles = new ArrayList<>();
    private Random random = new Random();
    private long lastObstacleTime = 0;
    private long score = 0;

    private boolean gameOver = false;
    /*
     * that means the is running
     * when any collision then true it
     * for again run
     */
    private Button runAgainButton;

    @Override

    /**************
     * Main Window
     **************/
    public void start(Stage primaryStage) {
        Pane root = new Pane();
        Canvas canvas = new Canvas(WIDTH, HEIGHT);// road height and width
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

        // when release the button then false it
        // arrow button press
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case LEFT -> leftPressed = true;
                case RIGHT -> rightPressed = true;
                case UP -> upPressed = true;
                case DOWN -> downPressed = true;
            }
        });

        // when release the arrow button then not work needed
        scene.setOnKeyReleased(event -> {
            switch (event.getCode()) {
                case LEFT -> leftPressed = false;
                case RIGHT -> rightPressed = false;
                case UP -> upPressed = false;
                case DOWN -> downPressed = false;
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

    /*********************
     * reset game menu
     *********************/
    private void resetGame() {
        playerX = WIDTH / 2 - CAR_WIDTH / 2;
        playerY = HEIGHT - CAR_HEIGHT - 20;
        obstacles.clear();
        score = 0;
        gameOver = false;
        runAgainButton.setVisible(false);
        // now again hide the run again button
    }

    /***************
     * update menu
     ***************/
    private void update(long now) {
        if (gameOver)
            return;

        // Move player car left
        if (leftPressed) {
            playerX -= playerSpeed;
            if (playerX < ROAD_X)
                playerX = ROAD_X;
        }

        // Move player car right
        if (rightPressed) {
            playerX += playerSpeed;
            if (playerX + CAR_WIDTH > ROAD_X + ROAD_WIDTH) {
                playerX = ROAD_X + ROAD_WIDTH - CAR_WIDTH;
            }
        }

        // Move player car up
        if (upPressed) {
            playerY -= playerSpeed;
            if (playerY < 0)
                playerY = 0; // Prevent moving off the top of the screen
        }

        // Move player car down
        if (downPressed) {
            playerY += playerSpeed;
            if (playerY + CAR_HEIGHT > HEIGHT) {
                playerY = HEIGHT - CAR_HEIGHT; // Prevent moving off the bottom of the screen
            }
        }

        // Generate new obstacles periodically
        if (now - lastObstacleTime > 1_000_000_000) { // Every 1 second
            obstacles.add(new Obstacle(random.nextInt(ROAD_WIDTH - CAR_WIDTH) + ROAD_X, -CAR_HEIGHT));
            lastObstacleTime = now;
        }

        // Update obstacle positions and check for collisions
        List<Obstacle> toRemove = new ArrayList<>();
        for (Obstacle obstacle : obstacles) {
            obstacle.y += 5; // Move obstacle down speed

            if (obstacle.y > HEIGHT) { // Remove obstacles that move off the screen
                toRemove.add(obstacle);
                score += 10; // Increase score for dodging an obstacle
            }

            // Check collision between player and obstacle
            if (checkCollision(obstacle)) {
                gameOver = true;// collision occured means end the game
                runAgainButton.setVisible(true);
                /*
                 * when collision happend
                 * that means game over
                 * now show the run again button
                 * so that again run
                 */
            }
        }
        obstacles.removeAll(toRemove); // Remove off-screen obstacles
    }

    /***************************
     * Check Collision Function
     ***************************/
    private boolean checkCollision(Obstacle obstacle) {
        // Check if the player's car overlaps with the obstacle

        // Condition 1: Player's left side is to the left of the obstacle's right side
        if (playerX >= obstacle.x + CAR_WIDTH) {
            return false; // No collision
        }

        // Condition 2: Player's right side is to the right of the obstacle's left side
        if (playerX + CAR_WIDTH <= obstacle.x) {
            return false; // No collision
        }

        // Condition 3: Player's top side is above the obstacle's bottom side
        if (playerY >= obstacle.y + CAR_HEIGHT) {
            return false; // No collision
        }

        // Condition 4: Player's bottom side is below the obstacle's top side
        if (playerY + CAR_HEIGHT <= obstacle.y) {
            return false; // No collision
        }

        // If none of the conditions indicate "no collision," then there is a collision
        return true;
    }

    /*************************************
     * Render Function Works for Graphics
     *************************************/
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

    /******************
     * Draw Car Window
     *****************/
    private void drawCar(GraphicsContext gc, double carX, double carY, Color bodyColor) {
        // Car body
        gc.setFill(bodyColor);
        gc.fillRect(carX, carY, CAR_WIDTH, CAR_HEIGHT);

        // Cockpit
        gc.setFill(Color.BLACK);
        gc.fillRect(carX + 10, carY + 10, 30, 60);

        // Headlights
        gc.setFill(Color.YELLOW);
        gc.fillOval(carX + 5, carY - 5, 10, 10); // Left headlight
        gc.fillOval(carX + 35, carY - 5, 10, 10); // Right headlight

        // Rear lights
        gc.setFill(Color.RED);
        gc.fillOval(carX + 5, carY + CAR_HEIGHT - 5, 10, 10); // Left rear light
        gc.fillOval(carX + 35, carY + CAR_HEIGHT - 5, 10, 10); // Right rear light

        // Wheels
        gc.setFill(Color.BLACK);
        gc.fillOval(carX - 5, carY + 15, 10, 30); // Left front wheel
        gc.fillOval(carX + CAR_WIDTH - 5, carY + 15, 10, 30); // Right front wheel
        gc.fillOval(carX - 5, carY + 55, 10, 30); // Left rear wheel
        gc.fillOval(carX + CAR_WIDTH - 5, carY + 55, 10, 30); // Right rear wheel
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
