package services;

import java.util.*;
import models.*; // Будет работать, когда появятся файлы в models

public class Database {
    private static Database instance;
    
    // Списки для хранения данных
    public List<User> users = new ArrayList<>();
    public List<Course> courses = new ArrayList<>();

    private Database() {}

    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }
    
    // Сюда ты потом добавишь методы save() и load() для сериализации
}