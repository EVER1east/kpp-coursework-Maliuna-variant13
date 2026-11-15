package ua.edu.nupp.kpp.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class RequestsDAO {

    private static final String SELECT_REQUESTS = """
            SELECT id, room_id, specialist_id, description, status,
                   guest_impact, fault_severity, room_availablity_impact
            FROM requests
            """;

    public boolean updateState(
            long id,
            RequestState previous,
            RequestState next
    ) throws SQLException {
        String sql = """
            UPDATE requests
            SET status = ?
            WHERE id = ? AND status = ?
            """;

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, next.name());
            statement.setLong(2, id);
            statement.setString(3, previous.name());

            return statement.executeUpdate() == 1;
        }
    }


    public void add(Requests request) throws SQLException {
        String sql = """
                INSERT INTO requests (
                    room_id, specialist_id, description, status,
                    guest_impact, fault_severity, room_availablity_impact
                )
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            bind(statement, request);
            statement.executeUpdate();
        }
    }

    public List<Requests> getAll() throws SQLException {
        String sql = SELECT_REQUESTS + " ORDER BY id";
        List<Requests> requests = new ArrayList<>();

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                requests.add(map(result));
            }
        }

        return requests;
    }

    public Requests getById(long id) throws SQLException {
        String sql = SELECT_REQUESTS + " WHERE id = ?";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return map(result);
                }
                return null;
            }
        }
    }

    public void update(Requests request) throws SQLException {
        String sql = """
            UPDATE requests
            SET room_id = ?, specialist_id = ?, description = ?,
                guest_impact = ?, fault_severity = ?,
                room_availablity_impact = ?
            WHERE id = ?
            """;

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, request.getRoomNumber());
            statement.setObject(2, request.getSpecialistId(), Types.BIGINT);
            statement.setString(3, request.getDescription());
            statement.setDouble(4, request.getF1());
            statement.setDouble(5, request.getF2());
            statement.setDouble(6, request.getF3());
            statement.setLong(7, request.getId());

            if (statement.executeUpdate() == 0) {
                throw new SQLException("Заявку не знайдено або дані не змінено");
            }
        }
    }

    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM requests WHERE id = ?";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        }
    }

    private void bind(PreparedStatement statement, Requests request)
            throws SQLException {
        statement.setLong(1, request.getRoomNumber());
        statement.setObject(2, request.getSpecialistId(), Types.BIGINT);
        statement.setString(3, request.getDescription());
        statement.setString(4, request.getState().name());
        statement.setDouble(5, request.getF1());
        statement.setDouble(6, request.getF2());
        statement.setDouble(7, request.getF3());
    }

    private Requests map(ResultSet result) throws SQLException {
        return new Requests(
                result.getLong("id"),
                result.getLong("room_id"),
                result.getObject("specialist_id", Long.class),
                result.getString("description"),
                RequestState.valueOf(result.getString("status")),
                result.getDouble("guest_impact"),
                result.getDouble("fault_severity"),
                result.getDouble("room_availablity_impact")
        );
    }
}