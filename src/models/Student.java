package models;

import base.User;
import enums.Role;

public class Student extends User {
    public Student(String login, String password, String name) {
        super(login, hashPassword(password), name, Role.STUDENT);
    }

    @Override
    public String getDetails() {
        return String.format("Student: %s (%s)", name, login);
    }
}