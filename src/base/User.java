package base;

import enums.Role;
import java.io.Serializable;
import java.util.UUID;

public abstract class User implements Serializable {
    private static final long serialVersionUID = 1L;

    protected String id;
    protected String login;
    protected String passwordHash;
    protected String name;
    protected Role role;

    protected User(String login, String passwordHash, String name, Role role) {
        this.id = UUID.randomUUID().toString();
        this.login = login;
        this.passwordHash = passwordHash;
        this.name = name;
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getName() {
        return name;
    }

    public Role getRole() {
        return role;
    }

    public String getPassword() {
        return passwordHash;
    }

    public boolean checkPassword(String password) {
        return this.passwordHash.equals(hashPassword(password));
    }

    public void setPassword(String password) {
        this.passwordHash = hashPassword(password);
    }

    public static String hashPassword(String password) {
        return Integer.toHexString(password.hashCode());
    }

    public abstract String getDetails();

    @Override
    public String toString() {
        return String.format("%s[id=%s, name=%s, role=%s]", getClass().getSimpleName(), id, name, role);
    }
}