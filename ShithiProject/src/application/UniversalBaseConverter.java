package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class UniversalBaseConverter extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Universal Base Converter");

        VBox mainLayout = new VBox(60);
        mainLayout.setPadding(new Insets(25));
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setStyle("-fx-background-color: linear-gradient(to top right, #83a4d4, #b6fbff);");

        Label titleLabel = new Label("Universal Base Converter");
        titleLabel.setFont(Font.font("Cambria", 30));

        Button convertBaseButton = new Button("Convert Base");
        styleButton(convertBaseButton, "#01579B", 33);
        convertBaseButton.setOnAction(e -> openBaseConversion(primaryStage));

        mainLayout.getChildren().addAll(titleLabel, convertBaseButton);

        Scene mainScene = new Scene(mainLayout, 420, 300);
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    private void openBaseConversion(Stage primaryStage) {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: linear-gradient(to top, #fbc2eb, #a6c1ee);");

        Label fromBaseLabel = new Label("From Base:");
        fromBaseLabel.setFont(Font.font("Cambria", 18));

        TextField fromBaseInput = new TextField();
        fromBaseInput.setPromptText("Enter source base (e.g., 35)");

        Label numberLabel = new Label("Number to Convert:");
        numberLabel.setFont(Font.font("Cambria", 18));

        TextField numberInput = new TextField();
        numberInput.setPromptText("Enter number");

        Label toBaseLabel = new Label("To Base:");
        toBaseLabel.setFont(Font.font("Cambria", 18));

        TextField toBaseInput = new TextField();
        toBaseInput.setPromptText("Enter target base (e.g., 23)");

        Button convertButton = new Button("Convert");
        styleButton(convertButton, "#00838F", 28);
        Label resultLabel = new Label();
        convertButton.setOnAction(e -> {
            resultLabel.setText(convertNumber(fromBaseInput.getText(), toBaseInput.getText(), numberInput.getText()));
        });

        Button backButton = new Button("Back");
        styleButton(backButton, "#B71C1C", 28);
        backButton.setOnAction(e -> start(primaryStage));

        layout.getChildren().addAll(fromBaseLabel, fromBaseInput, numberLabel, numberInput, toBaseLabel, toBaseInput, convertButton, resultLabel, backButton);

        Scene scene = new Scene(layout, 450, 500);
        primaryStage.setScene(scene);
    }

    private String convertNumber(String fromBase, String toBase, String number) {
        try {
            int sourceBase = Integer.parseInt(fromBase);
            int targetBase = Integer.parseInt(toBase);
            long decimal = Long.parseLong(number, sourceBase);
            return Long.toString(decimal, targetBase).toUpperCase();
        } catch (NumberFormatException e) {
            return "Invalid input or base";
        }
    }

    private void styleButton(Button button, String color, int fontSize) {
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white;");
        button.setFont(Font.font("Cambria", fontSize));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
