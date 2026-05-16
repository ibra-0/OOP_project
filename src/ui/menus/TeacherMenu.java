package ui.menus;

import base.User;
import comparators.StudentComparators;
import enums.Role;
import models.Course;
import models.Mark;
import models.Message;
import models.Student;
import models.Teacher;
import research.Researcher;
import services.CourseService;
import services.Database;
import services.MarkService;
import services.MessageService;
import services.NewsService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Scanner;

public class TeacherMenu {
    private final Scanner scanner;
    private final Database db = Database.getInstance();
    private final NewsService newsService = NewsService.getInstance();
    private final MessageService messageService = MessageService.getInstance();
    private final CourseService courseService = CourseService.getInstance();
    private final MarkService markService = MarkService.getInstance();

    public TeacherMenu(Scanner scanner) {
        this.scanner = scanner;
    }

    public void show(Teacher currentUser) {
        while (true) {
            System.out.println("\n--- Teacher Menu ---");
            System.out.println("1. View News");
            System.out.println("2. View My Profile");
            System.out.println("3. View My Courses");
            System.out.println("4. View My Students");
            System.out.println("5. Put Mark");
            System.out.println("6. Send Message");
            System.out.println("7. View Messages");
            System.out.println("8. Research Menu");
            System.out.println("0. Back");

            String choice = scanner.nextLine();
            switch (choice) {
                case "0":
                    return;
                case "1":
                    showNews();
                    break;
                case "2":
                    System.out.println(currentUser.getDetails());
                    break;
                case "3":
                    showMyTeachingCourses(currentUser);
                    break;
                case "4":
                    showMyStudents(currentUser);
                    break;
                case "5":
                    putMark(currentUser);
                    break;
                case "6":
                    sendMessage(currentUser);
                    break;
                case "7":
                    viewMessages(currentUser);
                    break;
                case "8":
                    new ResearchMenu(scanner).show(currentUser);
                    break;
                default:
                    System.out.println("Unknown option.");
            }
        }
    }

    private void showNews() {
        System.out.println("\n--- News Feed ---");
        if (db.news.isEmpty()) {
            System.out.println("No news today.");
        } else {
            for (int i = 0; i < db.news.size(); i++) {
                System.out.println(i + ") " + db.news.get(i));
            }
        }
    }

    private void sendMessage(Teacher from) {
        System.out.print("Recipient login: ");
        String dest = scanner.nextLine();
        System.out.print("Subject: ");
        String subject = scanner.nextLine();
        System.out.print("Body: ");
        String body = scanner.nextLine();

        User receiver = db.findUserByLogin(dest);
        if (receiver != null && receiver.getRole() != Role.STUDENT) {
            messageService.sendMessage(from, receiver, subject, body);
            System.out.println("Message sent!");
        } else {
            System.out.println("Employee not found.");
        }
    }

    private void viewMessages(Teacher of) {
        System.out.println("\n--- Messages ---");
        List<Message> messages = messageService.getInbox(of);
        if (messages.isEmpty()) {
            System.out.println("No messages.");
        } else {
            messages.forEach(m -> System.out.println("- " + m));
        }
    }

    private void showMyTeachingCourses(Teacher t) {
        System.out.println("\n--- My Teaching Courses ---");
        List<Course> courses = db.getCoursesByTeacher(t.getLogin());
        if (courses.isEmpty()) {
            System.out.println("You are not assigned to any courses.");
        } else {
            courses.forEach(c -> System.out.println("- " + c.getCourseID()));
        }
    }

    private void showMyStudents(Teacher t) {
        List<Course> myCourses = db.getCoursesByTeacher(t.getLogin());
        System.out.println("\n--- Students ---");
        if (myCourses.isEmpty()) {
            System.out.println("You are not assigned to any courses.");
            return;
        }

        List<Student> students = collectMyStudents(myCourses);
        if (students.isEmpty()) {
            System.out.println("No students enrolled in your courses yet.");
            return;
        }

        System.out.println("1. Show all students");
        System.out.println("2. Sort by GPA (desc)");
        System.out.println("3. Sort by name");
        System.out.print("Choose: ");
        String sortChoice = scanner.nextLine();

        Comparator<Student> comparator = null;
        if ("2".equals(sortChoice)) comparator = StudentComparators.BY_GPA_DESC;
        if ("3".equals(sortChoice)) comparator = StudentComparators.BY_NAME;
        if (comparator != null) students.sort(comparator);

        for (Student s : students) {
            double gpa = s.getTranscript().getGpa();
            System.out.printf("- %s (%s), GPA: %.2f%n", s.getName(), s.getLogin(), gpa);
        }
    }

    private void putMark(Teacher teacher) {
        List<Course> myCourses = db.getCoursesByTeacher(teacher.getLogin());
        if (myCourses.isEmpty()) {
            System.out.println("You are not assigned to any courses.");
            return;
        }
        System.out.println("Your courses:");
        myCourses.forEach(c -> System.out.println("- " + c.getCourseID()));
        System.out.print("Course: ");
        String courseId = scanner.nextLine();
        Course course = db.findCourseById(courseId);
        if (course == null || !myCourses.contains(course)) {
            System.out.println("You are not assigned to this course.");
            return;
        }

        System.out.print("Student login: ");
        String login = scanner.nextLine();
        User u = db.findUserByLogin(login);
        if (u == null || u.getRole() != Role.STUDENT) {
            System.out.println("No such student.");
            return;
        }
        Student s = (Student) u;
        if (!course.getStudents().contains(s)) {
            System.out.println("Student is not enrolled in " + courseId + ".");
            return;
        }

        int att1 = readScore("Attestation 1 (0-30): ", 0, 30);
        int att2 = readScore("Attestation 2 (0-30): ", 0, 30);
        int finalExam = readScore("Final exam (0-40): ", 0, 40);

        markService.putMark(s, course, att1, att2, finalExam);
        Mark mark = s.getMarkForCourse(course);
        System.out.println("Recorded " + mark + " for " + login + " in " + courseId);
    }

    private int readScore(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String raw = scanner.nextLine();
            try {
                int value = Integer.parseInt(raw);
                if (value < min || value > max) {
                    System.out.println("Value must be between " + min + " and " + max + ".");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid integer.");
            }
        }
    }

    private List<Student> collectMyStudents(List<Course> myCourses) {
        LinkedHashSet<Student> unique = new LinkedHashSet<>();
        for (Course course : myCourses) {
            unique.addAll(course.getStudents());
        }
        return new ArrayList<>(unique);
    }
}

