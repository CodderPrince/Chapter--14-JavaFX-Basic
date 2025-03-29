package application;

import java.io.*;
import java.util.*;

public class UserManager {
    private static final String DATA_FILE = "users.txt"; // Updated to save user data in a readable .txt file
    private Map<String, User> users = new HashMap<>();

    public UserManager() {
        ensureDataFileExists();
        loadUsers();
    }

    /**
     * Ensures that the users.txt file exists. If not, creates an empty file.
     */
    private void ensureDataFileExists() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    System.out.println("Data file created successfully.");
                }
            } catch (IOException e) {
                System.out.println("Failed to create data file: " + e.getMessage());
            }
        }
    }

    /**
     * Deletes all user data from the system and clears the data file.
     */
    public void deleteAllUsers() {
        users.clear();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE))) {
            writer.write(""); // Overwrite the file with an empty string
            System.out.println("All user data deleted successfully.");
        } catch (IOException e) {
            System.out.println("Failed to clear user data: " + e.getMessage());
        }
    }

    /**
     * Retrieves a user by their name and age.
     *
     * @param name The user's name.
     * @param age  The user's age.
     * @return The matching User object or null if not found.
     */
    public User getUser(String name, int age) {
        return users.get(generateUserId(name, age));
    }

    /**
     * Adds a new user to the system if they do not already exist.
     *
     * @param user The User object to add.
     */
    public void addUser(User user) {
        String userId = generateUserId(user.getName(), user.getAge());
        if (!users.containsKey(userId)) {
            users.put(userId, user);
            saveUsers();
            System.out.println("User added successfully.");
        } else {
            System.out.println("User already exists.");
        }
    }

    /**
     * Loads users from the data file.
     */
    private synchronized void loadUsers() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            System.out.println("No existing data file found. Starting fresh.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String name = parts[0].trim();
                    int age = Integer.parseInt(parts[1].trim());
                    User user = new User(name, age);
                    users.put(generateUserId(name, age), user);
                }
            }
            System.out.println("Users loaded successfully.");
        } catch (IOException | NumberFormatException e) {
            System.out.println("Failed to load user data: " + e.getMessage());
        }
    }

    /**
     * Saves the current user data to the data file.
     */
    private synchronized void saveUsers() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE))) {
            for (User user : users.values()) {
                writer.write(user.getName() + "," + user.getAge());
                writer.newLine();
            }
            System.out.println("User data saved successfully.");
        } catch (IOException e) {
            System.out.println("Failed to save user data: " + e.getMessage());
        }
    }

    /**
     * Generates a unique identifier for a user based on their name and age.
     *
     * @param name The user's name.
     * @param age  The user's age.
     * @return A unique identifier for the user.
     */
    private String generateUserId(String name, int age) {
        return name.trim().toLowerCase() + ":" + age;
    }

    /**
     * Retrieves all users in the system.
     *
     * @return A collection of all User objects.
     */
    public Collection<User> getAllUsers() {
        return users.values();
    }

    /**
     * Deletes a user by their name and age.
     *
     * @param name The user's name.
     * @param age  The user's age.
     * @return True if the user was deleted, false otherwise.
     */
    public boolean deleteUser(String name, int age) {
        String userId = generateUserId(name, age);
        if (users.containsKey(userId)) {
            users.remove(userId);
            saveUsers();
            System.out.println("User deleted successfully.");
            return true;
        } else {
            System.out.println("User not found.");
            return false;
        }
    }
}
