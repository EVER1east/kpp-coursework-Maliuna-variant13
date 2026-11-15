package ua.edu.nupp.kpp.server;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class DaoCheck {
    public static void main(String[] args) throws SQLException {

        double result = RiskCalculator.calculate(80, 60, 40);
        System.out.printf("Показник пріоритету: %.2f%n", result);
    }
}