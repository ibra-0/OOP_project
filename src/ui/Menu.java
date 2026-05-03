package ui;

import base.User;
import services.AuthService;
import services.Database;
import enums.Role;
import models.Message;
import models.Student;
import models.Teacher;
import java.util.List;
import java.util.Scanner;

/**
 * Main Console User Interface. Manages session states and role-based navigation.
 * Uses a Scanner-based loop for interactive command processing.
 */

public class Menu {
    private Scanner scanner = new Scanner(System.in);
    private Database db = Database.getInstance();
    private AuthService auth = AuthService.getInstance();

    private User currentUser() { return auth.getCurrentUser(); }

/** Starts the main execution loop of the application. */
    public void start() {
        System.out.println("=== Welcome to University Management System ===");
        while (true) {
            if (!auth.isLoggedIn()) {
                showLoginMenu();
            } else {
                showMainMenu();
            }
        }
    }
/** Handles initial authentication menu. */
    private void showLoginMenu() {
        System.out.println("\n1. Login\n2. Exit");
        String choice = scanner.nextLine();

        if (choice.equals("1")) {
            System.out.print("Login: ");
            String login = scanner.nextLine();
            System.out.print("Password: ");
            String pass = scanner.nextLine();

            if (auth.login(login, pass)) {
                System.out.println("✅ Welcome, " + currentUser().getName() + "!");
            } else {
                System.out.println("❌ Invalid credentials!");
            }
        } else if (choice.equals("2")) {
            db.save();
            System.out.println("Goodbye!");
            System.exit(0);
        }
    }
/** Renders the main menu based on the current user's role (Polymorphism). */
    private void showMainMenu() {
        System.out.println("\n--- Main Menu ---");
        System.out.println("Logged in as: " + currentUser().getRole());
        
        System.out.println("1. View News");
        System.out.println("2. View My Profile");

        switch (currentUser().getRole()) {
            case STUDENT:
                System.out.println("3. View My Courses");
                System.out.println("4. Register for Course");
                break;
            case TEACHER:
                System.out.println("3. View My Students");
                System.out.println("4. Put Mark");
                break;
            case ADMIN:
                System.out.println("3. Manage Users");
                System.out.println("4. Post News");
                break;
            case MANAGER:
                System.out.println("3. Manage Requests");
                break;
        }
        
        System.out.println("5. Send Message");
        System.out.println("0. Logout");
        handleChoice();
    }
/** Dispatches actions based on user numeric input. */
    private void handleChoice() {
        String choice = scanner.nextLine();

        switch (choice) {
            case "0":
                auth.logout();
                System.out.println("Logged out successfully.");
                return;
            case "1":
                showNews();
                return;
            case "2":
                showProfile();
                return;
            case "5":
                sendMessage();
                return;
        }

        switch (currentUser().getRole()) {
            case STUDENT:
                if (choice.equals("3")) { showMyCourses(); return; }
                if (choice.equals("4")) { registerForCourse(); return; }
                break;
            case TEACHER:
                if (choice.equals("3")) { showMyStudents(); return; }
                if (choice.equals("4")) { putMark(); return; }
                break;
            case ADMIN:
                if (choice.equals("3")) { manageUsers(); return; }
                if (choice.equals("4")) { postNews(); return; }
                break;
            case MANAGER:
                if (choice.equals("3")) { manageRequests(); return; }
                break;
        }

        System.out.println("Unknown option: " + choice);
    }

    private void showNews() {
        System.out.println("\n--- News Feed ---");
        if (db.news.isEmpty()) {
            System.out.println("No news today.");
        } else {
            db.news.forEach(n -> System.out.println("- " + n));
        }
    }

    private void showProfile() {
        System.out.println("\n--- My Profile ---");
        System.out.println(currentUser().getDetails());
    }

    private void sendMessage() {
        System.out.print("Recipient login: ");
        String dest = scanner.nextLine();
        System.out.print("Message: ");
        String text = scanner.nextLine();

        User receiver = db.findUserByLogin(dest);
        if (receiver != null) {
            receiver.receiveMessage(new Message(currentUser().getLogin(), text));
            System.out.println("✅ Message sent!");
        } else {
            System.out.println("❌ User not found.");
        }
    }

    private void showMyCourses() {
        Student s = (Student) currentUser();
        System.out.println("\n--- My Courses ---");
        List<String> courses = s.getEnrolledCourses();
        if (courses.isEmpty()) {
            System.out.println("You are not enrolled in any courses yet.");
        } else {
            courses.forEach(c -> System.out.println("- " + c));
        }
    }

    private void registerForCourse() {
        Student s = (Student) currentUser();
        System.out.println("\n--- Available Courses ---");
        if (db.courses.isEmpty()) {
            System.out.println("(no catalog courses yet — you may still type a course name to register)");
        } else {
            db.courses.forEach(c -> System.out.println("- " + c));
        }
        System.out.print("Course name to register: ");
        String course = scanner.nextLine();
        if (s.enroll(course)) {
            if (!db.courses.contains(course)) db.courses.add(course);
            db.save();
            System.out.println("✅ Enrolled in " + course);
        } else {
            System.out.println("❌ Could not enroll (already enrolled or empty name).");
        }
    }

    private void showMyStudents() {
        System.out.println("\n--- Students ---");
        List<User> students = db.getUsersByRole(Role.STUDENT);
        if (students.isEmpty()) {
            System.out.println("No students registered.");
        } else {
            students.forEach(u -> System.out.println("- " + u.getName() + " (" + u.getLogin() + ")"));
        }
    }

    private void putMark() {
        Teacher t = (Teacher) currentUser();
        System.out.print("Student login: ");
        String login = scanner.nextLine();
        User u = db.findUserByLogin(login);
        if (u == null || u.getRole() != Role.STUDENT) {
            System.out.println("❌ No such student.");
            return;
        }
        System.out.print("Mark: ");
        String mark = scanner.nextLine();
        t.putMark(login, mark);
        db.save();
        System.out.println("✅ Recorded mark " + mark + " for " + login);
    }

    private void manageUsers() {
        System.out.println("\n--- Users ---");
        if (db.users.isEmpty()) {
            System.out.println("No users.");
        } else {
            for (User u : db.users) {
                System.out.println("- [" + u.getRole() + "] " + u.getLogin() + " — " + u.getName());
            }
        }
    }

    private void postNews() {
        System.out.print("Enter news text: ");
        String text = scanner.nextLine();
        db.addNews(text);
        System.out.println("✅ News posted!");
    }

    private void manageRequests() {
        System.out.println("\n--- Requests ---");
        System.out.println("No pending requests.");
    }
}