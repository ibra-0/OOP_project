package ui;

import base.User;
import exceptions.enums.Role;
import services.Database;
import models.Message;
import java.util.Scanner;

/**
 * Main Console User Interface. Manages session states and role-based navigation.
 * Uses a Scanner-based loop for interactive command processing.
 */

public class Menu {
    private Scanner scanner = new Scanner(System.in);
    private Database db = Database.getInstance();
    private User currentUser;
/** Starts the main execution loop of the application. */
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
/** Handles initial authentication menu. */
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
/** Renders the main menu based on the current user's role (Polymorphism). */
    private void showMainMenu() {
        System.out.println("\n--- Main Menu ---");
        System.out.println("Logged in as: " + currentUser.getRole());
        
        System.out.println("1. View News");
        System.out.println("2. View My Profile"); // Твоя новая фича

        switch (currentUser.getRole()) {
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
                System.out.println("2. Manage Requests"); // Или что там по ТЗ
                break;
        }
        
        System.out.println("0. Logout");
        handleChoice();
    }
/** Dispatches actions based on user numeric input. */
    private void handleChoice() {
        String choice = scanner.nextLine();
        
        if (choice.equals("0")) {
            currentUser = null;
            System.out.println("Logged out successfully.");
            return;
        }

        if (choice.equals("1")) {
            System.out.println("\n--- News Feed ---");
            if (db.news.isEmpty()) {
                System.out.println("No news today.");
            } else {
                db.news.forEach(n -> System.out.println("- " + n));
            }
            return;
        }

        if (choice.equals("2")) {
            System.out.println("\n--- My Profile ---");
            System.out.println(currentUser.getDetails()); 
            return;
        }

        if (currentUser.getRole() == Role.ADMIN && choice.equals("4")) {
            System.out.print("Enter news text: ");
            String text = scanner.nextLine();
            db.addNews(text);
            System.out.println("✅ News posted!");
            return;
        }
if (choice.equals("some_number")) { // выбери номер пункта
    System.out.print("Recipient login: ");
    String dest = scanner.nextLine();
    System.out.print("Message: ");
    String text = scanner.nextLine();
    
    User receiver = db.findUserByLogin(dest);
    if (receiver != null) {
        receiver.receiveMessage(new Message(currentUser.getLogin(), text));
        System.out.println("✅ Message sent!");
    } else {
        System.out.println("❌ User not found.");
    }
}

        System.out.println("Feature coming soon... Wait for team members to finish their classes!");
    }
}