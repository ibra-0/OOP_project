package services;

import base.User;
import enums.Role;

public class AuthService {
    private static AuthService instance;
    private User currentUser;

    private AuthService() {}

    public static AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }

    public boolean login(String login, String password) {
        Database db = Database.getInstance();
        User user = db.findUserByLogin(login);

        if (user != null && user.checkPassword(password)) {
            currentUser = user;
            Logger.log("User logged in: " + login);
            return true;
        }

        Logger.log("Failed login attempt: " + login);
        return false;
    }

    public void logout() {
        if (currentUser != null) {
            Logger.log("User logged out: " + currentUser.getLogin());
            currentUser = null;
        }
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean hasRole(Role role) {
        return currentUser != null && currentUser.getRole() == role;
    }

    public boolean isAdmin() {
        return hasRole(Role.ADMIN);
    }
}