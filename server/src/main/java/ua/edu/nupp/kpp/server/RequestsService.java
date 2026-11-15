package ua.edu.nupp.kpp.server;

import com.sun.net.httpserver.Request;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;


public class RequestsService {

    private final RequestsDAO dao = new RequestsDAO();
    private final SpecialistsDAO specdao = new SpecialistsDAO();
    private final RoomsDAO roomsDAO = new RoomsDAO();

    public List<Requests> getAll() throws SQLException {
        return dao.getAll();
    }

    public Requests getById(long id) throws SQLException {
        return dao.getById(id);
    }

    public void add(Requests request) throws SQLException {
        if (request == null) {
            throw new IllegalArgumentException("Дані заявки відсутні");
        }

        request.setState(RequestState.NEW);

        validate(request);
        requireRelatedRecords(request);
        dao.add(request);

        LOG.info("Додано нову заявку");
    }

    public void update(Requests requests) throws SQLException {
        validate(requests);

        if (requests.getId() == null || requests.getId() <= 0) {
            throw new IllegalArgumentException("Не вказано ID заявки для оновлення");
        }

        requireRelatedRecords(requests);
        dao.update(requests);
        LOG.info("Запис оновлено");

    }

    public void delete (Requests requests) throws SQLException {
        if (requests == null || requests.getId() == null || requests.getId() <= 0) {
            throw new IllegalArgumentException("Не вказано ID заявки для видалення");
        }

        dao.delete(requests.getId());
        LOG.info("Запис видалено");

    }

    static void validate(Requests request) {
        if (request == null) {
            throw new IllegalArgumentException("Дані запиту відсутні");
        }


        if (request.getDescription() == null || request.getDescription().isBlank()) {
            throw new IllegalArgumentException("Запит має пояснювати проблему");
        }

        if (request.getRoomNumber() <= 0) {
            throw new IllegalArgumentException("Номер має існувати");
        }

        if (request.getSpecialistId() != null && request.getSpecialistId() <= 0) {
            throw new IllegalArgumentException("Працівник має існувати");
        }

        if (request.getState() == null) {
            throw new IllegalArgumentException("Заява повинна мати статус");
        }

        validateFactor(request.getF1());
        validateFactor(request.getF2());
        validateFactor(request.getF3());
    }

    private static void validateFactor(double value) {
        if (!Double.isFinite(value) || value < 0 || value > 100) {
            throw new IllegalArgumentException("Фактори мають бути числами від 0 до 100");
        }
    }

    private void requireRelatedRecords(Requests request) throws SQLException {
        if (roomsDAO.getById(request.getRoomNumber()) == null) {
            throw new IllegalArgumentException("Номер готелю не знайдено");
        }

        if (request.getSpecialistId() != null
                && specdao.getById(request.getSpecialistId()) == null) {
            throw new IllegalArgumentException("Фахівця не знайдено");
        }
    }

    public double calculateRisk(long id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("ID заявки має бути додатним");
        }

        Requests request = dao.getById(id);

        if (request == null) {
            throw new IllegalArgumentException("Заявку не знайдено");
        }

        return RiskCalculator.calculate(
                request.getF1(),
                request.getF2(),
                request.getF3()
        );
    }

    public void changeState(long id, RequestState next) throws SQLException {
        if (id <= 0 || next == null) {
            throw new IllegalArgumentException("Вкажіть ID заявки та новий стан");
        }

        Requests current = dao.getById(id);

        if (current == null) {
            throw new IllegalArgumentException("Заявку не знайдено");
        }

        RequestState previous = current.getState();

        if (!isAllowedTransition(previous, next)) {
            throw new IllegalArgumentException(
                    "Недопустимий перехід: " + previous + " → " + next
            );
        }

        if (!dao.updateState(id, previous, next)) {
            throw new IllegalStateException(
                    "Заявку вже змінено або видалено. Оновіть дані"
            );
        }

        LOG.info("Стан заявки " + id + ": " + previous + " → " + next);
    }

    public static boolean isAllowedTransition(RequestState from, RequestState to) {
        return (from==RequestState.NEW && to==RequestState.ASSIGNED)
                || (from==RequestState.ASSIGNED && to==RequestState.IN_PROGRESS)
                || (from==RequestState.IN_PROGRESS && to==RequestState.COMPLETED);
    }

    private static final Logger LOG =
            Logger.getLogger(RequestsService.class.getName());
}
