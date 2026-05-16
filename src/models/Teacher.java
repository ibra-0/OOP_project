package models;

import base.User;
<<<<<<< HEAD
import exceptions.enums.Role;
=======
import enums.Role;
import research.ResearchPaper;
import research.ResearchProject;
import research.Researcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Teacher extends User implements Researcher {
    private static final long serialVersionUID = 3L;

    private Map<String, String> marks = new LinkedHashMap<>();
    private List<String> assignedCourses = new ArrayList<>();
    private List<Course> assignedCourseObjects = new ArrayList<>();
    private int hIndex = 0;
    private List<ResearchPaper> papers = new ArrayList<>();
    private List<ResearchProject> projects = new ArrayList<>();
>>>>>>> eb511213a56517825a4f95e8fbc8e0c1452b9f68

    public Teacher(String login, String password, String name) {
        super(login, hashPassword(password), name, Role.TEACHER);
    }

    public Map<String, String> getMarks() {
        if (marks == null) marks = new LinkedHashMap<>();
        return Collections.unmodifiableMap(marks);
    }

    public void putMark(String studentLogin, String mark) {
        if (marks == null) marks = new LinkedHashMap<>();
        marks.put(studentLogin, mark);
    }

    public List<String> getAssignedCourses() {
        if (assignedCourses == null) assignedCourses = new ArrayList<>();
        return Collections.unmodifiableList(assignedCourses);
    }

    public boolean assignCourse(String course) {
        if (assignedCourses == null) assignedCourses = new ArrayList<>();
        if (course == null || course.isBlank() || assignedCourses.contains(course)) return false;
        assignedCourses.add(course);
        return true;
    }

    public boolean assignCourse(Course course) {
        if (assignedCourseObjects == null) assignedCourseObjects = new ArrayList<>();
        if (course == null || assignedCourseObjects.contains(course)) return false;
        assignedCourseObjects.add(course);
        return assignCourse(course.getCourseID());
    }

    public List<Course> getAssignedCourseObjects() {
        if (assignedCourseObjects == null) assignedCourseObjects = new ArrayList<>();
        return Collections.unmodifiableList(assignedCourseObjects);
    }

    public void setHIndex(int hIndex) { this.hIndex = hIndex; }

    @Override public String getResearcherId() { return getId(); }
    @Override public String getFullName() { return name; }
    @Override public int getHIndex() { return hIndex; }

    @Override
    public List<ResearchPaper> getPapers() {
        if (papers == null) papers = new ArrayList<>();
        return papers;
    }

    @Override
    public List<ResearchProject> getProjects() {
        if (projects == null) projects = new ArrayList<>();
        return projects;
    }

    @Override
    public void addPaper(ResearchPaper paper) {
        if (papers == null) papers = new ArrayList<>();
        papers.add(paper);
    }

    @Override
    public void addProject(ResearchProject project) {
        if (projects == null) projects = new ArrayList<>();
        projects.add(project);
    }

    @Override
    public String getDetails() {
        return String.format("Teacher: %s (%s) — h-index %d, %d papers",
                name, login, hIndex, getPapers().size());
    }
}