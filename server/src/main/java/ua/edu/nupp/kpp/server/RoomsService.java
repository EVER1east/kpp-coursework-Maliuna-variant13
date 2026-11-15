package ua.edu.nupp.kpp.server;

import ua.edu.nupp.kpp.server.RoomsDAO;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;



public class RoomsService {
    private final RoomsDAO dao = new RoomsDAO();

    public List<Rooms> getAll() throws SQLException {
        return dao.getAll();
    }

    public Rooms getById(long id) throws SQLException{
        return dao.getById(id);
    }

    public void add(Rooms rooms) throws SQLException {
        validate(rooms);
        dao.add(rooms);
        LOG.info("Додано нову кімнату");

    }

    public void update(Rooms rooms) throws SQLException {
        validate(rooms);

        if (rooms.getId() == null || rooms.getId()<= 0) {
            throw new IllegalArgumentException("Номер має бути додатнім");
        }
        dao.update(rooms);
        LOG.info("Оновлено кімнату");

    }

    public void delete(Rooms rooms) throws SQLException {

        if (rooms == null || rooms.getId()== null || rooms.getId()<= 0) {
            throw new IllegalArgumentException("Не вказано id номера для видалення");
        }
        dao.delete(rooms.getId());
        LOG.info("Видалено кімнату");

    }

    static void validate(Rooms room) {
        if (room == null) {
            throw new IllegalArgumentException("Дані номера відсутні");
        }

        if (room.getRoomNumber() == null || room.getRoomNumber() <= 0) {
            throw new IllegalArgumentException("Номер кімнати має бути додатним");
        }

        if (room.getPrice() == null || room.getPrice() < 0) {
            throw new IllegalArgumentException("Ціна має бути невід’ємною");
        }

        if (room.getStatus() == null) {
            throw new IllegalArgumentException("Статус номера не вказано");
        }
    }

    private static final Logger LOG =
            Logger.getLogger(RoomsService.class.getName());

}
