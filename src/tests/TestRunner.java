package tests;

import base.User;
import comparators.PaperComparators;
import enums.Role;
import exceptions.InvalidSupervisorException;
import exceptions.NotAResearcherException;
import models.Admin;
import models.Student;
import models.Teacher;
import research.ResearchManager;
import research.ResearchPaper;
import research.ResearchProject;
import research.Researcher;
import services.AuthService;
import services.CourseService;
import services.Database;
import services.Logger;
import services.UserFactory;
import services.UserService;

import java.io.File;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TestRunner {

    private static int passed = 0;
    private static int failed = 0;
    private static final List<String> failures = new ArrayList<>();

    public static void main(String[] args) {
        cleanArtifacts();
        resetSingletons();

        System.out.println("=== TestRunner: University Management System ===\n");

        testHashPasswordRoundTrip();
        testCheckPasswordWrong();
        testStudentRoleAndDetails();
        testTeacherRoleAndDetails();
        testAdminRoleAndDetails();

        testUserFactoryStudent();
        testUserFactoryTeacher();
        testUserFactoryAdmin();
        testUserFactoryManager();

        testDatabaseSingleton();
        testDatabaseAddAndFind();
        testDatabaseGetUsersByRole();
        testDatabaseAuthenticateOk();
        testDatabaseAuthenticateWrongPass();
        testDatabaseAuthenticateUnknownLogin();
        testDatabaseAddNews();

        testAuthServiceLoginLogout();
        testAuthServiceWrongPass();
        testAuthServiceRoleChecks();

        testLoggerByUser();
        testDatabaseSingletonRoundTrip();
        testUserServiceAddDuplicateRejected();
        testCourseServiceRegisterStudentLinksBothSides();
        testCourseServiceAssignTeacherLinksBothSides();

        testResearchPaperCompareToRecentFirst();
        testResearchPaperEqualsByDoi();
        testResearchPaperEqualsByTitleAndDateWhenNoDoi();
        testResearchPaperRejectsNullDate();
        testTeacherIsResearcher();
        testTeacherInResearchManager();

        testPaperComparatorsByCitations();
        testPaperComparatorsByDate();
        testPaperComparatorsByPages();

        testResearchProjectAddParticipantOk();
        testResearchProjectAddParticipantNonResearcher();

        testResearchManagerAssignSupervisorOk();
        testResearchManagerAssignSupervisorBelowMin();
        testResearchManagerAssignSupervisorNonResearcher();
        testResearchManagerCollectAndTopCited();

        printSummary();
    }

    private static void testHashPasswordRoundTrip() {
        Student s = new Student("alice", "secret", "Alice");
        check("hashPassword: correct password verifies", s.checkPassword("secret"));
    }

    private static void testCheckPasswordWrong() {
        Student s = new Student("bob", "right", "Bob");
        check("hashPassword: wrong password rejected", !s.checkPassword("wrong"));
    }

    private static void testStudentRoleAndDetails() {
        Student s = new Student("s1", "p", "Sam");
        check("Student role is STUDENT", s.getRole() == Role.STUDENT);
        check("Student getDetails contains name and login",
                s.getDetails().contains("Sam") && s.getDetails().contains("s1"));
    }

    private static void testTeacherRoleAndDetails() {
        Teacher t = new Teacher("t1", "p", "Tom");
        check("Teacher role is TEACHER", t.getRole() == Role.TEACHER);
        check("Teacher getDetails contains name", t.getDetails().contains("Tom"));
    }

    private static void testAdminRoleAndDetails() {
        Admin a = new Admin("a1", "p", "Ann");
        check("Admin role is ADMIN", a.getRole() == Role.ADMIN);
        check("Admin getDetails contains name", a.getDetails().contains("Ann"));
    }

    private static void testUserFactoryStudent() {
        User u = UserFactory.createUser(Role.STUDENT, "f1", "p", "F1");
        check("UserFactory: STUDENT -> Student", u instanceof Student && u.getRole() == Role.STUDENT);
    }

    private static void testUserFactoryTeacher() {
        User u = UserFactory.createUser(Role.TEACHER, "f2", "p", "F2");
        check("UserFactory: TEACHER -> Teacher", u instanceof Teacher && u.getRole() == Role.TEACHER);
    }

    private static void testUserFactoryAdmin() {
        User u = UserFactory.createUser(Role.ADMIN, "f3", "p", "F3");
        check("UserFactory: ADMIN -> Admin", u instanceof Admin && u.getRole() == Role.ADMIN);
    }

    private static void testUserFactoryManager() {
        User u = UserFactory.createUser(Role.MANAGER, "f4", "p", "F4");
        check("UserFactory: MANAGER -> Manager", u instanceof models.Manager && u.getRole() == Role.MANAGER);
    }

    private static void testDatabaseSingleton() {
        Database d1 = Database.getInstance();
        Database d2 = Database.getInstance();
        check("Database singleton: same instance on repeat call", d1 == d2);
    }

    private static void testDatabaseAddAndFind() {
        Database db = Database.getInstance();
        Student s = new Student("findme", "p", "Find Me");
        db.addUser(s);
        check("Database.findUserByLogin returns added user", db.findUserByLogin("findme") == s);
        check("Database.findUserById returns added user", db.findUserById(s.getId()) == s);
        check("Database.findUserByLogin unknown returns null", db.findUserByLogin("nobody-here") == null);
    }

    private static void testDatabaseGetUsersByRole() {
        Database db = Database.getInstance();
        int before = db.getUsersByRole(Role.TEACHER).size();
        db.addUser(new Teacher("teach1", "p", "Teach One"));
        db.addUser(new Teacher("teach2", "p", "Teach Two"));
        int after = db.getUsersByRole(Role.TEACHER).size();
        check("Database.getUsersByRole filters by role", after == before + 2);
    }

    private static void testDatabaseAuthenticateOk() {
        Database db = Database.getInstance();
        db.addUser(new Student("auth1", "pw1", "Auth One"));
        check("Database.authenticate: correct credentials returns user",
                db.authenticate("auth1", "pw1") != null);
    }

    private static void testDatabaseAuthenticateWrongPass() {
        Database db = Database.getInstance();
        check("Database.authenticate: wrong password returns null",
                db.authenticate("auth1", "WRONG") == null);
    }

    private static void testDatabaseAuthenticateUnknownLogin() {
        Database db = Database.getInstance();
        check("Database.authenticate: unknown login returns null",
                db.authenticate("ghost", "pw") == null);
    }

    private static void testDatabaseAddNews() {
        Database db = Database.getInstance();
        int before = db.news.size();
        // Suppress observer console spam by redirecting stdout briefly.
        java.io.PrintStream oldOut = System.out;
        System.setOut(new java.io.PrintStream(new java.io.ByteArrayOutputStream()));
        try {
            db.addNews("test-news-1");
        } finally {
            System.setOut(oldOut);
        }
        check("Database.addNews appends to news list", db.news.size() == before + 1);
        check("Database.addNews stores correct text",
                db.news.get(db.news.size() - 1).equals("test-news-1"));
    }

    private static void testAuthServiceLoginLogout() {
        Database db = Database.getInstance();
        db.addUser(new Admin("admin1", "adminpw", "Admin One"));
        AuthService auth = AuthService.getInstance();
        boolean ok = auth.login("admin1", "adminpw");
        check("AuthService.login: correct credentials succeed", ok);
        check("AuthService.isLoggedIn after login", auth.isLoggedIn());
        check("AuthService.getCurrentUser returns the logged-in user",
                auth.getCurrentUser() != null && "admin1".equals(auth.getCurrentUser().getLogin()));
        auth.logout();
        check("AuthService.logout clears current user", !auth.isLoggedIn());
    }

    private static void testAuthServiceWrongPass() {
        AuthService auth = AuthService.getInstance();
        check("AuthService.login: wrong password fails", !auth.login("admin1", "WRONG"));
        check("AuthService.isLoggedIn false after failed login", !auth.isLoggedIn());
    }

    private static void testAuthServiceRoleChecks() {
        AuthService auth = AuthService.getInstance();
        auth.login("admin1", "adminpw");
        check("AuthService.hasRole(ADMIN) true for admin", auth.hasRole(Role.ADMIN));
        check("AuthService.hasRole(STUDENT) false for admin", !auth.hasRole(Role.STUDENT));
        check("AuthService.isAdmin true for admin", auth.isAdmin());
        auth.logout();
    }

    private static void testUserServiceAddDuplicateRejected() {
        Database db = Database.getInstance();
        UserService svc = UserService.getInstance();
        int before = db.users.size();
        svc.addUser(new Student("dup1", "pw", "Dup"));
        svc.addUser(new Student("dup1", "pw", "Dup"));
        int after = db.users.size();
        check("UserService.addUser: duplicate login ignored", after == before + 1);
    }

    private static void testLoggerByUser() {
        Database db = Database.getInstance();
        Student s = new Student("loguser", "pw", "Log User");
        db.addUser(s);
        List<String> byLogin = Logger.getLogsByUser("loguser");
        List<String> byId = Logger.getLogsByUser(s.getId());
        check("Logger.getLogsByUser finds entries via login", !byLogin.isEmpty());
        check("Logger.getLogsByUser finds entries via userId", !byId.isEmpty());
    }

    private static void testDatabaseSingletonRoundTrip() {
        Database before = Database.getInstance();
        try {
            java.io.ByteArrayOutputStream buf = new java.io.ByteArrayOutputStream();
            try (java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(buf)) {
                oos.writeObject(before);
            }
            try (java.io.ObjectInputStream ois = new java.io.ObjectInputStream(
                    new java.io.ByteArrayInputStream(buf.toByteArray()))) {
                Database deserialized = (Database) ois.readObject();
                check("Database singleton: readResolve points instance at deserialized object",
                        Database.getInstance() == deserialized);
            }
        } catch (Exception e) {
            check("Database singleton: serialize roundtrip throws " + e, false);
        }
    }

    private static void testCourseServiceRegisterStudentLinksBothSides() {
        Database db = Database.getInstance();
        CourseService svc = CourseService.getInstance();
        Student s = new Student("enr", "pw", "Enr");
        db.addUser(s);
        db.createCourse("Algebra", 5, "GEN", 1, 30);
        boolean ok = svc.registerStudent(db.findCourseById("Algebra"), s);
        check("CourseService.registerStudent: returns true", ok);
        check("CourseService.registerStudent: course contains student",
                db.findCourseById("Algebra").getStudents().contains(s));
        check("CourseService.registerStudent: student contains course id",
                s.getEnrolledCourses().contains("Algebra"));
    }

    private static void testCourseServiceAssignTeacherLinksBothSides() {
        Database db = Database.getInstance();
        CourseService svc = CourseService.getInstance();
        Teacher t = new Teacher("tt", "pw", "TT");
        db.addUser(t);
        db.createCourse("Physics", 5, "GEN", 1, 30);
        boolean ok = svc.addTeacher(db.findCourseById("Physics"), t);
        check("CourseService.addTeacher: returns true", ok);
        check("CourseService.addTeacher: course contains teacher",
                db.findCourseById("Physics").getTeachers().contains(t));
        check("CourseService.addTeacher: teacher contains course id",
                t.getAssignedCourses().contains("Physics"));
    }

    private static void testResearchPaperCompareToRecentFirst() {
        ResearchPaper older = paper("Old", LocalDate.of(2020, 1, 1), 10, 50, "doi-old");
        ResearchPaper newer = paper("New", LocalDate.of(2024, 6, 1), 5, 20, "doi-new");
        check("ResearchPaper.compareTo: newer < older (recent-first)",
                newer.compareTo(older) < 0);
        check("ResearchPaper.compareTo: older > newer", older.compareTo(newer) > 0);
    }

    private static void testResearchPaperEqualsByDoi() {
        ResearchPaper a = paper("Title A", LocalDate.of(2020, 1, 1), 1, 10, "doi-x");
        ResearchPaper b = paper("Title B", LocalDate.of(2024, 1, 1), 2, 20, "doi-x");
        check("ResearchPaper.equals: same DOI -> equal regardless of other fields", a.equals(b));
        check("ResearchPaper.hashCode: same DOI -> same hash", a.hashCode() == b.hashCode());
    }

    private static void testResearchPaperEqualsByTitleAndDateWhenNoDoi() {
        ResearchPaper a = paper("Same", LocalDate.of(2022, 5, 5), 1, 10, null);
        ResearchPaper b = paper("Same", LocalDate.of(2022, 5, 5), 9, 99, null);
        ResearchPaper c = paper("Same", LocalDate.of(2022, 5, 6), 1, 10, null);
        check("ResearchPaper.equals: same title+date (no DOI) -> equal", a.equals(b));
        check("ResearchPaper.equals: different date -> not equal", !a.equals(c));
    }

    private static void testResearchPaperRejectsNullDate() {
        try {
            paper("Null Date", null, 1, 10, "b");
            check("ResearchPaper: constructor rejects null publicationDate", false);
        } catch (NullPointerException e) {
            check("ResearchPaper: constructor rejects null publicationDate", true);
        }
    }

    private static void testTeacherIsResearcher() {
        Teacher t = new Teacher("prof", "pw", "Prof X");
        check("Teacher implements Researcher", t instanceof Researcher);
        Researcher r = t;
        check("Teacher.getResearcherId returns the user id", r.getResearcherId().equals(t.getId()));
        check("Teacher.getFullName returns the user name", "Prof X".equals(r.getFullName()));
        check("Teacher.getHIndex defaults to 0", r.getHIndex() == 0);
        t.setHIndex(7);
        check("Teacher.setHIndex updates h-index", r.getHIndex() == 7);
        r.addPaper(paper("p", LocalDate.of(2024, 1, 1), 42, 10, "doi-t1"));
        check("Teacher.addPaper stores paper", r.getPapers().size() == 1);
        check("Teacher.getTotalCitations sums citations", r.getTotalCitations() == 42);
    }

    private static void testTeacherInResearchManager() {
        Teacher t = new Teacher("super", "pw", "Super");
        t.setHIndex(10);
        try {
            ResearchManager.assignSupervisor(t, 5);
            check("ResearchManager.assignSupervisor: accepts a Teacher", true);
        } catch (RuntimeException e) {
            check("ResearchManager.assignSupervisor: accepts a Teacher (got " + e + ")", false);
        }
        ResearchProject proj = new ResearchProject("Topic");
        try {
            proj.addParticipant(t);
            check("ResearchProject.addParticipant: accepts a Teacher", proj.getParticipants().size() == 1);
        } catch (RuntimeException e) {
            check("ResearchProject.addParticipant: accepts a Teacher (got " + e + ")", false);
        }
    }

    private static void testPaperComparatorsByCitations() {
        ResearchPaper p1 = paper("A", LocalDate.of(2020, 1, 1), 100, 10, "a");
        ResearchPaper p2 = paper("B", LocalDate.of(2020, 1, 1), 50, 20, "b");
        ResearchPaper p3 = paper("C", LocalDate.of(2020, 1, 1), 200, 5, "c");
        List<ResearchPaper> list = new ArrayList<>(Arrays.asList(p1, p2, p3));
        list.sort(PaperComparators.BY_CITATIONS);
        check("PaperComparators.BY_CITATIONS: descending order",
                list.get(0) == p3 && list.get(1) == p1 && list.get(2) == p2);
    }

    private static void testPaperComparatorsByDate() {
        ResearchPaper old = paper("X", LocalDate.of(2010, 1, 1), 0, 1, "x");
        ResearchPaper mid = paper("Y", LocalDate.of(2020, 1, 1), 0, 1, "y");
        ResearchPaper recent = paper("Z", LocalDate.of(2025, 1, 1), 0, 1, "z");
        List<ResearchPaper> list = new ArrayList<>(Arrays.asList(old, recent, mid));
        list.sort(PaperComparators.BY_DATE);
        check("PaperComparators.BY_DATE: descending order (newest first)",
                list.get(0) == recent && list.get(1) == mid && list.get(2) == old);
    }

    private static void testPaperComparatorsByPages() {
        ResearchPaper a = paper("A", LocalDate.of(2020, 1, 1), 0, 10, "a");
        ResearchPaper b = paper("B", LocalDate.of(2020, 1, 1), 0, 50, "b");
        ResearchPaper c = paper("C", LocalDate.of(2020, 1, 1), 0, 30, "c");
        List<ResearchPaper> list = new ArrayList<>(Arrays.asList(a, b, c));
        list.sort(PaperComparators.BY_PAGES);
        check("PaperComparators.BY_PAGES: descending order (longest first)",
                list.get(0) == b && list.get(1) == c && list.get(2) == a);
    }

    private static void testResearchProjectAddParticipantOk() {
        ResearchProject proj = new ResearchProject("Quantum Foo");
        FakeResearcher r = new FakeResearcher("R1", "Rita", 5);
        proj.addParticipant(r);
        check("ResearchProject.addParticipant: Researcher added", proj.getParticipants().size() == 1);
    }

    private static void testResearchProjectAddParticipantNonResearcher() {
        ResearchProject proj = new ResearchProject("Quantum Bar");
        Student s = new Student("notres", "pw", "Not Researcher");
        try {
            proj.addParticipant(s);
            check("ResearchProject.addParticipant: throws for non-Researcher", false);
        } catch (NotAResearcherException e) {
            check("ResearchProject.addParticipant: throws NotAResearcherException for non-Researcher", true);
        }
    }

    private static void testResearchManagerAssignSupervisorOk() {
        FakeResearcher r = new FakeResearcher("S1", "Sup", 10);
        try {
            ResearchManager.assignSupervisor(r, 5);
            check("ResearchManager.assignSupervisor: passes when h-index >= min", true);
        } catch (RuntimeException e) {
            check("ResearchManager.assignSupervisor: passes when h-index >= min (got " + e + ")", false);
        }
    }

    private static void testResearchManagerAssignSupervisorBelowMin() {
        FakeResearcher r = new FakeResearcher("S2", "LowH", 2);
        try {
            ResearchManager.assignSupervisor(r, 5);
            check("ResearchManager.assignSupervisor: throws when h-index below min", false);
        } catch (InvalidSupervisorException e) {
            check("ResearchManager.assignSupervisor: throws InvalidSupervisorException below min", true);
        }
    }

    private static void testResearchManagerAssignSupervisorNonResearcher() {
        try {
            ResearchManager.assignSupervisor("a string, not a Researcher", 5);
            check("ResearchManager.assignSupervisor: throws for non-Researcher", false);
        } catch (NotAResearcherException e) {
            check("ResearchManager.assignSupervisor: throws NotAResearcherException for non-Researcher", true);
        }
    }

    private static void testResearchManagerCollectAndTopCited() {
        FakeResearcher r1 = new FakeResearcher("R1", "Alpha", 5);
        r1.addPaper(paper("p1", LocalDate.of(2020, 1, 1), 100, 10, "p1"));
        r1.addPaper(paper("p2", LocalDate.of(2021, 1, 1), 50, 10, "p2"));
        FakeResearcher r2 = new FakeResearcher("R2", "Beta", 8);
        r2.addPaper(paper("p3", LocalDate.of(2022, 1, 1), 300, 10, "p3"));
        List<Researcher> all = Arrays.asList(r1, r2);
        check("ResearchManager.collectAllPapers: aggregates across researchers",
                ResearchManager.collectAllPapers(all).size() == 3);
        check("ResearchManager.topCitedResearcher: picks highest total citations",
                ResearchManager.topCitedResearcher(all) == r2);
    }

    private static ResearchPaper paper(String title, LocalDate date, int citations, int pages, String doi) {
        return new ResearchPaper(title, Collections.singletonList("Author"),
                "Journal", pages, date, doi, citations);
    }

    private static void check(String name, boolean ok) {
        if (ok) {
            passed++;
            System.out.println("[PASS] " + name);
        } else {
            failed++;
            failures.add(name);
            System.out.println("[FAIL] " + name);
        }
    }

    private static void cleanArtifacts() {
        new File("database.dat").delete();
        new File("app.log").delete();
        Logger.clear();
    }

    private static void resetSingletons() {
        // Database & AuthService keep static state across previous runs in same JVM.
        // For a fresh JVM start (the normal case), this is a no-op. Reflection used
        // defensively so the harness is reusable if invoked multiple times.
        try {
            Field instField = Database.class.getDeclaredField("instance");
            instField.setAccessible(true);
            instField.set(null, null);
        } catch (ReflectiveOperationException ignored) { }
        try {
            Field instField = AuthService.class.getDeclaredField("instance");
            instField.setAccessible(true);
            instField.set(null, null);
        } catch (ReflectiveOperationException ignored) { }
    }

    private static void printSummary() {
        System.out.println();
        System.out.println("=== Summary ===");
        System.out.println("Tests: " + passed + "/" + (passed + failed) + " passed");
        if (failed > 0) {
            System.out.println("Failures (each one indicates either a real defect or a coverage gap):");
            for (String f : failures) System.out.println("  - " + f);
        }
    }

    // ------------------------------------------------------------------
    // Test-only Researcher impl (no production class implements Researcher).
    // ------------------------------------------------------------------
    private static final class FakeResearcher implements Researcher {
        private final String id;
        private final String name;
        private final int hIndex;
        private final List<ResearchPaper> papers = new ArrayList<>();
        private final List<ResearchProject> projects = new ArrayList<>();

        FakeResearcher(String id, String name, int hIndex) {
            this.id = id;
            this.name = name;
            this.hIndex = hIndex;
        }

        @Override public String getResearcherId() { return id; }
        @Override public String getFullName() { return name; }
        @Override public int getHIndex() { return hIndex; }
        @Override public List<ResearchPaper> getPapers() { return papers; }
        @Override public List<ResearchProject> getProjects() { return projects; }
        @Override public void addPaper(ResearchPaper paper) { papers.add(paper); }
        @Override public void addProject(ResearchProject project) { projects.add(project); }
    }
}
