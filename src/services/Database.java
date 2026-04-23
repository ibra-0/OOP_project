package services;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import base.User;
import enums.Role;
import services.Logger;

public class Database implements Serializable {
    private static final long serialVersionUID = 1L;
    private static Database instance;
    private static final String DATA_FILE = "database.dat";

    public List<User> users = new ArrayList<>();
    public List<String> courses = new ArrayList<>();

    private Database() {}

    public static Database getInstance() {
        if (instance == null) {
            instance = load();
            if (instance == null) {
                instance = new Database();
            }
        }
        return instance;
    }

    public void addUser(User user) {
        users.add(user);
        Logger.log("User added: " + user.getLogin() + " with role " + user.getRole());
    }

    public boolean removeUser(String userId) {
        User user = findUserById(userId);
        if (user != null) {
            users.remove(user);
            Logger.log("User removed: " + user.getLogin());
            return true;
        }
        return false;
    }

    public User findUserById(String id) {
        return users.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public User findUserByLogin(String login) {
        return users.stream()
                .filter(u -> u.getLogin().equals(login))
                .findFirst()
                .orElse(null);
    }

    public List<User> getUsersByRole(Role role) {
        List<User> result = new ArrayList<>();
        for (User user : users) {
            if (user.getRole() == role) {
                result.add(user);
            }
        }
        return result;
    }

    public void save() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(this);
            Logger.log("Database saved");
        } catch (IOException e) {
            Logger.log("Save error: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private static Database load() {
        File file = new File(DATA_FILE);
        if (!file.exists()) return null;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Logger.log("Database loaded from file");
            return (Database) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            Logger.log("Load error: " + e.getMessage());
            return null;
        }
    }
}