package services;

import base.User;
<<<<<<< HEAD
import exceptions.enums.Role;
=======
import models.Admin;
import models.Manager;
import models.Student;
import models.Teacher;
/**
 * Factory Design Pattern implementation for User creation.
 * Centralizes object instantiation logic to maintain low coupling.
 */
public class UserFactory {

    /**
     * Static factory method to create concrete User instances.
     * * @param role target role to instantiate
     * @param login user login
     * @param password raw password (will be hashed internally)
     * @param name user full name
     * @return concrete instance of Student, Teacher, etc.
     * @throws IllegalArgumentException if the role is not supported
     */
    
    public static User createUser(Role role, String login, String password, String name) {
        switch (role) {
            case STUDENT:
                return new Student(login, password, name);
            case TEACHER:
                return new Teacher(login, password, name);
            case ADMIN:
                return new Admin(login, password, name);
            case MANAGER:
                return new Manager(login, password, name);
            default:
                throw new IllegalArgumentException("Unsupported role: " + role);
        }
    }
}