package interfaces;

/**
 * Part of the Observer Design Pattern.
 * Defines the contract for objects that should receive university notifications.
 */

public interface Observer {
    /**
     * Called when a new notification/news is available.
     * @param news text content of the update
     */
    void update(String news);
}