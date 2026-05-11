package services;

import base.User;
import enums.Role;
import models.Message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MessageService {
    private static MessageService instance;
    private final Database db = Database.getInstance();

    private MessageService() {}

    public static MessageService getInstance() {
        if (instance == null) instance = new MessageService();
        return instance;
    }

    public void sendMessage(User from, User to, String subject, String body) {
        if (from == null || to == null) return;
        String text = format(subject, body);
        to.receiveMessage(new Message(from.getLogin(), text));
        db.save();
    }

    public void sendComplaint(User from, String body) {
        if (from == null) return;
        List<User> managers = db.getUsersByRole(Role.MANAGER);
        if (managers.isEmpty()) {
            // fallback: send to admins if no managers exist
            managers = db.getUsersByRole(Role.ADMIN);
        }
        for (User manager : managers) {
            sendMessage(from, manager, "Complaint", body);
        }
    }

    public List<Message> getInbox(User of) {
        if (of == null) return Collections.emptyList();
        return new ArrayList<>(of.getMessages());
    }

    private String format(String subject, String body) {
        String s = subject == null ? "" : subject.trim();
        String b = body == null ? "" : body.trim();
        if (s.isEmpty()) return b;
        if (b.isEmpty()) return s;
        return s + " — " + b;
    }
}

