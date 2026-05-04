package ui;

import base.User;
import comparators.PaperComparators;
import comparators.StudentComparators;
import services.AuthService;
import services.Database;
import enums.Role;
import models.Course;
import models.Mark;
import models.Message;
import models.Student;
import models.Teacher;
import models.Transcript;
import research.ResearchManager;
import research.ResearchPaper;
import research.ResearchProject;
import research.Researcher;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
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
    private boolean isEmployee(User user) { return user.getRole() != Role.STUDENT; }

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
        System.out.println("\n--- Main Menu ---");
        System.out.println("Logged in as: " + currentUser().getRole());
        
        System.out.println("1. View News");
        System.out.println("2. View My Profile");

        switch (currentUser().getRole()) {
            case STUDENT:
                System.out.println("3. View My Courses");
                System.out.println("4. Register for Course");
                System.out.println("5. View Transcript");
                break;
            case TEACHER:
                System.out.println("3. View My Courses");
                System.out.println("4. View My Students");
                System.out.println("5. Put Mark");
                System.out.println("6. Send Message");
                System.out.println("7. View Messages");
                System.out.println("8. Research Menu");
                break;
            case ADMIN:
                System.out.println("3. Manage Users");
                System.out.println("4. Post News");
                System.out.println("5. Create Course");
                System.out.println("6. Assign Teacher To Course");
                System.out.println("7. Send Message");
                System.out.println("8. View Messages");
                break;
            case MANAGER:
                System.out.println("3. Manage Requests");
                System.out.println("4. Send Message");
                System.out.println("5. View Messages");
                break;
        }
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
        }

        switch (currentUser().getRole()) {
            case STUDENT:
                if (choice.equals("3")) { showMyCourses(); return; }
                if (choice.equals("4")) { registerForCourse(); return; }
                if (choice.equals("5")) { showTranscript(); return; }
                break;
            case TEACHER:
                if (choice.equals("3")) { showMyTeachingCourses(); return; }
                if (choice.equals("4")) { showMyStudents(); return; }
                if (choice.equals("5")) { putMark(); return; }
                if (choice.equals("6")) { sendMessage(); return; }
                if (choice.equals("7")) { viewMessages(); return; }
                if (choice.equals("8")) { showResearchMenu(); return; }
                break;
            case ADMIN:
                if (choice.equals("3")) { manageUsers(); return; }
                if (choice.equals("4")) { postNews(); return; }
                if (choice.equals("5")) { createCourse(); return; }
                if (choice.equals("6")) { assignTeacherToCourse(); return; }
                if (choice.equals("7")) { sendMessage(); return; }
                if (choice.equals("8")) { viewMessages(); return; }
                break;
            case MANAGER:
                if (choice.equals("3")) { manageRequests(); return; }
                if (choice.equals("4")) { sendMessage(); return; }
                if (choice.equals("5")) { viewMessages(); return; }
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
        if (receiver != null && isEmployee(receiver)) {
            receiver.receiveMessage(new Message(currentUser().getLogin(), text));
            db.save();
            System.out.println("Message sent!");
        } else {
            System.out.println("Employee not found.");
        }
    }

    private void viewMessages() {
        System.out.println("\n--- Messages ---");
        List<Message> messages = currentUser().getMessages();
        if (messages.isEmpty()) {
            System.out.println("No messages.");
        } else {
            messages.forEach(m -> System.out.println("- " + m));
        }
    }

    private void showMyCourses() {
        Student s = (Student) currentUser();
        System.out.println("\n--- My Courses ---");
        List<Course> courses = s.getEnrolledCourseObjects();
        if (courses.isEmpty()) {
            System.out.println("You are not enrolled in any courses yet.");
        } else {
            courses.forEach(c -> System.out.println("- " + c.getCourseID()));
        }
    }

    private void registerForCourse() {
        Student s = (Student) currentUser();
        System.out.println("\n--- Available Courses ---");
        List<String> courseIds = db.getCourseIds();
        if (courseIds.isEmpty()) {
            System.out.println("No courses in catalog yet. Ask admin to create courses.");
            return;
        } else {
            courseIds.forEach(c -> System.out.println("- " + c));
        }
        System.out.print("Course name to register: ");
        String courseId = scanner.nextLine();
        Course course = db.findCourseById(courseId);
        if (course == null) {
            System.out.println("Course does not exist in catalog.");
            return;
        }
        if (!course.hasSpace()) {
            System.out.println("Registration rejected: course is full.");
            return;
        }
        if (db.registerStudentToCourse(s.getLogin(), courseId)) {
            db.save();
            System.out.println("Registration approved. Enrolled in " + courseId);
        } else {
            System.out.println("Registration rejected: already enrolled.");
        }
    }

    private void showTranscript() {
        Student s = (Student) currentUser();
        System.out.println("\n--- My Transcript ---");
        Transcript transcript = s.getTranscript();
        if (s.getEnrolledCourseObjects().isEmpty()) {
            System.out.println("No enrolled courses yet.");
            return;
        }
        for (Course course : s.getEnrolledCourseObjects()) {
            Mark m = s.getMarkForCourse(course);
            System.out.println("- " + course.getCourseID() + ": " + (m == null ? "N/A" : m.getLetterGrade() + " (" + m.getTotal() + ")"));
        }
        System.out.println(transcript);
    }

    private void showMyTeachingCourses() {
        Teacher t = (Teacher) currentUser();
        System.out.println("\n--- My Teaching Courses ---");
        List<Course> courses = db.getCoursesByTeacher(t.getLogin());
        if (courses.isEmpty()) {
            System.out.println("You are not assigned to any courses.");
        } else {
            courses.forEach(c -> System.out.println("- " + c.getCourseID()));
        }
    }

    private void showMyStudents() {
        Teacher t = (Teacher) currentUser();
        List<Course> myCourses = db.getCoursesByTeacher(t.getLogin());
        System.out.println("\n--- Students ---");
        if (myCourses.isEmpty()) {
            System.out.println("You are not assigned to any courses.");
            return;
        }

        List<Student> students = collectMyStudents(myCourses);
        if (students.isEmpty()) {
            System.out.println("No students enrolled in your courses yet.");
            return;
        }

        System.out.println("1. Show all students");
        System.out.println("2. Sort by GPA (desc)");
        System.out.println("3. Sort by name");
        System.out.print("Choose: ");
        String sortChoice = scanner.nextLine();

        Comparator<Student> comparator = null;
        if ("2".equals(sortChoice)) comparator = StudentComparators.BY_GPA_DESC;
        if ("3".equals(sortChoice)) comparator = StudentComparators.BY_NAME;
        if (comparator != null) students.sort(comparator);

        for (Student s : students) {
            double gpa = s.getTranscript().getGpa();
            System.out.printf("- %s (%s), GPA: %.2f%n", s.getName(), s.getLogin(), gpa);
        }
    }

    private void putMark() {
        Teacher t = (Teacher) currentUser();
        List<Course> myCourses = db.getCoursesByTeacher(t.getLogin());
        if (myCourses.isEmpty()) {
            System.out.println("You are not assigned to any courses.");
            return;
        }
        System.out.println("Your courses:");
        myCourses.forEach(c -> System.out.println("- " + c.getCourseID()));
        System.out.print("Course: ");
        String courseId = scanner.nextLine();
        Course course = db.findCourseById(courseId);
        if (course == null || !myCourses.contains(course)) {
            System.out.println("You are not assigned to this course.");
            return;
        }

        System.out.print("Student login: ");
        String login = scanner.nextLine();
        User u = db.findUserByLogin(login);
        if (u == null || u.getRole() != Role.STUDENT) {
            System.out.println("No such student.");
            return;
        }
        Student s = (Student) u;
        if (!course.getStudents().contains(s)) {
            System.out.println("Student is not enrolled in " + courseId + ".");
            return;
        }

        int att1 = readScore("Attestation 1 (0-30): ", 0, 30);
        int att2 = readScore("Attestation 2 (0-30): ", 0, 30);
        int finalExam = readScore("Final exam (0-40): ", 0, 40);

        Mark mark = new Mark(att1, att2, finalExam, s, course);
        t.putMark(login + "@" + courseId, mark.getLetterGrade());
        s.addMark(mark);
        db.save();
        System.out.println("Recorded " + mark + " for " + login + " in " + courseId);
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
        System.out.println("News posted!");
    }

    private void createCourse() {
        System.out.print("Course ID: ");
        String courseId = scanner.nextLine();
        int credits = readScore("Credits: ", 1, 20);
        System.out.print("Major: ");
        String major = scanner.nextLine();
        int year = readScore("Year: ", 1, 8);
        int maxStudents = readScore("Max students: ", 1, 500);

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
        System.out.print("Course name: ");
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

    private int readScore(String prompt, int min, int max) {
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

    private void showResearchMenu() {
        Teacher t = (Teacher) currentUser();
        while (true) {
            System.out.println("\n--- Research Menu ---");
            System.out.println("1. Set h-index");
            System.out.println("2. Add research paper");
            System.out.println("3. Create research project");
            System.out.println("4. View my papers (by citations)");
            System.out.println("5. View top cited researcher");
            System.out.println("0. Back");
            String choice = scanner.nextLine();

            switch (choice) {
                case "0":
                    return;
                case "1":
                    int h = readScore("h-index: ", 0, 1000);
                    t.setHIndex(h);
                    db.save();
                    System.out.println("h-index updated.");
                    break;
                case "2":
                    addResearchPaper(t);
                    break;
                case "3":
                    createResearchProject(t);
                    break;
                case "4":
                    printMyPapersByCitations(t);
                    break;
                case "5":
                    printTopCitedResearcher();
                    break;
                default:
                    System.out.println("Unknown option.");
            }
        }
    }

    private void addResearchPaper(Teacher teacher) {
        System.out.print("Title: ");
        String title = scanner.nextLine();
        System.out.print("Authors (comma separated, empty for teacher name): ");
        String authorsInput = scanner.nextLine();
        System.out.print("Journal: ");
        String journal = scanner.nextLine();
        int pages = readScore("Pages: ", 1, 10000);
        System.out.print("Publication date (YYYY-MM-DD): ");
        String dateRaw = scanner.nextLine();
        System.out.print("DOI: ");
        String doi = scanner.nextLine();
        int citations = readScore("Citations: ", 0, 1000000);

        try {
            LocalDate date = LocalDate.parse(dateRaw);
            List<String> authors = parseAuthors(authorsInput, teacher.getName());
            ResearchPaper paper = new ResearchPaper(title, authors, journal, pages, date, doi, citations);
            teacher.addPaper(paper);
            db.save();
            System.out.println("Research paper added.");
        } catch (Exception e) {
            System.out.println("Invalid date format. Use YYYY-MM-DD.");
        }
    }

    private void createResearchProject(Teacher teacher) {
        System.out.print("Project topic: ");
        String topic = scanner.nextLine();
        if (topic == null || topic.isBlank()) {
            System.out.println("Topic cannot be empty.");
            return;
        }
        ResearchProject project = new ResearchProject(topic);
        project.addParticipant(teacher);
        teacher.addProject(project);
        db.save();
        System.out.println("Research project created and you were added as participant.");
    }

    private void printMyPapersByCitations(Teacher teacher) {
        System.out.println("\n--- My Papers (by citations) ---");
        if (teacher.getPapers().isEmpty()) {
            System.out.println("No papers yet.");
            return;
        }
        teacher.printPapers(PaperComparators.BY_CITATIONS);
    }

    private void printTopCitedResearcher() {
        List<Researcher> researchers = new ArrayList<>();
        for (User u : db.getUsersByRole(Role.TEACHER)) {
            if (u instanceof Researcher) researchers.add((Researcher) u);
        }
        if (researchers.isEmpty()) {
            System.out.println("No researchers found.");
            return;
        }
        Researcher top = ResearchManager.topCitedResearcher(researchers);
        if (top == null) {
            System.out.println("No citation data yet.");
            return;
        }
        System.out.println("Top cited researcher: " + top.getFullName()
                + " (total citations: " + top.getTotalCitations() + ")");
    }

    private List<String> parseAuthors(String authorsInput, String fallbackAuthor) {
        List<String> authors = new ArrayList<>();
        if (authorsInput != null && !authorsInput.isBlank()) {
            for (String item : authorsInput.split(",")) {
                String a = item.trim();
                if (!a.isEmpty()) authors.add(a);
            }
        }
        if (authors.isEmpty()) authors.add(fallbackAuthor);
        return authors;
    }

    private List<Student> collectMyStudents(List<Course> myCourses) {
        LinkedHashSet<Student> unique = new LinkedHashSet<>();
        for (Course course : myCourses) {
            unique.addAll(course.getStudents());
        }
        return new ArrayList<>(unique);
    }

    private void manageRequests() {
        System.out.println("\n--- Requests ---");
        System.out.println("No pending requests.");
    }
}