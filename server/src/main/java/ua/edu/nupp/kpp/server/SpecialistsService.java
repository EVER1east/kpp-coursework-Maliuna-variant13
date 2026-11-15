package ua.edu.nupp.kpp.server;

import com.sun.net.httpserver.Request;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;


public class SpecialistsService {

    private final SpecialistsDAO dao = new SpecialistsDAO();;

    public List<Specialists> getAll() throws SQLException {
        return dao.getAll();
    }

    public Specialists getById(long id) throws SQLException {
        return dao.getById(id);
    }

    public void add(Specialists specialists) throws SQLException {
        validate(specialists);
        dao.add(specialists);
        LOG.info("Додано нового працівника");

    }

    public void update(Specialists specialists) throws SQLException {
        validate(specialists);

        if (specialists.getId() == null || specialists.getId() <= 0) {
            throw new IllegalArgumentException("Не вказано ID фахівця для оновлення");
        }

        dao.update(specialists);
        LOG.info("Оновлено дані працівника");

    }

    public void delete (Specialists specialists) throws SQLException {
        if (specialists == null || specialists.getId() == null || specialists.getId() <= 0) {
            throw new IllegalArgumentException("Не вказано ID заявки для видалення");
        }

        dao.delete(specialists.getId());
        LOG.info("Видалено працівника");

    }

    static void validate(Specialists specialist) {
        if (specialist == null) {
            throw new IllegalArgumentException("Дані працівника відсутні");
        }

        if (specialist.getName() == null || specialist.getName().isBlank()) {
            throw new IllegalArgumentException("У працівника має бути своє ім'я!");
        }

        if (specialist.getSurname() == null || specialist.getSurname().isBlank()) {
            throw new IllegalArgumentException("У працівника має бути своє прізвище!");
        }

        if (specialist.getPost() == null || specialist.getPost().isBlank()) {
            throw new IllegalArgumentException("У працівника має бути своя посада!");
        }
    }

    private static final Logger LOG =
            Logger.getLogger(SpecialistsService.class.getName());
}
