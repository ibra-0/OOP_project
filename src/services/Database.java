package services;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import base.User;
<<<<<<< HEAD
import exceptions.enums.Role;
=======
import enums.Role;
import models.Course;
import models.Student;
import models.Teacher;
>>>>>>> eb511213a56517825a4f95e8fbc8e0c1452b9f68
import services.Logger;

/**
 * Singleton class representing the central data repository.
 * Handles Binary Serialization to database.dat and implements Observer broadcasting.
 */

public class Database implements Serializable {
    private static final long serialVersionUID = 1L;
    private static Database instance;
    private static final String DATA_FILE = "database.dat";

    public List<User> users = new ArrayList<>();
    // Legacy string catalog kept for compatibility with previous serialized data.
    public List<String> courses = new ArrayList<>();
    public List<Course> courseCatalog = new ArrayList<>();
    public List<String> news = new ArrayList<>();

    private Database() {}
    /** @return the unique instance of the Database (Singleton) */

    public static Database getInstance() {
        if (instance == null) {
            instance = load();
            if (instance == null) {
                instance = new Database();
            }
        }
        instance.ensureInitialized();
        return instance;
    }
/**
     * Broadcasts a news item to all registered users (Observer Pattern).
     * @param text news content
     */
    public void addUser(User user) {
        ensureInitialized();
        users.add(user);
        Logger.log("User added: " + user.getLogin() + " [id=" + user.getId() + "] with role " + user.getRole());
    }

    public boolean removeUser(String userId) {
        ensureInitialized();
        User user = findUserById(userId);
        if (user != null) {
            users.remove(user);
            Logger.log("User removed: " + user.getLogin() + " [id=" + user.getId() + "]");
            return true;
        }
        return false;
    }

    public User findUserById(String id) {
        ensureInitialized();
        return users.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public User findUserByLogin(String login) {
        ensureInitialized();
        return users.stream()
                .filter(u -> u.getLogin().equals(login))
                .findFirst()
                .orElse(null);
    }

    public List<User> getUsersByRole(Role role) {
        ensureInitialized();
        List<User> result = new ArrayList<>();
        for (User user : users) {
            if (user.getRole() == role) {
                result.add(user);
            }
        }
        return result;
    }

    public boolean createCourse(String courseId, int credits, String major, int year, int maxStudents) {
        ensureInitialized();
        if (courseId == null) return false;
        String normalized = courseId.trim();
        if (normalized.isEmpty() || findCourseById(normalized) != null) return false;

        courseCatalog.add(new Course(normalized, credits, major, year, maxStudents));
        if (!courses.contains(normalized)) courses.add(normalized);
        return true;
    }

    public boolean assignTeacherToCourse(String courseId, String teacherLogin) {
        ensureInitialized();
        if (courseId == null || teacherLogin == null) return false;
        Course course = findCourseById(courseId);
        if (course == null) return false;
        User u = findUserByLogin(teacherLogin);
        if (!(u instanceof Teacher)) return false;

        return course.addTeacher((Teacher) u);
    }

    public List<Course> getCoursesByTeacher(String teacherLogin) {
        ensureInitialized();
        List<Course> result = new ArrayList<>();
        for (Course course : courseCatalog) {
            for (Teacher teacher : course.getTeachers()) {
                if (teacher.getLogin().equals(teacherLogin)) {
                    result.add(course);
                    break;
                }
            }
        }
        return result;
    }

    public Course findCourseById(String courseId) {
        ensureInitialized();
        if (courseId == null) return null;
        for (Course c : courseCatalog) {
            if (c.getCourseID().equals(courseId)) return c;
        }
        return null;
    }

    public List<String> getCourseIds() {
        ensureInitialized();
        List<String> ids = new ArrayList<>();
        for (Course c : courseCatalog) ids.add(c.getCourseID());
        return ids;
    }

    public boolean registerStudentToCourse(String studentLogin, String courseId) {
        ensureInitialized();
        User u = findUserByLogin(studentLogin);
        if (!(u instanceof Student)) return false;
        Course c = findCourseById(courseId);
        if (c == null) return false;
        return c.addStudent((Student) u);
    }
/** Serializes the entire database state to a file. */
    public void save() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(this);
            Logger.log("Database saved");
        } catch (IOException e) {
            Logger.log("Save error: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private static Database load() {
        File file = new File(DATA_FILE);
        if (!file.exists()) return null;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Logger.log("Database loaded from file");
            return (Database) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            Logger.log("Load error: " + e.getMessage());
            return null;
        }
    }
    public User authenticate(String login, String password) {
    User user = findUserByLogin(login);
    if (user != null && user.getPassword().equals(User.hashPassword(password))) {
        return user;
    }
    return null;
}

public void addNews(String text) {
    ensureInitialized();
    news.add(text);
    for (User user : users) {
        user.update(text);
    }
    save();
}

    private Object readResolve() {
        ensureInitialized();
        instance = this;
        return this;
    }

    private void ensureInitialized() {
        if (users == null) users = new ArrayList<>();
        if (courses == null) courses = new ArrayList<>();
        if (courseCatalog == null) courseCatalog = new ArrayList<>();
        if (news == null) news = new ArrayList<>();

        // Migrate legacy string-only courses into Course objects.
        if (!courses.isEmpty()) {
            for (String id : courses) {
                if (id == null || id.isBlank()) continue;
                if (findCourseByIdInternal(id) == null) {
                    courseCatalog.add(new Course(id, 5, "GENERAL", 1, 30));
                }
            }
        }
    }

    private Course findCourseByIdInternal(String courseId) {
        for (Course c : courseCatalog) {
            if (c.getCourseID().equals(courseId)) return c;
        }
        return null;
    }

}