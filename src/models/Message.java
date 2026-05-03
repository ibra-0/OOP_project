package models;
import java.io.Serializable;
import java.time.LocalDateTime;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private String sender;
    private String text;
    private LocalDateTime date;

    public Message(String sender, String text) {
        this.sender = sender;
        this.text = text;
        this.date = LocalDateTime.now();
    }
    @Override
    public String toString() {
        return "[" + date + "] " + sender + ": " + text;
    }
}