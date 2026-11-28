package ua.edu.nupp.kpp.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EquipmentDAO {

    public void add(Equipment equipment) throws SQLException {
        String sql = "INSERT INTO equipment (room_id, name) VALUES (?, ?)";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, equipment.getRoomId());
            statement.setString(2, equipment.getName());
            statement.executeUpdate();
        }
    }

    public List<Equipment> getAll() throws SQLException {
        String sql = SELECT_EQUIPMENT + " ORDER BY er.id";
        List<Equipment> equipment = new ArrayList<>();

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                equipment.add(map(result));
            }
        }

        return equipment;
    }

    public Equipment getById(long id) throws SQLException {
        String sql = SELECT_EQUIPMENT + " WHERE er.id = ?";

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

    private static final String SELECT_EQUIPMENT = """
        SELECT er.id, er.room_id, er.name
        FROM equipment er
        JOIN rooms r ON r.id = er.room_id
        """;

    public void update(Equipment equipment) throws SQLException {
        String sql = "UPDATE equipment SET name = ? WHERE id = ?";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, equipment.getName());
            statement.setLong(2, equipment.getId());
            statement.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM equipment WHERE id = ?";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        }
    }

    private Equipment map(ResultSet result) throws SQLException {
        Equipment equipment = new Equipment(
                result.getLong("id"),
                result.getLong("room_id"),
                result.getString("name")
        );
        return equipment;
    }
}