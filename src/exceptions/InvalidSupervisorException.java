package exceptions;

public class InvalidSupervisorException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public InvalidSupervisorException(String message) { super(message); }
}