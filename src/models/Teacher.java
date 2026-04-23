package models;

import base.User;
import enums.Role;

public class Teacher extends User {
    public Teacher(String login, String password, String name) {
        super(login, hashPassword(password), name, Role.TEACHER);
    }

    @Override
    public String getDetails() {
        return String.format("Teacher: %s (%s)", name, login);
    }
}