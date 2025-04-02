module RacingCar {
    requires javafx.controls;
    requires javafx.fxml;

    exports application; // Allows JavaFX to access the Main class
    opens application to javafx.fxml; // Ensures reflection works for FXML loading
    opens application.controllers to javafx.fxml; // Enables controllers to be loaded via FXML
}
