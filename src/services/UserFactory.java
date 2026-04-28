package services;

import base.User;
import models.Student;
import models.Teacher;
import enums.Role;
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
             default:
                throw new IllegalArgumentException("Unknown role: " + role);
        }
    }
}