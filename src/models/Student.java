package models;

import base.User;
<<<<<<< HEAD
import exceptions.enums.Role;
=======
import enums.Role;
import java.util.ArrayList;
import java.util.List;
>>>>>>> eb511213a56517825a4f95e8fbc8e0c1452b9f68

/**
 * Student user with course enrollment, marks, and transcript support.
 */
public class Student extends User {
    private static final long serialVersionUID = 2L;

    private List<String> enrolledCourses = new ArrayList<>();
    private List<Course> enrolledCourseObjects = new ArrayList<>();
    private List<Mark> marks = new ArrayList<>();

    public Student(String login, String password, String name) {
        super(login, hashPassword(password), name, Role.STUDENT);
    }

    public List<String> getEnrolledCourses() {
        if (enrolledCourses == null) enrolledCourses = new ArrayList<>();
        return enrolledCourses;
    }

    public List<Course> getEnrolledCourseObjects() {
        if (enrolledCourseObjects == null) enrolledCourseObjects = new ArrayList<>();
        return enrolledCourseObjects;
    }

    public void addMark(Mark mark) {
        if (mark == null) return;
        if (marks == null) marks = new ArrayList<>();
        marks.removeIf(m -> m.getCourse().equals(mark.getCourse()));
        marks.add(mark);
    }

    public List<Mark> getMarks() {
        if (marks == null) marks = new ArrayList<>();
        return marks;
    }

    public Mark getMarkForCourse(Course course) {
        if (marks == null) marks = new ArrayList<>();
        for (Mark m : marks) {
            if (m.getCourse().equals(course)) return m;
        }
        return null;
    }

    public Transcript getTranscript() {
        return new Transcript(this);
    }

    @Override
    public String getDetails() {
        int courseCount = enrolledCourseObjects == null ? 0 : enrolledCourseObjects.size();
        return String.format("Student: %s (%s), %d enrolled courses", name, login, courseCount);
    }
}