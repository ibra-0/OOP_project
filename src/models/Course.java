package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
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
        return students.size() < maxStudents;
    }

    public boolean addTeacher(Teacher teacher) {
        if (teacher == null || teachers.contains(teacher)) return false;
        teachers.add(teacher);
        teacher.assignCourse(this);
        return true;
    }

    public boolean removeTeacher(Teacher teacher) {
        return teachers.remove(teacher);
    }

    public boolean addStudent(Student student) {
        if (student == null || students.contains(student) || !hasSpace()) return false;
        students.add(student);
        student.enrollCourse(this);
        return true;
    }

    public boolean removeStudent(Student student) {
        return students.remove(student);
    }

    public List<Teacher> getTeachers() {
        return Collections.unmodifiableList(teachers);
    }

    public List<Student> getStudents() {
        return Collections.unmodifiableList(students);
    }

    public List<Lesson> getLessons() {
        return Collections.unmodifiableList(lessons);
    }

    public void addLesson(Lesson lesson) {
        if (lesson != null) {
            lessons.add(lesson);
        }
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
