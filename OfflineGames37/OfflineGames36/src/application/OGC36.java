package application;

import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class OGC36 extends Application {
    private UserManager userManager = new UserManager();
    private User currentUser;
    private Stage primaryStage;
    private Map<String, Integer> leaderboard = new HashMap<>();
    private Map<String, String> lastLoginTimes = new HashMap<>();
    private List<String> previousUsers = new ArrayList<>();
    private ComboBox<String> userComboBox = new ComboBox<>();
    private Label lastLoginLabel = new Label();
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Offline Games Collection");
        loadLeaderboardFromFile();
        loadPreviousUsers();
        showAccountCreationWindow();
    }

    private void showAccountCreationWindow() {
        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #34495e;"); // Set the background color of the VBox

        Label nameLabel = new Label("Enter your name:");
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        nameLabel.setTextFill(Color.WHITE); // Set the text color to white
        TextField nameField = new TextField();
        nameField.setFont(Font.font("Arial", FontWeight.NORMAL, 16)); // Increase font size
        nameField.setStyle("-fx-background-color: #ecf0f1; -fx-text-fill: #2c3e50;"); // Set background and text color of text field

        Label ageLabel = new Label("Enter your age:");
        ageLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        ageLabel.setTextFill(Color.WHITE); // Set the text color to white
        TextField ageField = new TextField();
        ageField.setFont(Font.font("Arial", FontWeight.NORMAL, 16)); // Increase font size
        ageField.setStyle("-fx-background-color: #ecf0f1; -fx-text-fill: #2c3e50;"); // Set background and text color of text field


        // Initialize the combo box
        userComboBox.setPromptText("Select Previous User");
        userComboBox.setItems(FXCollections.observableArrayList(previousUsers));
        userComboBox.setMaxWidth(200);

        userComboBox.setOnAction(e -> {
           String selectedUser = userComboBox.getValue();
            if (selectedUser != null) {
                String[] parts = selectedUser.split(", ");
                if (parts.length == 2) {
                    nameField.setText(parts[0].substring(parts[0].indexOf(":") + 1).trim());
                    ageField.setText(parts[1].substring(parts[1].indexOf(":") + 1).trim());
                    
                    String lastLogin = lastLoginTimes.get(selectedUser);
                    if(lastLogin != null) {
                    	lastLoginLabel.setText("Last Login: " + lastLogin);
                    	System.out.println("Last login time for " + selectedUser + ": " + lastLogin);
                    } else {
                    	lastLoginLabel.setText("");
                    	System.out.println("No login time data for " + selectedUser);
                    }
                }
            }
        });

        Button loginButton = createStyledButton("Login", "#3498db", "#2980b9");
        Button createAccountButton = createStyledButton("Create Account", "#2ecc71", "#27ae60");
        Button leaderboardButton = createStyledButton("Leaderboard", "#9b59b6", "#8e44ad");

        root.getChildren().addAll(userComboBox, lastLoginLabel, nameLabel, nameField, ageLabel, ageField, loginButton, createAccountButton, leaderboardButton);

        loginButton.setOnAction(e -> {
            try {
            	String name = nameField.getText();
                int age = Integer.parseInt(ageField.getText());
                
                currentUser = userManager.getUser(name, age);
                if (currentUser != null) {
                    saveUserLogin(name, age);
                    showGameSelectionWindow();
                    
                } else {
                    showAlert("Error", "User not found, please create an account.");
                }
            } catch (NumberFormatException ex) {
                showAlert("Error", "Invalid age entered.");
            }
        });

        createAccountButton.setOnAction(e -> {
             try {
            	 String name = nameField.getText();
                 int age = Integer.parseInt(ageField.getText());
                
                if(userManager.getUser(name, age) == null) {
                	 currentUser = new User(name, age);
                     userManager.addUser(currentUser);
                     saveUserLogin(name, age);
                     showGameSelectionWindow();
                } else {
                     Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                     alert.setTitle("Account Exists");
                     alert.setHeaderText("An account with this name and age already exists");
                     alert.setContentText("Do you want to Login instead?");

                     Optional<ButtonType> result = alert.showAndWait();
                     if(result.isPresent() && result.get() == ButtonType.OK) {
                        currentUser = userManager.getUser(name, age);
                        saveUserLogin(name, age);
                        showGameSelectionWindow();
                     }
                 }

             } catch (NumberFormatException ex) {
                showAlert("Error", "Invalid age entered.");
             }
        });

        leaderboardButton.setOnAction(e -> showLeaderboardWindow());

        Scene scene = new Scene(root, 400, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void saveUserLogin(String name, int age) {
       
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = now.format(formatter);

        //Save the user's last login time to the map
        String userKey = "Name: " + name + ", Age: " + age;
        lastLoginTimes.put(userKey, formattedDateTime);


        File file = new File("users.txt");
         try (PrintWriter out = new PrintWriter(new FileWriter(file,true))) {
            out.println("Name: " + name + ", Age: " + age + ", Last Login: " + formattedDateTime);
            
         } catch(IOException e){
            System.out.println("Error writing to user file" + e.getMessage());
            e.printStackTrace();
         }
     }

    private void loadPreviousUsers() {
         File file = new File("users.txt");
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            previousUsers.clear();
            lastLoginTimes.clear();
            while ((line = reader.readLine()) != null) {
                 String[] parts = line.split(", Last Login: ");
                 if(parts.length == 2) {
                     String userDetails = parts[0].trim();
                     String lastLogin = parts[1].trim();
                    previousUsers.add(userDetails);
                    lastLoginTimes.put(userDetails, lastLogin);
                 }

            }
        } catch (IOException e) {
            System.out.println("Failed to load user data: " + e.getMessage());
            // Ignore if file not found
        }
    }

    private void showGameSelectionWindow() {
        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);

        // Example game button
        Button btnTicTacToe = createStyledButton("Tic Tac Toe", "#8B4513", "#A0522D");
    	btnTicTacToe.setOnAction(e -> launchTicTacToe());
        
        Button btnSudokuSolver = createStyledButton("Sudoku Solver", "#32cd32", "#228b22");
        btnSudokuSolver.setOnAction(e -> {
            launchSudokuSolver();
            updateLeaderboard("Sudoku Solver", new Random().nextInt(500));
        });

        Button btnSnakeGame = createStyledButton("Snake Game", "#1e90ff", "#00008b");
        btnSnakeGame.setOnAction(e -> {
            launchSnakeGame();
            updateLeaderboard("Snake Game", new Random().nextInt(500));
        });

        Button btnMinesweeper = createStyledButton("Minesweeper", "#8B0000", "#660000");
        btnMinesweeper.setOnAction(e -> {
            launchMinesweeper();
            updateLeaderboard("Minesweeper", new Random().nextInt(500));
        });

        Button btnBrickBreaker = createStyledButton("Brick Breaker", "#ffa500", "#ff8c00");
        btnBrickBreaker.setOnAction(e -> {
            launchBrickBreaker();
            updateLeaderboard("Brick Breaker", new Random().nextInt(500));
        });

        Button btn2048Game = createStyledButton("2048 Game", "#8a2be2", "#4b0082");
        btn2048Game.setOnAction(e -> {
            launch2048Game();
            updateLeaderboard("2048 Game", new Random().nextInt(500));
        });

        Button btnFlappyBird = createStyledButton("Flappy Bird", "#ff6347", "#ff0000");
        btnFlappyBird.setOnAction(e -> {
            launchFlappyBird();
            updateLeaderboard("Flappy Bird", new Random().nextInt(500));
        });

        Button btnHangman = createStyledButton("Hangman Game", "#87ceeb", "#4682b4");
        btnHangman.setOnAction(e -> {
            launchHangman();
            updateLeaderboard("Hangman", new Random().nextInt(500));
        });

        Button btnMemoryMatch = createStyledButton("Memory Match", "#843C0C", "#dc143c");
        btnMemoryMatch.setOnAction(e -> {
            launchMemoryMatch();
            updateLeaderboard("Memory Match", new Random().nextInt(500));
        });

        Button btnTowerOfHanoi = createStyledButton("Tower of Hanoi", "#00FFFF", "#008B8B");
        btnTowerOfHanoi.setOnAction(e -> {
            launchTowerOfHanoi();
            updateLeaderboard("Tower of Hanoi", new Random().nextInt(500));
        });

        Button btnBeanMachine = createStyledButton("Bean Machine", "#003300", "#004d00");
        btnBeanMachine.setOnAction(e -> {
            launchBeanMachine();
            updateLeaderboard("Bean Machine", new Random().nextInt(500));
        });

        Button btnNCS = createStyledButton("NCS", "#808080", "#696969");
        btnNCS.setOnAction(e -> {
            launchUBC();
            updateLeaderboard("NCS", new Random().nextInt(500));
        });

        Button btnPingPong = createStyledButton("Ping Pong Ball", "#ff69b4", "#c71585");
        btnPingPong.setOnAction(e -> {
            launchPingPong();
            updateLeaderboard("Ping Pong", new Random().nextInt(500));
        });

        Button btnRacingCar = createStyledButton("Racing Car", "#2F4F4F", "#1C1C1C");
        btnRacingCar.setOnAction(e -> {
            launchRacingCar();
            updateLeaderboard("Racing Car", new Random().nextInt(500));
        });

        Button backButton = createStyledButton("Back", "#FF4500", "#FF6347");
        backButton.setOnAction(e -> showAccountCreationWindow());

        // Left column buttons
        VBox leftColumn = new VBox(20);
        leftColumn.getChildren().addAll(btnTicTacToe, btnSudokuSolver, btnSnakeGame, btnMinesweeper, btnBrickBreaker, btnPingPong, btnBeanMachine);

        // Right column buttons
        VBox rightColumn = new VBox(20);
        rightColumn.getChildren().addAll(btn2048Game, btnFlappyBird, btnHangman, btnMemoryMatch, btnRacingCar, btnTowerOfHanoi, btnNCS);

        // Layout with two columns
        HBox buttonLayout = new HBox(40, leftColumn, rightColumn);
        buttonLayout.setAlignment(Pos.CENTER);

        VBox layout = new VBox(20, buttonLayout, backButton);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-alignment: center; -fx-background-color: #2c3e50;");

        // Scene and Stage setup
        Scene scene = new Scene(layout, 630, 650);
        scene.setFill(createGradientBackground());

        primaryStage.setTitle("Offline Games Collection - Game Selection");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

   
    private void loadLeaderboardFromFile() {
        File file = new File("leaderboard.txt");
        if (!file.exists()) {
            System.out.println("Leaderboard file does not exist. Creating a new one.");
            return; // Exit if the file doesn't exist to avoid FileNotFoundException
        }

        leaderboard.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(": ");
                if (parts.length == 2) {
                    String gameUser = parts[0].trim();
                    int score = Integer.parseInt(parts[1].trim());
                    leaderboard.put(gameUser, score);
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to load leaderboard data: " + e.getMessage());
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.out.println("Error parsing scores from file: " + e.getMessage());
        }
    }

    
    private void showLeaderboardWindow() {
        List<Map.Entry<String, Integer>> sortedLeaderboard = new ArrayList<>(leaderboard.entrySet());
        sortedLeaderboard.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        VBox leaderboardLayout = new VBox(10);
        leaderboardLayout.setAlignment(Pos.CENTER);
        leaderboardLayout.setStyle("-fx-background-color: #2c3e50; -fx-padding: 10;");

        Label titleLabel = new Label("Leaderboard");
        titleLabel.setFont(Font.font("Arial Rounded MT Bold", 20));
        titleLabel.setTextFill(Color.YELLOW);
        leaderboardLayout.getChildren().add(titleLabel);

        for (Map.Entry<String, Integer> entry : sortedLeaderboard) {
            Label scoreLabel = new Label(entry.getKey() + ": " + entry.getValue());
            scoreLabel.setFont(Font.font("Arial", 20));
            scoreLabel.setTextFill(Color.WHITE);
            leaderboardLayout.getChildren().add(scoreLabel);
        }

        // Using createStyledButton for both buttons
        Button backButton = createStyledButton("Back", "#FF6347", "#FF4500");  // Orange gradient
        backButton.setOnAction(e -> {
            primaryStage.hide(); // Hide the leaderboard stage
            showAccountCreationWindow(); // Show the account creation window
        });

        Button resetButton = createStyledButton("Reset", "#DC143C", "#B22222");  // Red gradient
        resetButton.setOnAction(e -> {
            leaderboard.clear(); // Clear the leaderboard map
            saveLeaderboardToFile(); // Save the empty leaderboard to file
            showLeaderboardWindow(); // Refresh the leaderboard window
        });

        HBox buttonRow = new HBox(10, backButton, resetButton);
        buttonRow.setAlignment(Pos.CENTER);
        leaderboardLayout.getChildren().add(buttonRow);

        Scene scene = new Scene(leaderboardLayout, 600, 500);
        Stage leaderboardStage = new Stage();
        leaderboardStage.setTitle("Leaderboard");
        leaderboardStage.setScene(scene);
        leaderboardStage.show();
    }


    private Button createStyledButton(String text, String startColor, String endColor) {
        Button button = new Button(text);
        button.setPrefWidth(250);
        button.setPrefHeight(60);
        button.setFont(Font.font("Arial Rounded MT Bold", 25));
        button.setTextFill(Color.WHITE);
        button.setStyle("-fx-background-color: linear-gradient(" + startColor + ", " + endColor + ");" +
                "-fx-background-radius: 15;" +
                "-fx-border-radius: 15;" +
                "-fx-border-color: white; -fx-border-width: 2;");
        DropShadow shadow = new DropShadow();
        shadow.setRadius(10);
        shadow.setColor(Color.GRAY);
        button.setEffect(shadow);

        button.setOnMouseEntered(e -> {
            ScaleTransition scaleUp = new ScaleTransition(Duration.millis(200), button);
            scaleUp.setToX(1.2);
            scaleUp.setToY(1.2);
            scaleUp.play();
        });
        button.setOnMouseExited(e -> {
            ScaleTransition scaleDown = new ScaleTransition(Duration.millis(200), button);
            scaleDown.setToX(1);
            scaleDown.setToY(1);
            scaleDown.play();
        });

        return button;
    }

    private LinearGradient createGradientBackground() {
        return new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#6a5acd")),
                new Stop(1, Color.web("#483d8b")));
    }
    
    private void updateLeaderboard(String game, int score) {
        // Get the current date and time
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = now.format(formatter);

        // Create a unique key for the user and game
        String userGameKey = game + " (" + currentUser.getName() + ")";

        // Always update the score with the latest and correct value
        leaderboard.put(userGameKey + " [" + formattedDateTime + "]", score);

        // Remove old entries for the same user and game
        leaderboard.entrySet().removeIf(entry -> entry.getKey().startsWith(userGameKey) && !entry.getKey().contains(formattedDateTime));

        // Save the updated leaderboard to file
        saveLeaderboardToFile();
    }


    private boolean scoreIsValid(int score) {
        // Example condition: Score must be non-negative and not unusually high
        return score >= 0 && score <= 1000;
    }






    
    
    private void saveLeaderboardToFile() {
        File file = new File("leaderboard.txt");
        try {
            if (!file.exists()) {
                file.createNewFile();
                System.out.println("Leaderboard file created successfully.");
            }
            try (PrintWriter out = new PrintWriter(new FileWriter(file))) {
                leaderboard.forEach((key, value) -> out.println(key + ": " + value));
                System.out.println("Leaderboard data saved successfully.");
            }
        } catch (IOException e) {
            System.out.println("Failed to create or write to leaderboard file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    
    
    private void launchGame(String gameName) {
        // Placeholder for launching a game
        System.out.println("Launching " + gameName + "...");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.setHeaderText(title);
        alert.showAndWait();
    }

    private void launchTicTacToe() {
        TicTacToe ticTacToe = new TicTacToe();
        ticTacToe.start(new Stage());
    }

    private void launchSudokuSolver() {
        SudokuSolver sudokuSolverApp = new SudokuSolver();
        Stage sudokuStage = new Stage();
        sudokuSolverApp.startApp(sudokuStage);
    }

    private void launchSnakeGame() {
        SnakeGame snakeGame = new SnakeGame();
        snakeGame.setScoreCallback(score -> {
            updateLeaderboard("Snake Game", score);
            System.out.println("Snake Game Score: " + score); // For debugging
        });
        snakeGame.start(new Stage());
    }


    private void launchMinesweeper() {
        Minesweeper minesweeperApp = new Minesweeper();
        Stage minesweeperStage = new Stage();
        minesweeperApp.start(minesweeperStage);
    }

    private void launchBrickBreaker() {
        BrickBreaker brickBreakerApp = new BrickBreaker();
        brickBreakerApp.setScoreCallback(score -> {
            updateLeaderboard("Brick Breaker", score);
            System.out.println("Brick Breaker Score: " + score); // For debugging
        });
        Stage brickBreakerStage = new Stage();
        brickBreakerApp.launchGame(brickBreakerStage);
    }


    private void launch2048Game() {
        Game2048 game2048App = new Game2048();
        Stage game2048Stage = new Stage();
        game2048App.start(game2048Stage);
    }

    private void launchFlappyBird() {
        FlappyBird flappyBirdApp = new FlappyBird();
        flappyBirdApp.setScoreCallback(score -> {
            updateLeaderboard("Flappy Bird", score);
            System.out.println("Flappy Bird Score: " + score); // For debugging
        });
        Stage flappyBirdStage = new Stage();
        flappyBirdApp.start(flappyBirdStage);
    }


    private void launchHangman() {
        HangmanGame hangmanApp = new HangmanGame();
        Stage hangmanStage = new Stage();
        hangmanApp.start(hangmanStage);
    }

    private void launchMemoryMatch() {
        MemoryMatchGame memoryMatchApp = new MemoryMatchGame();
        Stage memoryMatchStage = new Stage();
        memoryMatchApp.start(memoryMatchStage);
    }

    private void launchTowerOfHanoi() {
        TowerOfHanoi towerOfHanoiApp = new TowerOfHanoi();
        Stage towerOfHanoiStage = new Stage();
        towerOfHanoiApp.startTowerOfHanoi(towerOfHanoiStage);
    }

    private void launchBeanMachine() {
        BMF25 beanMachineApp = new BMF25();
        Stage beanMachineStage = new Stage();
        beanMachineApp.start(beanMachineStage);
    }

    private void launchUBC() {
        NumberConversionSystem ncsApp = new NumberConversionSystem();
        Stage ncsStage = new Stage();
        ncsApp.start(ncsStage);
    }

    private void launchPingPong() {
        PingPong pingPongApp = new PingPong();
        Stage pingPongStage = new Stage();
        pingPongApp.start(pingPongStage);
    }

    private void launchRacingCar() {
        RacingCar3 racingCarApp = new RacingCar3();
        Stage racingCarStage = new Stage();
        racingCarApp.start(racingCarStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}