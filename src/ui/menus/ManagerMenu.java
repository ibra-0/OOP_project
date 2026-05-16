package ui.menus;

import base.User;
import comparators.StudentComparators;
import comparators.TeacherComparators;
import enums.Role;
import models.Course;
import models.CourseRegistrationRequest;
import models.Manager;
import models.Message;
import models.Student;
import models.Teacher;
import services.CourseService;
import services.Database;
import services.MarkService;
import services.MessageService;
import services.NewsService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class ManagerMenu {
    private final Scanner scanner;
    private final Database db = Database.getInstance();
    private final NewsService newsService = NewsService.getInstance();
    private final MessageService messageService = MessageService.getInstance();
    private final CourseService courseService = CourseService.getInstance();
    private final MarkService markService = MarkService.getInstance();

    public ManagerMenu(Scanner scanner) {
        this.scanner = scanner;
    }

    public void show(User currentUser) {
        while (true) {
            System.out.println("\n--- Manager Menu ---");
            System.out.println("1. View News");
            System.out.println("2. View My Profile");
            System.out.println("3. Manage Requests");
            System.out.println("4. Post News");
            System.out.println("5. Delete News");
            System.out.println("6. Create Course");
            System.out.println("7. Assign Teacher To Course");
            System.out.println("8. View Students (sorted)");
            System.out.println("9. View Teachers (sorted)");
            System.out.println("10. Create Academic Report");
            System.out.println("11. Send Message");
            System.out.println("12. View Messages");
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
                    manageRequests();
                    break;
                case "4":
                    postNews(currentUser);
                    break;
                case "5":
                    deleteNews(currentUser);
                    break;
                case "6":
                    createCourse();
                    break;
                case "7":
                    assignTeacherToCourse();
                    break;
                case "8":
                    viewStudentsSorted();
                    break;
                case "9":
                    viewTeachersSorted();
                    break;
                case "10":
                    createAcademicReport();
                    break;
                case "11":
                    sendMessage(currentUser);
                    break;
                case "12":
                    viewMessages(currentUser);
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

    private void postNews(User currentUser) {
        System.out.print("Title: ");
        String title = scanner.nextLine();
        System.out.print("Body: ");
        String body = scanner.nextLine();
        if (currentUser instanceof Manager) {
            ((Manager) currentUser).postNews(title, body);
        } else {
            newsService.postNews(title, body);
        }
        System.out.println("News posted!");
    }

    private void deleteNews(User currentUser) {
        showNews();
        System.out.print("News id (index): ");
        String id = scanner.nextLine();
        if (currentUser instanceof Manager) {
            ((Manager) currentUser).deleteNews(id);
        } else {
            newsService.deleteNews(id);
        }
        System.out.println("Delete attempted (if id was valid).");
    }

    private void createCourse() {
        System.out.print("Course ID: ");
        String courseId = scanner.nextLine();
        int credits = readInt("Credits: ", 1, 20);
        System.out.print("Major: ");
        String major = scanner.nextLine();
        int year = readInt("Year: ", 1, 8);
        int maxStudents = readInt("Max students: ", 1, 500);

        if (db.createCourse(courseId, credits, major, year, maxStudents)) {
            db.save();
            System.out.println("Course created.");
        } else {
            System.out.println("Could not create course (empty or already exists).");
        }
    }

    private void assignTeacherToCourse() {
        List<String> courseIds = db.getCourseIds();
        if (courseIds.isEmpty()) {
            System.out.println("No courses available. Create a course first.");
            return;
        }
        System.out.println("Courses:");
        courseIds.forEach(c -> System.out.println("- " + c));
        System.out.print("Course ID: ");
        String courseId = scanner.nextLine();

        System.out.println("Teachers:");
        List<User> teachers = db.getUsersByRole(Role.TEACHER);
        if (teachers.isEmpty()) {
            System.out.println("No teachers found.");
            return;
        }
        teachers.forEach(t -> System.out.println("- " + t.getLogin() + " (" + t.getName() + ")"));

        System.out.print("Teacher login: ");
        String teacherLogin = scanner.nextLine();
        if (db.assignTeacherToCourse(courseId, teacherLogin)) {
            db.save();
            System.out.println("Teacher assigned to course.");
        } else {
            System.out.println("Failed to assign teacher. Check course and login.");
        }
    }

    private void viewStudentsSorted() {
        List<User> users = db.getUsersByRole(Role.STUDENT);
        List<Student> students = new ArrayList<>();
        for (User u : users) students.add((Student) u);

        System.out.println("1. Sort by GPA (desc)");
        System.out.println("2. Sort by name");
        System.out.print("Choose: ");
        String sortChoice = scanner.nextLine();
        Comparator<Student> comp = "1".equals(sortChoice)
                ? StudentComparators.BY_GPA_DESC
                : StudentComparators.BY_NAME;
        students.sort(comp);

        for (Student s : students) {
            System.out.printf("- %s (%s), GPA: %.2f%n", s.getName(), s.getLogin(), s.getTranscript().getGpa());
        }
    }

    private void viewTeachersSorted() {
        List<User> users = db.getUsersByRole(Role.TEACHER);
        List<Teacher> teachers = new ArrayList<>();
        for (User u : users) teachers.add((Teacher) u);

        System.out.println("1. Sort by name");
        System.out.print("Choose: ");
        scanner.nextLine();

        teachers.sort(TeacherComparators.BY_NAME);
        for (Teacher t : teachers) {
            System.out.println("- " + t.getName() + " (" + t.getLogin() + ")");
        }
    }

    private void createAcademicReport() {
        System.out.print("Student login: ");
        String login = scanner.nextLine();
        User u = db.findUserByLogin(login);
        if (!(u instanceof Student)) {
            System.out.println("No such student.");
            return;
        }
        markService.generateAcademicReport((Student) u);
    }

    private void manageRequests() {
        while (true) {
            List<CourseRegistrationRequest> pending = courseService.getPendingRequests();
            System.out.println("\n--- Pending registration requests ---");
            if (pending.isEmpty()) {
                System.out.println("No pending requests.");
            } else {
                for (CourseRegistrationRequest r : pending) {
                    System.out.println("- " + r);
                }
            }

            System.out.println("1. Approve request by id");
            System.out.println("2. Reject request by id");
            System.out.println("0. Back");
            String choice = scanner.nextLine();
            if ("0".equals(choice)) return;
            if ("1".equals(choice)) {
                System.out.print("Request id: ");
                String id = scanner.nextLine();
                boolean ok = courseService.approveRegistration(id);
                System.out.println(ok ? "Approved." : "Could not approve.");
            } else if ("2".equals(choice)) {
                System.out.print("Request id: ");
                String id = scanner.nextLine();
                boolean ok = courseService.rejectRegistration(id);
                System.out.println(ok ? "Rejected." : "Could not reject.");
            } else {
                System.out.println("Unknown option.");
            }
        }
    }

    private void sendMessage(User from) {
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

    private void viewMessages(User of) {
        System.out.println("\n--- Messages ---");
        List<Message> messages = messageService.getInbox(of);
        if (messages.isEmpty()) {
            System.out.println("No messages.");
        } else {
            messages.forEach(m -> System.out.println("- " + m));
        }
    }

    private int readInt(String prompt, int min, int max) {
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
}

