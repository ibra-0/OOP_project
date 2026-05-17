package models;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Student request to enroll in a course; may be pending, approved, or rejected.
 */
public class CourseRegistrationRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private final String studentLogin;
    private final String courseId;
    private final String reason;
    private final LocalDateTime createdAt;
    private Status status;

    /** Lifecycle state of a registration request. */
    public enum Status {
        PENDING,
        APPROVED,
        REJECTED
    }

    public CourseRegistrationRequest(String studentLogin, String courseId, String reason) {
        this.id = UUID.randomUUID().toString();
        this.studentLogin = studentLogin;
        this.courseId = courseId;
        this.reason = reason;
        this.createdAt = LocalDateTime.now();
        this.status = Status.PENDING;
    }

    public String getId() { return id; }
    public String getStudentLogin() { return studentLogin; }
    public String getCourseId() { return courseId; }
    public String getReason() { return reason; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Status getStatus() { return status; }

    public void approve() { this.status = Status.APPROVED; }
    public void reject() { this.status = Status.REJECTED; }

    @Override
    public String toString() {
        return "CourseRegistrationRequest{" +
                "id='" + id + '\'' +
                ", studentLogin='" + studentLogin + '\'' +
                ", courseId='" + courseId + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                (reason == null || reason.isBlank() ? "" : ", reason='" + reason + '\'') +
                '}';
    }
}

