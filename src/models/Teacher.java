package models;

import base.User;
import enums.Role;
import research.ResearchPaper;
import research.ResearchProject;
import research.Researcher;

import java.util.ArrayList;
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

    public Teacher(String login, String password, String name) {
        super(login, hashPassword(password), name, Role.TEACHER);
    }

    public Map<String, String> getMarks() {
        if (marks == null) marks = new LinkedHashMap<>();
        return marks;
    }

    public List<String> getAssignedCourses() {
        if (assignedCourses == null) assignedCourses = new ArrayList<>();
        return assignedCourses;
    }

    public List<Course> getAssignedCourseObjects() {
        if (assignedCourseObjects == null) assignedCourseObjects = new ArrayList<>();
        return assignedCourseObjects;
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