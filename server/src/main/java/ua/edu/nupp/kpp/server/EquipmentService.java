package ua.edu.nupp.kpp.server;

import com.sun.net.httpserver.Request;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;


public class EquipmentService {

    private final EquipmentDAO dao = new EquipmentDAO();
    private final RoomsDAO roomsDAO = new RoomsDAO();

    public List<Equipment> getAll() throws SQLException {
        return dao.getAll();
    }

    public Equipment getById(long id) throws SQLException {
        return dao.getById(id);
    }

    public void add(Equipment equipment) throws SQLException {
        validate(equipment);
        requireRoom(equipment);
        dao.add(equipment);
        LOG.info("Додано нове обладнання");

    }

    public void update(Equipment equipment) throws SQLException {
        validate(equipment);

        if (equipment.getId() == null || equipment.getId() <= 0) {
            throw new IllegalArgumentException("Не вказано ID обладнання для оновлення");
        }

        requireRoom(equipment);
        dao.update(equipment);
        LOG.info("Оновлено обладнання");

    }

    public void delete (Equipment equipment) throws SQLException {
        if (equipment == null || equipment.getId() == null || equipment.getId() <= 0) {
            throw new IllegalArgumentException("Не вказано ID обладнання для видалення");
        }

        dao.delete(equipment.getId());
        LOG.info("Видалено звіт");

    }

    static void validate(Equipment equipment) {
        if (equipment == null) {
            throw new IllegalArgumentException("Дані обладнання відсутні");
        }

        if (equipment.getRoomId() <= 0) {
            throw new IllegalArgumentException("ID кімнати має бути додатним");
        }

        if (equipment.getName() == null || equipment.getName().isBlank()) {
            throw new IllegalArgumentException("Вкажіть назву обладнання");
        }

        if (equipment.getName().length() > 255) {
            throw new IllegalArgumentException(
                    "Назва обладнання не повинна перевищувати 255 символів"
            );
        }
    }

    private void requireRoom(Equipment equipment) throws SQLException {
        if (roomsDAO.getById(equipment.getRoomId()) == null) {
            throw new IllegalArgumentException("Кімнату не знайдено");
        }
    }

    private static final Logger LOG =
            Logger.getLogger(EquipmentService.class.getName());
}
