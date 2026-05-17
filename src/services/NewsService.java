package services;

/** Publishes and removes news items via the central {@link Database}. */
public class NewsService {
    private static NewsService instance;
    private final Database db = Database.getInstance();

    private NewsService() {}

    public static NewsService getInstance() {
        if (instance == null) instance = new NewsService();
        return instance;
    }

    public void postNews(String title, String body) {
        String t = title == null ? "" : title.trim();
        String b = body == null ? "" : body.trim();
        String text = t.isEmpty() ? b : (b.isEmpty() ? t : (t + ": " + b));
        if (text.isBlank()) return;
        db.addNews(text);
    }

    public void deleteNews(String newsId) {
        // current DB stores news as plain strings; treat id as an index
        if (newsId == null) return;
        try {
            int idx = Integer.parseInt(newsId.trim());
            if (idx < 0 || idx >= db.news.size()) return;
            db.news.remove(idx);
            db.save();
        } catch (NumberFormatException ignored) {
        }
    }
}

