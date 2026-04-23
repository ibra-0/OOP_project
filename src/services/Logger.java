package services;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Logger implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String LOG_FILE = "app.log";
    private static final List<String> logs = new ArrayList<>();
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void log(String message) {
        String entry = "[" + dateFormat.format(new Date()) + "] " + message;
        logs.add(entry);
        writeToFile(entry);
    }

    private static void writeToFile(String entry) {
        try (FileWriter fw = new FileWriter(LOG_FILE, true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(entry);
            bw.newLine();
        } catch (IOException e) {
            System.err.println("Log write error: " + e.getMessage());
        }
    }

    public static List<String> getLogs() {
        return new ArrayList<>(logs);
    }

    public static List<String> getLogsByUser(String userId) {
        List<String> userLogs = new ArrayList<>();
        for (String log : logs) {
            if (log.contains(userId)) {
                userLogs.add(log);
            }
        }
        return userLogs;
    }

    public static void clear() {
        logs.clear();
        new File(LOG_FILE).delete();
    }
}