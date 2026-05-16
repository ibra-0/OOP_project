package models;

import base.User;
import enums.Role;
import services.NewsService;

public class Manager extends User {
    private static final long serialVersionUID = 1L;

    private transient NewsService newsService = NewsService.getInstance();

    public Manager(String login, String password, String name) {
        super(login, hashPassword(password), name, Role.MANAGER);
    }

    public void postNews(String title, String body) {
        ensureServices();
        newsService.postNews(title, body);
    }

    public void deleteNews(String newsId) {
        ensureServices();
        newsService.deleteNews(newsId);
    }

    private void ensureServices() {
        if (newsService == null) newsService = NewsService.getInstance();
    }

    @Override
    public String getDetails() {
        return String.format("Manager: %s (%s)", name, login);
    }
}

