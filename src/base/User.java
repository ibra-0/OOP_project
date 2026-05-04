package base;

import java.io.Serializable;
import java.util.UUID;

import exceptions.enums.Role;
import interfaces.Observer;

/**
 * Abstract base class representing a generic user in the University Management System.
 * Implements Serializable for data persistence and Observer for the notification system.
 * * @authors Batyrbekov Zhanibek, Razyyev Ibrakhim, Yeskenov Aldiyar, Nurdybmek Elizat
 * @version 2.1
 */
public abstract class User implements Serializable, Observer {
    private static final long serialVersionUID = 1L;

    protected String id;
    protected String login;
    protected String passwordHash;
    protected String name;
    protected Role role;
    protected java.util.List<models.Message> messages = new java.util.ArrayList<>();

    /**
     * Initializes a new user with a unique UUID.
     * * @param login unique username
     * @param passwordHash pre-hashed password
     * @param name display name of the user
     * @param role user's access level/role
     */
    protected User(String login, String passwordHash, String name, Role role) {
        this.id = UUID.randomUUID().toString();
        this.login = login;
        this.passwordHash = passwordHash;
        this.name = name;
        this.role = role;
    }

    /** @return the unique identification string */
    public String getId() { return id; }
    public String getLogin() { return login; }
    public String getName() { return name; }
    public Role getRole() { return role; }
    public String getPassword() { return passwordHash; }

    /**
     * Checks if the provided raw password matches the stored hash.
     * @param password raw password input
     * @return true if matches
     */
    public boolean checkPassword(String password) {
        return this.passwordHash.equals(hashPassword(password));
    }

    /**
     * Hashes a password using the String hashCode. 
     * Note: In a production system, use BCrypt or SHA-256.
     * @param password raw password
     * @return hexadecimal representation of the hash
     */
    public static String hashPassword(String password) {
        return Integer.toHexString(password.hashCode());
    }

    /** Force subclasses to provide specific profile details. */
    public abstract String getDetails();

    /**
     * Implementation of the Observer pattern. Prints a notification to the console.
     * @param news the news content to notify about
     */
    @Override
    public void update(String news) {
        System.out.println("🔔 [Notification for " + name + "]: " + news);
    }

    public void receiveMessage(models.Message msg) { this.messages.add(msg); }
    public java.util.List<models.Message> getMessages() { return messages; }
}