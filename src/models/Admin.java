package models;

import base.User;
import exceptions.enums.Role;

/** Administrator with full system access. */
public class Admin extends User {
    public Admin(String login, String password, String name) {
        super(login, hashPassword(password), name, Role.ADMIN);
    }

    @Override
    public String getDetails() {
        return String.format("Admin: %s (%s)", name, login);
    }
}