package services;

public final class ServiceRegistry {
    private ServiceRegistry() {}

    public static AuthService auth() { return AuthService.getInstance(); }
    public static MessageService messages() { return MessageService.getInstance(); }
    public static NewsService news() { return NewsService.getInstance(); }
    public static UserService users() { return UserService.getInstance(); }
    public static CourseService courses() { return CourseService.getInstance(); }
    public static MarkService marks() { return MarkService.getInstance(); }
    public static ResearchService research() { return ResearchService.getInstance(); }
}

