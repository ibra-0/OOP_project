package services;

import base.User;
import enums.Role;

/** CRUD operations for {@link base.User} instances backed by {@link Database}. */
public class UserService {
    private static UserService instance;
    private final Database db = Database.getInstance();

    private UserService() {}

    public static UserService getInstance() {
        if (instance == null) instance = new UserService();
        return instance;
    }

    public void addUser(User u) {
        if (u == null) return;
        if (db.findUserByLogin(u.getLogin()) != null) return;
        db.addUser(u);
        db.save();
    }

    public void removeUser(User u) {
        if (u == null) return;
        db.removeUser(u.getId());
        db.save();
    }

    public void updateUser(User u) {
        if (u == null) return;
        User existing = db.findUserById(u.getId());
        if (existing == null) return;

        // Replace by id to avoid duplicates; keep same reference semantics simple.
        for (int i = 0; i < db.users.size(); i++) {
            if (db.users.get(i).getId().equals(u.getId())) {
                db.users.set(i, u);
                db.save();
                return;
            }
        }
    }

    public User createUser(Role role, String login, String password, String name) {
        return UserFactory.createUser(role, login, password, name);
    }
}

