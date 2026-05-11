package services;

import base.User;
import enums.Role;
import models.Course;
import models.CourseRegistrationRequest;
import models.Student;
import models.Teacher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CourseService {
    private static CourseService instance;
    private final Database db = Database.getInstance();

    private CourseService() {}

    public static CourseService getInstance() {
        if (instance == null) instance = new CourseService();
        return instance;
    }

    public boolean addTeacher(Course c, Teacher t) {
        if (c == null || t == null) return false;
        if (c.getTeachers().contains(t)) return false;
        c.getTeachers().add(t);
        if (!t.getAssignedCourseObjects().contains(c)) t.getAssignedCourseObjects().add(c);
        if (!t.getAssignedCourses().contains(c.getCourseID())) t.getAssignedCourses().add(c.getCourseID());
        db.save();
        return true;
    }

    public boolean removeTeacher(Course c, Teacher t) {
        if (c == null || t == null) return false;
        boolean removed = c.getTeachers().remove(t);
        if (removed) {
            t.getAssignedCourseObjects().remove(c);
            t.getAssignedCourses().remove(c.getCourseID());
            db.save();
        }
        return removed;
    }

    public boolean registerStudent(Course c, Student s) {
        if (c == null || s == null) return false;
        if (!c.hasSpace()) return false;
        if (c.getStudents().contains(s)) return false;
        c.getStudents().add(s);
        if (!s.getEnrolledCourseObjects().contains(c)) s.getEnrolledCourseObjects().add(c);
        if (!s.getEnrolledCourses().contains(c.getCourseID())) s.getEnrolledCourses().add(c.getCourseID());
        db.save();
        return true;
    }

    public boolean dropStudent(Course c, Student s) {
        if (c == null || s == null) return false;
        boolean removed = c.getStudents().remove(s);
        if (removed) {
            s.getEnrolledCourseObjects().remove(c);
            s.getEnrolledCourses().remove(c.getCourseID());
            db.save();
        }
        return removed;
    }

    public List<Student> viewStudentsAtCourse(Course c) {
        if (c == null) return Collections.emptyList();
        return new ArrayList<>(c.getStudents());
    }

    public List<Teacher> viewTeachersAtCourse(Course c) {
        if (c == null) return Collections.emptyList();
        return new ArrayList<>(c.getTeachers());
    }

    public boolean addCourseForRegistration(Course c, int year, String name) {
        // In current model, courseId is the "name"/identifier. Keep year for request metadata later.
        if (c == null) return false;
        if (db.findCourseById(c.getCourseID()) != null) return false;
        db.courseCatalog.add(c);
        if (!db.courses.contains(c.getCourseID())) db.courses.add(c.getCourseID());
        db.save();
        return true;
    }

    public CourseRegistrationRequest requestRegistration(Student s, Course c, String reason) {
        if (s == null || c == null) return null;
        CourseRegistrationRequest req = new CourseRegistrationRequest(s.getLogin(), c.getCourseID(), reason);

        // Auto-approve when possible
        if (registerStudent(c, s)) {
            req.approve();
            db.save();
            return req;
        }

        db.registrationRequests.add(req);
        db.save();
        return req;
    }

    public List<CourseRegistrationRequest> getPendingRequests() {
        List<CourseRegistrationRequest> out = new ArrayList<>();
        for (CourseRegistrationRequest r : db.registrationRequests) {
            if (r.getStatus() == CourseRegistrationRequest.Status.PENDING) out.add(r);
        }
        return out;
    }

    public boolean approveRegistration(String registrationID) {
        CourseRegistrationRequest r = findRequest(registrationID);
        if (r == null || r.getStatus() != CourseRegistrationRequest.Status.PENDING) return false;

        User u = db.findUserByLogin(r.getStudentLogin());
        if (!(u instanceof Student)) return false;
        Student s = (Student) u;
        Course c = db.findCourseById(r.getCourseId());
        if (c == null) return false;

        if (!registerStudent(c, s)) return false;

        r.approve();
        db.save();
        return true;
    }

    public boolean rejectRegistration(String registrationID) {
        CourseRegistrationRequest r = findRequest(registrationID);
        if (r == null || r.getStatus() != CourseRegistrationRequest.Status.PENDING) return false;
        r.reject();
        db.save();
        return true;
    }

    private CourseRegistrationRequest findRequest(String id) {
        if (id == null) return null;
        String norm = id.trim();
        for (CourseRegistrationRequest r : db.registrationRequests) {
            if (r.getId().equals(norm)) return r;
        }
        return null;
    }
}

