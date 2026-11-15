package ua.edu.nupp.kpp.server;

import com.sun.net.httpserver.Request;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;


public class ExecutionRecordsService {

    private final ExecutionRecordsDAO dao = new ExecutionRecordsDAO();
    private final RequestsDAO requestsDAO = new RequestsDAO();

    public List<ExecutionRecords> getAll() throws SQLException {
        return dao.getAll();
    }

    public ExecutionRecords getById(long id) throws SQLException {
        return dao.getById(id);
    }

    public void add(ExecutionRecords executionRecords) throws SQLException {
        validate(executionRecords);
        requireRequest(executionRecords);
        dao.add(executionRecords);
        LOG.info("Додано новий звіт");

    }

    public void update(ExecutionRecords executionRecords) throws SQLException {
        validate(executionRecords);

        if (executionRecords.getId() == null || executionRecords.getId() <= 0) {
            throw new IllegalArgumentException("Не вказано ID звіту для оновлення");
        }

        requireRequest(executionRecords);
        dao.update(executionRecords);
        LOG.info("Оновлено звіт");

    }

    public void delete (ExecutionRecords executionRecords) throws SQLException {
        if (executionRecords == null || executionRecords.getId() == null || executionRecords.getId() <= 0) {
            throw new IllegalArgumentException("Не вказано ID заявки для видалення");
        }

        dao.delete(executionRecords.getId());
        LOG.info("Видалено звіт");

    }

    static void validate(ExecutionRecords executionRecord) {
        if (executionRecord == null) {
            throw new IllegalArgumentException("Дані звіту відсутні");
        }

        if (executionRecord.getRequestNumber() <= 0) {
            throw new IllegalArgumentException("Має існувати відповідний запит");
        }

        if (executionRecord.getWorkDone() == null || executionRecord.getWorkDone().isBlank()) {
            throw new IllegalArgumentException("Робота ще не виконана");
        }
    }

    private void requireRequest(ExecutionRecords executionRecord) throws SQLException {
        if (requestsDAO.getById(executionRecord.getRequestNumber()) == null) {
            throw new IllegalArgumentException("Заявку не знайдено");
        }
    }

    private static final Logger LOG =
            Logger.getLogger(ExecutionRecordsService.class.getName());
}
