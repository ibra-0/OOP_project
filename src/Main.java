import ui.Menu;
import services.Database;
import models.Student;

public class Main {
    public static void main(String[] args) {
        Database db = Database.getInstance();

        if (db.findUserByLogin("test") == null) {
            db.addUser(new Student("test", "123", "Test Student"));
            db.save();
        }

        Menu menu = new Menu();
        menu.start();
    }
}