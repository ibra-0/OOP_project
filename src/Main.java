import ui.Menu;
import services.Database;
import models.Student; // или Admin, смотря кто у вас уже готов

public class Main {
    public static void main(String[] args) {
        // 1. Получаем доступ к базе
        Database db = Database.getInstance();

        // 2. ВРЕМЕННЫЙ КОД ДЛЯ ТЕСТА (удалим, когда сделаем регистрацию)
        // Добавляем тестового студента, если база пустая
        if (db.findUserByLogin("test") == null) {
            db.addUser(new Student("test", "123", "Test Student"));
            db.save(); // сохраняем в файл
        }

        // 3. Запускаем наше меню
        Menu menu = new Menu();
        menu.start();
    }
}