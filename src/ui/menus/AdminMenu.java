package ui.menus;

import base.User;
import enums.Role;
import models.Message;
import services.Database;
import services.MessageService;
import services.UserService;

import java.util.List;
import java.util.Scanner;

public class AdminMenu {
    private final Scanner scanner;
    private final Database db = Database.getInstance();
    private final MessageService messageService = MessageService.getInstance();
    private final UserService userService = UserService.getInstance();

    public AdminMenu(Scanner scanner) {
        this.scanner = scanner;
    }

    public void show(models.Admin currentUser) {
        while (true) {
            System.out.println("\n--- Admin Menu ---");
            System.out.println("1. View News");
            System.out.println("2. View My Profile");
            System.out.println("3. Manage Users");
            System.out.println("4. Send Message");
            System.out.println("5. View Messages");
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
                    manageUsers();
                    break;
                case "4":
                    sendMessage(currentUser);
                    break;
                case "5":
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

    private void manageUsers() {
        while (true) {
            System.out.println("\n--- Users ---");
            if (db.users.isEmpty()) {
                System.out.println("No users.");
            } else {
                for (User u : db.users) {
                    System.out.println("- [" + u.getRole() + "] " + u.getLogin() + " — " + u.getName() + " (id=" + u.getId() + ")");
                }
            }

            System.out.println("1. Add user");
            System.out.println("2. Remove user by id");
            System.out.println("0. Back");
            String choice = scanner.nextLine();
            if ("0".equals(choice)) return;
            if ("1".equals(choice)) {
                System.out.print("Role (STUDENT/TEACHER/ADMIN/MANAGER): ");
                Role role = Role.valueOf(scanner.nextLine().trim().toUpperCase());
                System.out.print("Login: ");
                String login = scanner.nextLine();
                System.out.print("Password: ");
                String pass = scanner.nextLine();
                System.out.print("Name: ");
                String name = scanner.nextLine();
                User u = userService.createUser(role, login, pass, name);
                userService.addUser(u);
                System.out.println("User added (if login was unique).");
            } else if ("2".equals(choice)) {
                System.out.print("User id: ");
                String id = scanner.nextLine();
                User u = db.findUserById(id);
                if (u == null) {
                    System.out.println("No such user.");
                } else {
                    userService.removeUser(u);
                    System.out.println("Removed (if allowed).");
                }
            } else {
                System.out.println("Unknown option.");
            }
        }
    }

    private void sendMessage(models.Admin from) {
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

    private void viewMessages(models.Admin of) {
        System.out.println("\n--- Messages ---");
        List<Message> messages = messageService.getInbox(of);
        if (messages.isEmpty()) {
            System.out.println("No messages.");
        } else {
            messages.forEach(m -> System.out.println("- " + m));
        }
    }

}

