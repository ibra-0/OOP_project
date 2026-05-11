package comparators;

import models.Teacher;

import java.util.Comparator;

public final class TeacherComparators {
    private TeacherComparators() { }

    public static final Comparator<Teacher> BY_NAME =
            Comparator.comparing(Teacher::getName, String.CASE_INSENSITIVE_ORDER);
}

