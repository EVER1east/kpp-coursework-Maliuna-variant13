package ua.edu.nupp.kpp.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ExecutionRecordsDAO {

    public void add(ExecutionRecords executionRecord) throws SQLException {
        String sql = "INSERT INTO execution_records (request_id, work_done) VALUES (?, ?)";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, executionRecord.getRequestNumber());
            statement.setString(2, executionRecord.getWorkDone());
            statement.executeUpdate();
        }
    }

    public List<ExecutionRecords> getAll() throws SQLException {
        String sql = "SELECT id, request_id, work_done FROM execution_records ORDER BY id";
        List<ExecutionRecords> executionRecord = new ArrayList<>();

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                executionRecord.add(map(result));
            }
        }

        return executionRecord;
    }

    public ExecutionRecords getById(long id) throws SQLException {
        String sql = "SELECT id, request_id, work_done FROM execution_records WHERE id = ?";

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

    public void update(ExecutionRecords executionRecord) throws SQLException {
        String sql = "UPDATE execution_records SET work_done = ? WHERE id = ?";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, executionRecord.getWorkDone());
            statement.setLong(2, executionRecord.getId());
            statement.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM execution_records WHERE id = ?";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        }
    }

    private ExecutionRecords map(ResultSet result) throws SQLException {
        return new ExecutionRecords(
                result.getLong("id"),
                result.getLong("request_id"),
                result.getString("work_done")
        );
    }
}