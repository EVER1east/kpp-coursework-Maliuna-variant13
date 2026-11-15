package ua.edu.nupp.kpp.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoomsDAO {

    public void add(Rooms room) throws SQLException {
        String sql = "INSERT INTO rooms (r_number, price, status) VALUES (?, ?, ?)";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, room.getRoomNumber());
            statement.setLong(2, room.getPrice());
            statement.setLong(3, room.getStatus());
            statement.executeUpdate();
        }
    }

    public List<Rooms> getAll() throws SQLException {
        String sql = "SELECT id, r_number, price, status FROM rooms ORDER BY id";
        List<Rooms> rooms = new ArrayList<>();

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                rooms.add(map(result));
            }
        }

        return rooms;
    }

    public Rooms getById(long id) throws SQLException {
        String sql = "SELECT id, r_number, price, status FROM rooms WHERE id = ?";

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

    public void update(Rooms room) throws SQLException {
        String sql = "UPDATE rooms SET r_number = ?, price = ?, status = ? WHERE id = ?";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, room.getRoomNumber());
            statement.setLong(2, room.getPrice());
            statement.setLong(3, room.getStatus());
            statement.setLong(4, room.getId());
            statement.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM rooms WHERE id = ?";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        }
    }

    private Rooms map(ResultSet result) throws SQLException {
        return new Rooms(
                result.getLong("id"),
                result.getLong("r_number"),
                result.getLong("price"),
                result.getLong("status")
        );
    }
}