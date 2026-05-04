package models;

import java.io.Serializable;

public class Mark implements Serializable {
    private static final long serialVersionUID = 1L;

    private int att1;
    private int att2;
    private int finalExam;
    private Student student;
    private Course course;

    public Mark(int att1, int att2, int finalExam, Student student, Course course) {
        this.att1 = att1;
        this.att2 = att2;
        this.finalExam = finalExam;
        this.student = student;
        this.course = course;
    }

    public int getAtt1() {
        return att1;
    }

    public int getAtt2() {
        return att2;
    }

    public int getFinalExam() {
        return finalExam;
    }

    public Student getStudent() {
        return student;
    }

    public Course getCourse() {
        return course;
    }

    public int getTotal() {
        return att1 + att2 + finalExam;
    }

    public String getLetterGrade() {
        int total = getTotal();
        if (total >= 90) return "A";
        if (total >= 80) return "B";
        if (total >= 70) return "C";
        if (total >= 60) return "D";
        return "F";
    }

    public boolean isPassed() {
        return getTotal() >= 60;
    }

    @Override
    public String toString() {
        return "Mark{" +
                "student=" + (student == null ? "-" : student.getLogin()) +
                ", course=" + (course == null ? "-" : course.getCourseID()) +
                ", att1=" + att1 +
                ", att2=" + att2 +
                ", finalExam=" + finalExam +
                ", total=" + getTotal() +
                ", letter='" + getLetterGrade() + '\'' +
                ", passed=" + isPassed() +
                '}';
    }
}
