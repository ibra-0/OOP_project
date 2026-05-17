package services;

import comparators.StudentComparators;
import models.Course;
import models.Mark;
import models.Student;
import models.Teacher;
import models.Transcript;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Grading, transcripts, academic reports, and teacher ratings. */
public class MarkService {
    private static MarkService instance;
    private final Database db = Database.getInstance();

    private MarkService() {}

    public static MarkService getInstance() {
        if (instance == null) instance = new MarkService();
        return instance;
    }

    public void putMark(Student s, Course c, float att1, float att2, float fin) {
        if (s == null || c == null) return;
        Mark mark = new Mark(Math.round(att1), Math.round(att2), Math.round(fin), s, c);
        s.addMark(mark);
        db.save();
    }

    public Transcript getTranscript(Student s) {
        if (s == null) return null;
        return s.getTranscript();
    }

    public void generateAcademicReport(Student s) {
        if (s == null) return;
        System.out.println("=== Academic report for " + s.getName() + " (" + s.getLogin() + ") ===");
        for (Course c : s.getEnrolledCourseObjects()) {
            Mark m = s.getMarkForCourse(c);
            System.out.println("- " + c.getCourseID() + ": " + (m == null ? "N/A" : (m.getLetterGrade() + " (" + m.getTotal() + ")")));
        }
        System.out.println(s.getTranscript());
    }

    public void generateAcademicReportForCourse(Course c) {
        if (c == null) return;
        System.out.println("=== Course report for " + c.getCourseID() + " ===");
        for (Student s : c.getStudents()) {
            Mark m = s.getMarkForCourse(c);
            System.out.println("- " + s.getLogin() + " " + s.getName() + ": " + (m == null ? "N/A" : (m.getLetterGrade() + " (" + m.getTotal() + ")")));
        }
    }

    public float getAverageGpa(Course c) {
        if (c == null) return 0f;
        if (c.getStudents().isEmpty()) return 0f;
        double sum = 0.0;
        for (Student s : c.getStudents()) sum += s.getTranscript().getGpa();
        return (float) (sum / c.getStudents().size());
    }

    public List<Student> getTopStudents(int n) {
        if (n <= 0) return Collections.emptyList();
        List<Student> students = new ArrayList<>();
        for (base.User u : db.getUsersByRole(enums.Role.STUDENT)) {
            students.add((Student) u);
        }
        students.sort(StudentComparators.BY_GPA_DESC);
        if (students.size() <= n) return students;
        return new ArrayList<>(students.subList(0, n));
    }

    public void rateTeacher(Student student, Teacher teacher, int rate) {
        if (student == null || teacher == null) return;
        if (rate < 1 || rate > 5) return;
        String key = student.getId() + "::" + teacher.getId();
        if (db.teacherRatings.containsKey(key)) return; // once per student
        db.teacherRatings.put(key, rate);
        db.save();
    }
}

