/*
 * Title:         Base Conversion Toolkit
 * Developed by:  [Friend's Name]
 * ID:            [Friend's ID]
 * Availability:  Converts and performs arithmetic on custom/predefined bases 
 *                (Binary, Decimal, Octal, Hexadecimal).
 * Key Features:
 * - Custom base number operations
 * - Base conversions (Binary, Decimal, Octal, Hexadecimal, Custom)
 * - Addition and subtraction in any base
 * - Fractional number support
 */

package toolkit;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class BaseConversionToolkit extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Base Conversion Toolkit");

        // Main layout
        VBox mainLayout = new VBox(40);
        mainLayout.setPadding(new Insets(15, 15, 15, 15));
        mainLayout.setAlignment(Pos.CENTER);
        
        // Primary dialog box background
        mainLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #c2ffd8, #d1ffe5);");
        
        Label titleLabel = new Label("Base Conversion Toolkit");
        titleLabel.setFont(Font.font("Verdana", 26)); 

        // Custom Base Button
        Button customBaseButton = new Button("Custom Base Options");
        setButtonStyle(customBaseButton, "#004D40", 30);
        customBaseButton.setOnAction(e -> openCustomBaseSelection(primaryStage));

        // Default Base Button
        Button defaultBaseButton = new Button("Standard Base Options");
        setButtonStyle(defaultBaseButton, "#1A237E", 31);
        defaultBaseButton.setOnAction(e -> openDefaultBaseSelection(primaryStage));

        mainLayout.getChildren().addAll(titleLabel, customBaseButton, defaultBaseButton);

        Scene mainScene = new Scene(mainLayout, 420, 400);
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    private void openCustomBaseSelection(Stage primaryStage) {
        VBox selectionLayout = new VBox(20);
        selectionLayout.setPadding(new Insets(25));
        selectionLayout.setAlignment(Pos.CENTER);
        selectionLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #d2fcf0, #afe2e8);");

        Button addButton = new Button("Addition");
        setButtonStyle(addButton, "#00695C", 30);
        addButton.setOnAction(e -> openCustomBaseWindow(primaryStage, "Add"));

        Button subtractButton = new Button("Subtraction");
        setButtonStyle(subtractButton, "#1B5E20", 30);
        subtractButton.setOnAction(e -> openCustomBaseWindow(primaryStage, "Subtract"));

        Button convertButton = new Button("Convert");
        setButtonStyle(convertButton, "#C62828", 30);
        convertButton.setOnAction(e -> openCustomBaseWindow(primaryStage, "Convert"));

        Button backButton = new Button("Back");
        setButtonStyle(backButton, "#BF360C", 30);
        backButton.setOnAction(e -> start(primaryStage));

        selectionLayout.getChildren().addAll(addButton, subtractButton, convertButton, backButton);

        Scene selectionScene = new Scene(selectionLayout, 420, 400);
        primaryStage.setScene(selectionScene);
    }

    private void openDefaultBaseSelection(Stage primaryStage) {
        VBox selectionLayout = new VBox(20);
        selectionLayout.setPadding(new Insets(25));
        selectionLayout.setAlignment(Pos.CENTER);
        selectionLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #e8eaf6, #e1f5fe);");

        Button addButton = new Button("Addition");
        setButtonStyle(addButton, "#00796B", 30);
        addButton.setOnAction(e -> openDefaultBaseWindow(primaryStage, "Add"));

        Button subtractButton = new Button("Subtraction");
        setButtonStyle(subtractButton, "#3E2723", 30);
        subtractButton.setOnAction(e -> openDefaultBaseWindow(primaryStage, "Subtract"));

        Button convertButton = new Button("Convert");
        setButtonStyle(convertButton, "#4527A0", 30);
        convertButton.setOnAction(e -> openDefaultBaseWindow(primaryStage, "Convert"));

        Button backButton = new Button("Back");
        setButtonStyle(backButton, "#FF5722", 30);
        backButton.setOnAction(e -> start(primaryStage));

        selectionLayout.getChildren().addAll(addButton, subtractButton, convertButton, backButton);

        Scene selectionScene = new Scene(selectionLayout, 420, 400);
        primaryStage.setScene(selectionScene);
    }

    private void openCustomBaseWindow(Stage primaryStage, String operation) {
        VBox customLayout = new VBox(20);
        customLayout.setPadding(new Insets(25));
        customLayout.setAlignment(Pos.CENTER);
        customLayout.setStyle("-fx-background-color: #fafafa;");

        Label baseLabel = new Label("Enter Base:");
        baseLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #004D40; -fx-font-family: 'Georgia';");

        TextField baseInput = new TextField();
        baseInput.setPromptText("Enter base (e.g., 2 for Binary, 8 for Octal)");
        baseInput.setStyle("-fx-font-size: 16px; -fx-text-fill: #333333;");

        Label numberLabel1 = new Label("Enter First Number:");
        numberLabel1.setStyle("-fx-font-size: 16px; -fx-text-fill: #004D40; -fx-font-family: 'Georgia';");

        TextField numberInput1 = new TextField();
        numberInput1.setPromptText("Enter first base number");
        numberInput1.setStyle("-fx-font-size: 16px; -fx-text-fill: #333333;");

        Label numberLabel2 = new Label("Enter Second Number:");
        numberLabel2.setStyle("-fx-font-size: 16px; -fx-text-fill: #004D40; -fx-font-family: 'Georgia';");

        TextField numberInput2 = new TextField();
        numberInput2.setPromptText("Enter second base number");
        numberInput2.setStyle("-fx-font-size: 16px; -fx-text-fill: #333333;");

        ComboBox<String> targetBaseBox = new ComboBox<>();
        targetBaseBox.getItems().addAll("Decimal", "Binary", "Octal", "Hexadecimal", "Custom");
        targetBaseBox.setPromptText("Choose target base");
        targetBaseBox.setStyle("-fx-font-size: 16px; -fx-text-fill: #004D40;");

        Label resultLabel = new Label();
        Button actionButton = new Button(operation);
        setButtonStyle(actionButton, "#388E3C", 25);

        Button backButton = new Button("Back");
        setButtonStyle(backButton, "#BF360C", 25);

        if (operation.equals("Convert")) {
            customLayout.getChildren().addAll(baseLabel, baseInput, numberLabel1, numberInput1, targetBaseBox, actionButton, resultLabel);
            actionButton.setOnAction(e -> convertNumber(baseInput, numberInput1, targetBaseBox, resultLabel));
        } else {
            customLayout.getChildren().addAll(baseLabel, baseInput, numberLabel1, numberInput1, numberLabel2, numberInput2, targetBaseBox, actionButton, resultLabel);
            actionButton.setOnAction(e -> performOperation(baseInput, numberInput1, numberInput2, targetBaseBox, resultLabel, operation));
        }

        backButton.setOnAction(e -> openCustomBaseSelection(primaryStage));
        customLayout.getChildren().add(backButton);

        Scene customScene = new Scene(customLayout, 420, 460);
        primaryStage.setScene(customScene);
    }

    private void setButtonStyle(Button button, String color, int fontSize) {
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: #FFFFFF;");
        button.setFont(Font.font("Tahoma", fontSize));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
