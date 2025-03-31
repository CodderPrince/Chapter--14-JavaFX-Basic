package application;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class User implements Serializable {
    private static final long serialVersionUID = 1L; // Ensures class compatibility during serialization
    private String name;
    private int age;
    private Map<String, Integer> scores; // Stores scores for different games

    public User(String name, int age) {
        this.name = name;
        this.age = age;
        this.scores = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public Map<String, Integer> getScores() {
        return scores;
    }

    // Sets the score for a specific game, only updates if it's a higher score than already recorded
    public void setScore(String game, int score) {
        scores.put(game, Math.max(scores.getOrDefault(game, 0), score));
    }

    // Retrieves the score for a specific game, defaulting to 0 if no score is recorded
    public int getScore(String game) {
        return scores.getOrDefault(game, 0);
    }
}
