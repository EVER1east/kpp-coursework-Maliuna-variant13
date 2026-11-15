package ua.edu.nupp.kpp.server;

public final class RiskCalculator {

    private RiskCalculator() {
    }

    public static double calculate(double f1, double f2, double f3) {
        validateFactor(f1);
        validateFactor(f2);
        validateFactor(f3);

        return 0.40 * f1 + 0.35 * f2 + 0.25 * f3;
    }

    private static void validateFactor(double value) {
        if (!Double.isFinite(value) || value < 0 || value > 100) {
            throw new IllegalArgumentException(
                    "Фактори мають бути числами від 0 до 100"
            );
        }
    }

    double result = RiskCalculator.calculate(80, 60, 40);
}