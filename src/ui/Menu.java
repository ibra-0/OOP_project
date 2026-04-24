package ui;

import base.User;
import services.Database;
import java.util.Scanner;

public class Menu {
    private Scanner scanner = new Scanner(System.in);
    private Database db = Database.getInstance();
    private User currentUser;

    public void start() {
        System.out.println("=== Welcome to University Management System ===");
        while (true) {
            if (currentUser == null) {
                showLoginMenu();
            } else {
                showMainMenu();
            }
        }
    }

    private void showLoginMenu() {
        System.out.println("\n1. Login\n2. Exit");
        String choice = scanner.nextLine();

        if (choice.equals("1")) {
            System.out.print("Login: ");
            String login = scanner.nextLine();
            System.out.print("Password: ");
            String pass = scanner.nextLine();

            currentUser = db.authenticate(login, pass);
            if (currentUser == null) {
                System.out.println("❌ Invalid credentials!");
            } else {
                System.out.println("✅ Welcome, " + currentUser.getName() + "!");
            }
        } else if (choice.equals("2")) {
            db.save();
            System.out.println("Goodbye!");
            System.exit(0);
        }
    }

    private void showMainMenu() {
        System.out.println("\n--- Main Menu ---");
        System.out.println("Logged in as: " + currentUser.getRole());
        
        System.out.println("1. View News");
        
        switch (currentUser.getRole()) {
            case STUDENT:
                System.out.println("2. View My Courses");
                System.out.println("3. Register for Course");
                break;
            case TEACHER:
                System.out.println("2. View My Students");
                System.out.println("3. Put Mark");
                break;
            case ADMIN:
                System.out.println("2. Manage Users");
                System.out.println("3. Post News");
                break;
        }
        
        System.out.println("0. Logout");
        handleChoice();
    }

    private void handleChoice() {
        String choice = scanner.nextLine();
        if (choice.equals("0")) {
            currentUser = null;
            return;
        }

        if (choice.equals("1")) {
            System.out.println("--- News Feed ---");
            // db.news.forEach(System.out.println);
            return;
        }

        System.out.println("Feature coming soon...");
    }
}