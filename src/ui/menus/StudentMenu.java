package ui.menus;

import base.User;
import models.Course;
import models.CourseRegistrationRequest;
import models.Mark;
import models.Student;
import models.Transcript;
import services.CourseService;
import services.Database;
import services.MarkService;
import services.NewsService;

import java.util.List;
import java.util.Scanner;

public class StudentMenu {
    private final Scanner scanner;
    private final Database db = Database.getInstance();
    private final NewsService newsService = NewsService.getInstance();
    private final CourseService courseService = CourseService.getInstance();
    private final MarkService markService = MarkService.getInstance();

    public StudentMenu(Scanner scanner) {
        this.scanner = scanner;
    }

    public void show(Student currentUser) {
        while (true) {
            System.out.println("\n--- Student Menu ---");
            System.out.println("1. View News");
            System.out.println("2. View My Profile");
            System.out.println("3. View My Courses");
            System.out.println("4. Register for Course");
            System.out.println("5. View Transcript");
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
                    showMyCourses(currentUser);
                    break;
                case "4":
                    registerForCourse(currentUser);
                    break;
                case "5":
                    showTranscript(currentUser);
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

    private void showMyCourses(Student s) {
        System.out.println("\n--- My Courses ---");
        List<Course> courses = s.getEnrolledCourseObjects();
        if (courses.isEmpty()) {
            System.out.println("You are not enrolled in any courses yet.");
            return;
        }
        courses.forEach(c -> System.out.println("- " + c.getCourseID()));
    }

    private void registerForCourse(Student s) {
        System.out.println("\n--- Available Courses ---");
        List<String> courseIds = db.getCourseIds();
        if (courseIds.isEmpty()) {
            System.out.println("No courses in catalog yet. Ask admin to create courses.");
            return;
        }
        courseIds.forEach(c -> System.out.println("- " + c));

        System.out.print("Course ID: ");
        String courseId = scanner.nextLine();
        Course course = db.findCourseById(courseId);
        if (course == null) {
            System.out.println("Course does not exist in catalog.");
            return;
        }

        CourseRegistrationRequest req = courseService.requestRegistration(s, course, "");
        if (req.getStatus() == CourseRegistrationRequest.Status.APPROVED) {
            System.out.println("Registration approved. Enrolled in " + courseId);
        } else {
            System.out.println("Registration queued for manager review. Request id: " + req.getId());
        }
    }

    private void showTranscript(Student s) {
        System.out.println("\n--- My Transcript ---");
        Transcript transcript = markService.getTranscript(s);
        if (s.getEnrolledCourseObjects().isEmpty()) {
            System.out.println("No enrolled courses yet.");
            return;
        }
        for (Course course : s.getEnrolledCourseObjects()) {
            Mark m = s.getMarkForCourse(course);
            System.out.println("- " + course.getCourseID() + ": " + (m == null ? "N/A" : m.getLetterGrade() + " (" + m.getTotal() + ")"));
        }
        System.out.println(transcript);
    }
}

