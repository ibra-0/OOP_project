package ui;

import base.User;
import services.AuthService;
import services.Database;
import enums.Role;
import models.Student;
import models.Teacher;
import java.util.Scanner;
import ui.menus.AdminMenu;
import ui.menus.ManagerMenu;
import ui.menus.StudentMenu;
import ui.menus.TeacherMenu;

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
                System.out.println("Welcome, " + currentUser().getName() + "!");
            } else {
                System.out.println("Invalid credentials!");
            }
        } else if (choice.equals("2")) {
            db.save();
            System.out.println("Goodbye!");
            System.exit(0);
        }
    }
/** Renders the main menu based on the current user's role (Polymorphism). */
    private void showMainMenu() {
        switch (currentUser().getRole()) {
            case STUDENT:
                new StudentMenu(scanner).show((Student) currentUser());
                break;
            case TEACHER:
                new TeacherMenu(scanner).show((Teacher) currentUser());
                break;
            case ADMIN:
                new AdminMenu(scanner).show((models.Admin) currentUser());
                break;
            case MANAGER:
                new ManagerMenu(scanner).show(currentUser());
                break;
        }
        System.out.println("\n0. Logout\n(press Enter to continue)");
        scanner.nextLine();
        auth.logout();
        System.out.println("Logged out successfully.");
    }
}