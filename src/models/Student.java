package models;

import base.User;
import enums.Role;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
        return Collections.unmodifiableList(enrolledCourses);
    }

    public boolean enroll(String course) {
        if (enrolledCourses == null) enrolledCourses = new ArrayList<>();
        if (course == null || course.isBlank() || enrolledCourses.contains(course)) return false;
        enrolledCourses.add(course);
        return true;
    }

    public boolean enrollCourse(Course course) {
        if (enrolledCourseObjects == null) enrolledCourseObjects = new ArrayList<>();
        if (course == null || enrolledCourseObjects.contains(course)) return false;
        enrolledCourseObjects.add(course);
        return enroll(course.getCourseID());
    }

    public List<Course> getEnrolledCourseObjects() {
        if (enrolledCourseObjects == null) enrolledCourseObjects = new ArrayList<>();
        return Collections.unmodifiableList(enrolledCourseObjects);
    }

    public void addMark(Mark mark) {
        if (mark == null) return;
        if (marks == null) marks = new ArrayList<>();
        marks.removeIf(m -> m.getCourse().equals(mark.getCourse()));
        marks.add(mark);
    }

    public List<Mark> getMarks() {
        if (marks == null) marks = new ArrayList<>();
        return Collections.unmodifiableList(marks);
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