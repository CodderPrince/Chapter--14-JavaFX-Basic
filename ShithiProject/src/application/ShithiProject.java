package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class ShithiProject extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Universal Base Calculator");

        VBox mainLayout = new VBox(40);
        mainLayout.setPadding(new Insets(25));
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setStyle("-fx-background-color: linear-gradient(to top right, #83a4d4, #b6fbff);");

        Label titleLabel = new Label("Universal Base Calculator");
        titleLabel.setFont(Font.font("Cambria", 30));

        Button convertButton = new Button("Convert Base");
        styleButton(convertButton, "#01579B", 33);
        convertButton.setOnAction(e -> openBaseConversion(primaryStage));

        Button addButton = new Button("Add Numbers");
        styleButton(addButton, "#00695C", 33);
        addButton.setOnAction(e -> openOperationWindow(primaryStage, "Add"));

        Button subtractButton = new Button("Subtract Numbers");
        styleButton(subtractButton, "#BF360C", 33);
        subtractButton.setOnAction(e -> openOperationWindow(primaryStage, "Subtract"));

        mainLayout.getChildren().addAll(titleLabel, convertButton, addButton, subtractButton);

        Scene mainScene = new Scene(mainLayout, 420, 400);
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

    private void openOperationWindow(Stage primaryStage, String operation) {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: linear-gradient(to bottom, #b2dfdb, #e0f2f1);");

        Label baseLabel = new Label("Base of Numbers:");
        baseLabel.setFont(Font.font("Cambria", 18));

        TextField baseInput = new TextField();
        baseInput.setPromptText("Enter base for operation (e.g., 16)");

        Label number1Label = new Label("First Number:");
        number1Label.setFont(Font.font("Cambria", 18));

        TextField number1Input = new TextField();
        number1Input.setPromptText("Enter first number");

        Label number2Label = new Label("Second Number:");
        number2Label.setFont(Font.font("Cambria", 18));

        TextField number2Input = new TextField();
        number2Input.setPromptText("Enter second number");

        Button actionButton = new Button(operation);
        styleButton(actionButton, "#004D40", 28);

        Label resultLabel = new Label();
        actionButton.setOnAction(e -> {
            resultLabel.setText(performOperation(baseInput.getText(), number1Input.getText(), number2Input.getText(), operation));
        });

        Button backButton = new Button("Back");
        styleButton(backButton, "#B71C1C", 28);
        backButton.setOnAction(e -> start(primaryStage));

        layout.getChildren().addAll(baseLabel, baseInput, number1Label, number1Input, number2Label, number2Input, actionButton, resultLabel, backButton);

        Scene scene = new Scene(layout, 450, 550);
        primaryStage.setScene(scene);
    }

    private String performOperation(String base, String num1, String num2, String operation) {
        try {
            int baseValue = Integer.parseInt(base);
            double number1 = parseFractional(num1, baseValue);
            double number2 = parseFractional(num2, baseValue);
            double result = operation.equals("Add") ? number1 + number2 : number1 - number2;

            StringBuilder steps = new StringBuilder("Operation Steps:\n");
            steps.append("1. Convert ").append(num1).append(" and ").append(num2).append(" from base ").append(baseValue).append(" to decimal.\n");
            steps.append("   - ").append(num1).append(" in decimal: ").append(number1).append("\n");
            steps.append("   - ").append(num2).append(" in decimal: ").append(number2).append("\n");

            steps.append("2. Perform ").append(operation.toLowerCase()).append(" operation in decimal:\n");
            steps.append("   - ").append(number1).append(" ").append(operation.equals("Add") ? "+" : "-").append(" ").append(number2)
                .append(" = ").append(result).append("\n");

            String formattedResult = formatFractional(result, baseValue);
            steps.append("3. Convert the result ").append(result).append(" back to base ").append(baseValue).append(":\n");
            steps.append("   - Result in base ").append(baseValue).append(": ").append(formattedResult).append("\n");

            return steps.toString();
        } catch (NumberFormatException e) {
            return "Invalid input or base";
        }
    }

    private String convertNumber(String fromBase, String toBase, String number) {
        try {
            int sourceBase = Integer.parseInt(fromBase);
            int targetBase = Integer.parseInt(toBase);
            double decimal = parseFractional(number, sourceBase);

            StringBuilder steps = new StringBuilder("Conversion Steps:\n");
            steps.append("1. Convert ").append(number).append(" from base ").append(sourceBase).append(" to decimal.\n");

            steps.append("   - Integer part: ").append(Long.parseLong(number.split("\\.")[0], sourceBase)).append("\n");
            if (number.contains(".")) {
                steps.append("   - Fractional part calculated as:\n");
                String[] parts = number.split("\\.");
                for (int i = 0; i < parts[1].length(); i++) {
                    steps.append("     + ").append(Character.digit(parts[1].charAt(i), sourceBase))
                        .append(" * ").append(sourceBase).append("^(-").append(i + 1).append(")\n");
                }
            }
            steps.append("2. Result in decimal: ").append(decimal).append("\n");

            String convertedResult = formatFractional(decimal, targetBase);
            steps.append("3. Convert decimal ").append(decimal).append(" to base ").append(targetBase).append(":\n");
            steps.append("   - Result: ").append(convertedResult).append("\n");

            return steps.toString();
        } catch (NumberFormatException e) {
            return "Invalid input or base";
        }
    }

    private double parseFractional(String number, int base) {
        String[] parts = number.split("\\.");
        double integerPart = Long.parseLong(parts[0], base);
        double fractionalPart = 0;
        if (parts.length > 1) {
            for (int i = 0; i < parts[1].length(); i++) {
                fractionalPart += Character.digit(parts[1].charAt(i), base) / Math.pow(base, i + 1);
            }
        }
        return integerPart + fractionalPart;
    }

    private String formatFractional(double number, int base) {
        long integerPart = (long) number;
        double fractionalPart = number - integerPart;
        StringBuilder result = new StringBuilder(Long.toString(integerPart, base).toUpperCase() + ".");
        for (int i = 0; i < 10; i++) { // Limit the fractional part to 10 digits for precision
            fractionalPart *= base;
            int fractionalDigit = (int) fractionalPart;
            result.append(Integer.toString(fractionalDigit, base).toUpperCase());
            fractionalPart -= fractionalDigit;
            if (fractionalPart == 0) break;
        }
        return result.toString();
    }

    private void styleButton(Button button, String color, int fontSize) {
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white;");
        button.setFont(Font.font("Cambria", fontSize));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
