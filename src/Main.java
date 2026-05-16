import ui.Menu;
import services.Database;
import models.Admin;
import models.Student;
import models.Teacher;

public class Main {
    public static void main(String[] args) {
        Database db = Database.getInstance();

        seedUsers(db);

        Menu menu = new Menu();
        menu.start();
    }

    private static void seedUsers(Database db) {
        boolean changed = false;

        changed |= seedIfMissing(db, new Admin("admin", "123456", "admin"));
        changed |= seedIfMissing(db, new Teacher("employee", "123456", "test"));
        changed |= seedIfMissing(db, new Teacher("kaster", "123456", "Kaster Nurmakhan"));
        changed |= seedIfMissing(db, new Student("ibrahim", "123456", "Razyyev Ibrahim"));
        changed |= seedIfMissing(db, new Student("zhanibek", "123456", "Batyrbekov Zhanibek"));
        changed |= seedIfMissing(db, new Student("elizat", "123456", "Elizat Nurdymbek"));
        changed |= seedIfMissing(db, new Student("aldyar", "123456", "Aldyar Yeskenov"));

        if (changed) {
            db.save();
        }
    }

    private static boolean seedIfMissing(Database db, base.User user) {
        if (db.findUserByLogin(user.getLogin()) != null) {
            return false;
        }
        db.addUser(user);
        return true;
    }
}