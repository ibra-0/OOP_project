package services;

import base.User;
import models.Admin;
import models.Teacher;
import models.Student;
import enums.Role;
import java.util.List;

public class AdminService {
    private Database db;

    public AdminService() {
        this.db = Database.getInstance();
    }

    public boolean addUser(String login, String password, String name, Role role) {
        if (db.findUserByLogin(login) != null) {
            Logger.log("Failed attempt to add duplicate user: " + login);
            return false;
        }

        User user;
        switch (role) {
            case ADMIN:
                user = new Admin(login, password, name);
                break;
            case TEACHER:
                user = new Teacher(login, password, name);
                break;
            case STUDENT:
                user = new Student(login, password, name);
                break;
            default:
                return false;
        }

        db.addUser(user);
        return true;
    }

    public boolean removeUser(String userId) {
        User user = db.findUserById(userId);
        if (user == null) {
            Logger.log("Attempt to remove non-existent user: " + userId);
            return false;
        }

        if (user.getRole() == Role.ADMIN) {
            long adminCount = db.getUsersByRole(Role.ADMIN).size();
            if (adminCount <= 1) {
                Logger.log("Cannot remove last admin");
                return false;
            }
        }

        return db.removeUser(userId);
    }

    public List<User> getAllUsers() {
        return db.users;
    }

    public List<User> getUsersByRole(Role role) {
        return db.getUsersByRole(role);
    }

    public void viewLogs() {
        List<String> logs = Logger.getLogs();
        System.out.println("\n=== System Logs ===");
        for (String log : logs) {
            System.out.println(log);
        }
        if (logs.isEmpty()) {
            System.out.println("No logs available");
        }
    }

    public void viewUserLogs(String userId) {
        List<String> logs = Logger.getLogsByUser(userId);
        System.out.println("\n=== Logs for user " + userId + " ===");
        for (String log : logs) {
            System.out.println(log);
        }
        if (logs.isEmpty()) {
            System.out.println("No logs for this user");
        }
    }
}