package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class AdvancedNumberConverter extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Advanced Number Converter");

        VBox mainLayout = new VBox(40);
        mainLayout.setPadding(new Insets(25));
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setStyle("-fx-background-color: linear-gradient(to top, #ff9a9e, #fecfef);");

        Label titleLabel = new Label("Advanced Number Converter");
        titleLabel.setFont(Font.font("Verdana", 30));

        Button customBaseButton = new Button("Custom Base Operations");
        styleButton(customBaseButton, "#00695C", 32);

        customBaseButton.setOnAction(e -> openCustomBaseOptions(primaryStage));

        Button predefinedBaseButton = new Button("Predefined Base Operations");
        styleButton(predefinedBaseButton, "#4527A0", 32);

        predefinedBaseButton.setOnAction(e -> openPredefinedBaseOptions(primaryStage));

        mainLayout.getChildren().addAll(titleLabel, customBaseButton, predefinedBaseButton);

        Scene mainScene = new Scene(mainLayout, 420, 400);
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    private void openCustomBaseOptions(Stage primaryStage) {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: linear-gradient(to top, #a18cd1, #fbc2eb);");

        Button addBtn = new Button("Addition");
        styleButton(addBtn, "#1B5E20", 34);
        addBtn.setOnAction(e -> openOperationWindow(primaryStage, "Add"));

        Button subtractBtn = new Button("Subtraction");
        styleButton(subtractBtn, "#0D47A1", 34);
        subtractBtn.setOnAction(e -> openOperationWindow(primaryStage, "Subtract"));

        Button convertBtn = new Button("Convert Numbers");
        styleButton(convertBtn, "#FF6F00", 34);
        convertBtn.setOnAction(e -> openOperationWindow(primaryStage, "Convert"));

        Button backBtn = new Button("Return");
        styleButton(backBtn, "#C62828", 34);
        backBtn.setOnAction(e -> start(primaryStage));

        layout.getChildren().addAll(addBtn, subtractBtn, convertBtn, backBtn);

        Scene scene = new Scene(layout, 420, 400);
        primaryStage.setScene(scene);
    }

    private void openPredefinedBaseOptions(Stage primaryStage) {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: linear-gradient(to top, #d4fc79, #96e6a1);");

        Button addBtn = new Button("Add Numbers");
        styleButton(addBtn, "#2E7D32", 34);
        addBtn.setOnAction(e -> openOperationWindow(primaryStage, "Add"));

        Button subtractBtn = new Button("Subtract Numbers");
        styleButton(subtractBtn, "#0D47A1", 34);
        subtractBtn.setOnAction(e -> openOperationWindow(primaryStage, "Subtract"));

        Button convertBtn = new Button("Base Conversion");
        styleButton(convertBtn, "#FF6F00", 34);
        convertBtn.setOnAction(e -> openOperationWindow(primaryStage, "Convert"));

        Button backBtn = new Button("Go Back");
        styleButton(backBtn, "#C62828", 34);
        backBtn.setOnAction(e -> start(primaryStage));

        layout.getChildren().addAll(addBtn, subtractBtn, convertBtn, backBtn);

        Scene scene = new Scene(layout, 420, 400);
        primaryStage.setScene(scene);
    }

    private void openOperationWindow(Stage primaryStage, String operation) {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #fafafa;");

        Label baseLabel = new Label("Base of Numbers:");
        baseLabel.setFont(Font.font("Tahoma", 18));

        TextField baseField = new TextField();
        baseField.setPromptText("Enter base here");

        Label numberLabel1 = new Label("First Number:");
        numberLabel1.setFont(Font.font("Tahoma", 18));

        TextField numberField1 = new TextField();
        numberField1.setPromptText("First number");

        Label numberLabel2 = new Label("Second Number:");
        numberLabel2.setFont(Font.font("Tahoma", 18));

        TextField numberField2 = new TextField();
        numberField2.setPromptText("Second number");

        ComboBox<String> baseBox = new ComboBox<>();
        baseBox.getItems().addAll("Decimal", "Binary", "Octal", "Hexadecimal", "Custom");
        baseBox.setPromptText("Select target base");

        Label resultLabel = new Label();
        Button actionBtn = new Button(operation);
        styleButton(actionBtn, "#4CAF50", 24);

        Button backBtn = new Button("Back");
        styleButton(backBtn, "#f44336", 24);
        backBtn.setOnAction(e -> openCustomBaseOptions(primaryStage));

        if (operation.equals("Convert")) {
            layout.getChildren().addAll(baseLabel, baseField, numberLabel1, numberField1, baseBox, actionBtn, resultLabel);
        } else {
            layout.getChildren().addAll(baseLabel, baseField, numberLabel1, numberField1, numberLabel2, numberField2, baseBox, actionBtn, resultLabel);
        }

        layout.getChildren().add(backBtn);

        Scene scene = new Scene(layout, 420, 500);
        primaryStage.setScene(scene);
    }

    private void styleButton(Button button, String color, int fontSize) {
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white;");
        button.setFont(Font.font("Tahoma", fontSize));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
