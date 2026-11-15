package ua.edu.nupp.kpp.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SpecialistsDAO {

    public void add(Specialists specialist) throws SQLException {
        String sql = "INSERT INTO specialists (name, surname, post) VALUES (?, ?, ?)";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, specialist.getName());
            statement.setString(2, specialist.getSurname());
            statement.setString(3, specialist.getPost());
            statement.executeUpdate();
        }
    }

    public List<Specialists> getAll() throws SQLException {
        String sql = "SELECT id, name, surname, post FROM specialists ORDER BY id";
        List<Specialists> specialists = new ArrayList<>();

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                specialists.add(map(result));
            }
        }

        return specialists;
    }

    public Specialists getById(long id) throws SQLException {
        String sql = "SELECT id, name, surname, post FROM specialists WHERE id = ?";

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

    public void update(Specialists specialist) throws SQLException {
        String sql = "UPDATE specialists SET name = ?, surname = ?, post = ? WHERE id = ?";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, specialist.getName());
            statement.setString(2, specialist.getSurname());
            statement.setString(3, specialist.getPost());
            statement.setLong(4, specialist.getId());
            statement.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM specialists WHERE id = ?";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        }
    }

    private Specialists map(ResultSet result) throws SQLException {
        return new Specialists(
                result.getLong("id"),
                result.getString("name"),
                result.getString("surname"),
                result.getString("post")
        );
    }
}