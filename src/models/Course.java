package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Course implements Serializable {
    private static final long serialVersionUID = 1L;

    private String courseID;
    private int credits;
    private List<Teacher> teachers = new ArrayList<>();
    private List<Student> students = new ArrayList<>();
    private List<Lesson> lessons = new ArrayList<>();
    private String major;
    private int year;
    private int maxStudents;

    public Course(String courseID, int credits, String major, int year, int maxStudents) {
        this.courseID = courseID;
        this.credits = credits;
        this.major = major;
        this.year = year;
        this.maxStudents = maxStudents;
    }

    public String getCourseID() {
        return courseID;
    }

    public int getCredits() {
        return credits;
    }

    public String getMajor() {
        return major;
    }

    public int getYear() {
        return year;
    }

    public int getMaxStudents() {
        return maxStudents;
    }

    public boolean hasSpace() {
        if (students == null) students = new ArrayList<>();
        return students.size() < maxStudents;
    }

    public List<Teacher> getTeachers() {
        if (teachers == null) teachers = new ArrayList<>();
        return teachers;
    }

    public List<Student> getStudents() {
        if (students == null) students = new ArrayList<>();
        return students;
    }

    public List<Lesson> getLessons() {
        if (lessons == null) lessons = new ArrayList<>();
        return lessons;
    }

    @Override
    public String toString() {
        return "Course{" +
                "courseID='" + courseID + '\'' +
                ", credits=" + credits +
                ", major='" + major + '\'' +
                ", year=" + year +
                ", maxStudents=" + maxStudents +
                ", teachers=" + teachers.size() +
                ", students=" + students.size() +
                ", lessons=" + lessons.size() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Course)) return false;
        Course course = (Course) o;
        return Objects.equals(courseID, course.courseID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseID);
    }
}
