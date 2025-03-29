package application;

import java.util.Optional;
import java.util.Stack;

import javafx.animation.PathTransition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class TowerOfHanoi {
    private int numDisks; // Number of disks, set dynamically by user
    private static final int ROD_SPACING = 300;
    private static final int BASE_ROD_X = 200; // Increased left space for the source rod
    private static final int ROD_HEIGHT = 400;

    private Stack<Ellipse>[] rods = new Stack[3];
    private Pane root = new Pane();
    private Text stepLabel;
    private int stepCount = 1;

    public void startTowerOfHanoi(Stage primaryStage) {
        // Ask user for the number of disks
        TextInputDialog inputDialog = new TextInputDialog("4");
        inputDialog.setTitle("Tower of Hanoi | Developed by PRINCE");

        // Set dialog background to a light linear gradient
        DialogPane dialogPane = inputDialog.getDialogPane();
        dialogPane.setStyle("-fx-background-color: linear-gradient(to bottom, #e0f7fa, #b2ebf2);");

        // Customize header text and content text font and size
        inputDialog.setHeaderText("Enter the number of disks:");
        inputDialog.setContentText("Number of disks:");
        dialogPane.lookup(".header-panel")
                .setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #00796b;");
        dialogPane.lookup(".content").setStyle("-fx-font-size: 16px; -fx-text-fill: #004d40;");

        // Style the TextField inside the dialog
        TextField textField = (TextField) dialogPane.lookup(".text-field");
        textField.setStyle(
                "-fx-background-color: #351804; -fx-text-fill: WHITE; -fx-font-size: 20px; -fx-border-radius: 5px; -fx-background-radius: 5px;");

        // Customize buttons (e.g., OK and Cancel)
        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        okButton.setStyle(
                "-fx-background-color: darkgreen; -fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold; -fx-border-radius: 5px; -fx-background-radius: 5px;");

        Button cancelButton = (Button) dialogPane.lookupButton(ButtonType.CANCEL);
        cancelButton.setStyle(
                "-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold; -fx-border-radius: 5px; -fx-background-radius: 5px;");

        // Get user input
        Optional<String> result = inputDialog.showAndWait();
        numDisks = result.map(Integer::parseInt).orElse(4);

        // Check if the user clicked OK and provided valid input
        if (!result.isPresent() || result.get().trim().isEmpty()) {
            primaryStage.close();
            return;
        }

        // Validate input for numeric values
        try {
            numDisks = Integer.parseInt(result.get().trim());
            if (numDisks <= 0)
                throw new NumberFormatException("Disks must be greater than zero.");
        } catch (NumberFormatException e) {
            // Show an error alert if the input is invalid
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Input");
            alert.setHeaderText("Error");
            alert.setContentText("Please enter a valid positive integer for the number of disks.");

            DialogPane dialogPane1 = alert.getDialogPane();
            dialogPane1.setStyle("-fx-background-color: linear-gradient(to bottom, #fbe9e7, #ffccbc);");
            dialogPane1.lookup(".header-panel")
                    .setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: black;");
            dialogPane1.lookup(".content").setStyle("-fx-font-size: 16px; -fx-text-fill: black;");

            alert.showAndWait();
            startTowerOfHanoi(primaryStage);
            return;
        }

        root.setStyle("-fx-background-color: linear-gradient(to bottom, lightgray, #cfdef3);");

        // Initialize rods
        for (int i = 0; i < 3; i++) {
            rods[i] = new Stack<>();
            double x = BASE_ROD_X + i * ROD_SPACING;
            root.getChildren().add(createRod(x));
        }

        root.getChildren().add(createRodLabel(BASE_ROD_X - 50, "Source"));
        root.getChildren().add(createRodLabel(BASE_ROD_X + ROD_SPACING - 50, "Helper"));
        root.getChildren().add(createRodLabel(BASE_ROD_X + 2 * ROD_SPACING - 50, "Destination"));

        for (int i = numDisks; i >= 1; i--) {
            Ellipse disk = createDisk(i);
            rods[0].push(disk);
            disk.setTranslateX(BASE_ROD_X);
            disk.setTranslateY(650 - (numDisks - i) * 30);
            root.getChildren().add(disk);
        }

        stepLabel = new Text(90, 50, "Move: ");
        stepLabel.setFill(Color.BLUE);
        stepLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");
        root.getChildren().add(stepLabel);

        Button runAgainButton = new Button("Run Again");
        runAgainButton.setLayoutX(450);
        runAgainButton.setLayoutY(750);
        runAgainButton.setStyle("-fx-background-color: darkgreen; -fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold; -fx-border-radius: 10px; -fx-background-radius: 10px;");
        runAgainButton.setOnAction(e -> restartAnimation(primaryStage));
        root.getChildren().add(runAgainButton);

        Text minMovesLabel = new Text("Minimum Moves: " + (Math.pow(2, numDisks) - 1));
        minMovesLabel.setFill(Color.web("#351804"));
        minMovesLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: RED;");
        HBox minMovesContainer = new HBox(minMovesLabel);
        minMovesContainer.setAlignment(Pos.TOP_RIGHT);
        minMovesContainer.setTranslateY(28);
        minMovesContainer.setTranslateX(710);
        minMovesContainer.setStyle("-fx-padding: 5 15 5 15; -fx-background-color: linear-gradient(to right, #f0f8ff, #d1e8ff); -fx-border-radius: 8px; -fx-background-radius: 8px;");
        root.getChildren().add(minMovesContainer);

        new Thread(() -> towerOfHanoi(numDisks, 0, 1, 2)).start();

        Scene scene = new Scene(root, 1050, 810);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Tower of Hanoi Visualizer - Updated");
        primaryStage.show();
    }

    private Rectangle createRod(double x) {
        double rodBottom = 650;
        double rodTop = rodBottom - ROD_HEIGHT;
        double rodWidth = 15;
        Rectangle rod = new Rectangle(x - rodWidth / 2, rodTop, rodWidth, ROD_HEIGHT);
        rod.setFill(Color.BROWN);
        rod.setArcWidth(10);
        rod.setArcHeight(10);
        return rod;
    }

    private Text createRodLabel(double x, String label) {
        Text text = new Text(label);
        text.setX(x + 18);
        text.setY(ROD_HEIGHT + 320);
        text.setFill(Color.BLUE);
        text.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");
        return text;
    }

    private Ellipse createDisk(int size) {
        double radiusX = 40 + size * 10;
        double radiusY = 15 + size * 2;
        Ellipse disk = new Ellipse(radiusX, radiusY);
        Color[] colors = { Color.GOLDENROD, Color.LIMEGREEN, Color.SKYBLUE, Color.PURPLE, Color.WHITE, Color.PINK, Color.YELLOW, Color.GRAY, Color.RED, Color.BLACK };
        disk.setFill(colors[(size - 1) % colors.length]);
        disk.setStroke(Color.BLACK);
        disk.setStrokeWidth(2);
        return disk;
    }

    private void moveDisk(int from, int to) {
        Ellipse disk = rods[from].pop();
        rods[to].push(disk);

        double startX = disk.getTranslateX();
        double startY = disk.getTranslateY();
        double endX = BASE_ROD_X + to * ROD_SPACING;
        double endY = 650 - rods[to].size() * 30;

        Line liftPath = new Line(startX, startY, startX, 200);
        Line horizontalPath = new Line(startX, 200, endX, 200);
        Line lowerPath = new Line(endX, 200, endX, endY);

        PathTransition liftTransition = new PathTransition(Duration.seconds(1.0), liftPath, disk);
        PathTransition horizontalTransition = new PathTransition(Duration.seconds(1.0), horizontalPath, disk);
        PathTransition lowerTransition = new PathTransition(Duration.seconds(1.0), lowerPath, disk);

        liftTransition.setOnFinished(e -> horizontalTransition.play());
        horizontalTransition.setOnFinished(e -> lowerTransition.play());
        lowerTransition.setOnFinished(e -> {
            disk.setTranslateX(endX);
            disk.setTranslateY(endY);
            updateStepLabel(from, to);
        });

        liftTransition.play();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void updateStepLabel(int from, int to) {
        String[] rodNames = { "Source", "Helper", "Destination" };
        stepLabel.setText("Move " + stepCount++ + ": " + rodNames[from] + " to " + rodNames[to]);
    }

    private void towerOfHanoi(int n, int src, int helper, int dest) {
        if (n == 1) {
            moveDisk(src, dest);
            return;
        }
        towerOfHanoi(n - 1, src, dest, helper);
        moveDisk(src, dest);
        towerOfHanoi(n - 1, helper, src, dest);
    }

    private void restartAnimation(Stage primaryStage) {
        root.getChildren().clear();
        for (int i = 0; i < 3; i++) {
            rods[i].clear();
        }
        stepCount = 1;
        startTowerOfHanoi(primaryStage);
    }
}
