package models;

import java.io.Serializable;
import java.util.List;

public class Transcript implements Serializable {
    private static final long serialVersionUID = 1L;

    private Student student;

    public Transcript(Student student) {
        this.student = student;
    }

    public double getGpa() {
        List<Mark> marks = student.getMarks();
        if (marks.isEmpty()) return 0.0;

        double sum = 0.0;
        int totalCredits = 0;
        for (Mark m : marks) {
            int credits = m.getCourse().getCredits();
            totalCredits += credits;
            sum += gradePoint(m.getLetterGrade()) * credits;
        }
        if (totalCredits == 0) return 0.0;
        return sum / totalCredits;
    }

    public int getTotalCredits() {
        int total = 0;
        for (Mark m : student.getMarks()) {
            if (m.isPassed()) total += m.getCourse().getCredits();
        }
        return total;
    }

    public int getFailCount() {
        int fail = 0;
        for (Mark m : student.getMarks()) {
            if (!m.isPassed()) fail++;
        }
        return fail;
    }

    @Override
    public String toString() {
        return String.format(
                "Transcript{student=%s, gpa=%.2f, totalCredits=%d, failCount=%d}",
                student.getLogin(), getGpa(), getTotalCredits(), getFailCount()
        );
    }

    private double gradePoint(String letter) {
        switch (letter) {
            case "A": return 4.0;
            case "B": return 3.0;
            case "C": return 2.0;
            case "D": return 1.0;
            default: return 0.0;
        }
    }
}
