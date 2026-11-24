package ua.edu.nupp.kpp.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DBUtil {

    private static final String URL =
            "jdbc:mysql://localhost:3306/hotel";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private DBUtil() {
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Драйвер MySQL відсутній у застосунку", e);
        }

        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    /*public static void main(String[] args) {
        try (Connection connection = getConnection()) {
            System.out.println("Підключення до MySQL успішне.");
        } catch (SQLException e) {
            System.out.println("Помилка підключення до MySQL:");
            System.out.println(e.getMessage());
        }
    }*/

}
