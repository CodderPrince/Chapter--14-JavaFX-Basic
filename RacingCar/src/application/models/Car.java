package application.models;

public class Car {
    private String name;
    private double speed;
    private double acceleration;

    public Car(String name, double speed, double acceleration) {
        this.name = name;
        this.speed = speed;
        this.acceleration = acceleration;
    }

    // Getters
    public String getName() {
        return name;
    }

    public double getSpeed() {
        return speed;
    }

    public double getAcceleration() {
        return acceleration;
    }
}
