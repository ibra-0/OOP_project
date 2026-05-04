package models;

import enums.LessonType;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Lesson implements Serializable {
    private static final long serialVersionUID = 1L;

    private String lessonID;
    private LessonType type;
    private LocalDateTime dateTime;
    private Course course;
    private Teacher teacher;

    public Lesson(String lessonID, LessonType type, LocalDateTime dateTime, Course course, Teacher teacher) {
        this.lessonID = lessonID;
        this.type = type;
        this.dateTime = dateTime;
        this.course = course;
        this.teacher = teacher;
    }

    public LessonType getType() {
        return type;
    }

    public String getLessonID() {
        return lessonID;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public Course getCourse() {
        return course;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    @Override
    public String toString() {
        return "Lesson{" +
                "lessonID='" + lessonID + '\'' +
                ", type=" + type +
                ", dateTime=" + dateTime +
                ", course=" + (course == null ? "-" : course.getCourseID()) +
                ", teacher=" + (teacher == null ? "-" : teacher.getLogin()) +
                '}';
    }
}
