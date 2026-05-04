package comparators;

import models.Student;

import java.util.Comparator;

public final class StudentComparators {
    private StudentComparators() { }

    public static final Comparator<Student> BY_NAME =
            Comparator.comparing(Student::getName, String.CASE_INSENSITIVE_ORDER);

    public static final Comparator<Student> BY_GPA_DESC =
            Comparator.<Student>comparingDouble(s -> s.getTranscript().getGpa()).reversed();
}
