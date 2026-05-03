package models;

import base.User;
import enums.Role;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Student extends User {
    private static final long serialVersionUID = 2L;

    private List<String> enrolledCourses = new ArrayList<>();

    public Student(String login, String password, String name) {
        super(login, hashPassword(password), name, Role.STUDENT);
    }

    public List<String> getEnrolledCourses() {
        if (enrolledCourses == null) enrolledCourses = new ArrayList<>();
        return Collections.unmodifiableList(enrolledCourses);
    }

    public boolean enroll(String course) {
        if (enrolledCourses == null) enrolledCourses = new ArrayList<>();
        if (course == null || course.isBlank() || enrolledCourses.contains(course)) return false;
        enrolledCourses.add(course);
        return true;
    }

    @Override
    public String getDetails() {
        return String.format("Student: %s (%s)", name, login);
    }
}