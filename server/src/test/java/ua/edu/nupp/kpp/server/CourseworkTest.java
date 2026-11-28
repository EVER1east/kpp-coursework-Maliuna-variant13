package ua.edu.nupp.kpp.server;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CourseworkTest {
    private static final double EPSILON = 0.000001;

    @Test
    @DisplayName("Нульові фактори дають показник 0")
    void zeroFactorsGiveZero() {
        assertEquals(0.0, RiskCalculator.calculate(0, 0, 0), EPSILON);
    }

    @Test
    @DisplayName("Максимальні фактори дають показник 100")
    void maximumFactorsGiveHundred() {
        assertEquals(100.0, RiskCalculator.calculate(100, 100, 100), EPSILON);
    }

    @Test
    @DisplayName("Фактори 80, 60, 40 дають показник 63")
    void weightedFactorsGiveExpectedResult() {
        assertEquals(63.0, RiskCalculator.calculate(80, 60, 40), EPSILON);
    }

    @Test
    @DisplayName("Від'ємне значення кожного фактора відхиляється")
    void negativeFactorsAreRejected() {
        assertAll(
            () -> assertThrows(IllegalArgumentException.class,
                () -> RiskCalculator.calculate(-1, 50, 50)),
            () -> assertThrows(IllegalArgumentException.class,
                () -> RiskCalculator.calculate(50, -1, 50)),
            () -> assertThrows(IllegalArgumentException.class,
                () -> RiskCalculator.calculate(50, 50, -1))
        );
    }

    @Test
    @DisplayName("Значення понад 100 у кожній позиції відхиляється")
    void factorsAboveHundredAreRejected() {
        assertAll(
            () -> assertThrows(IllegalArgumentException.class,
                () -> RiskCalculator.calculate(101, 50, 50)),
            () -> assertThrows(IllegalArgumentException.class,
                () -> RiskCalculator.calculate(50, 101, 50)),
            () -> assertThrows(IllegalArgumentException.class,
                () -> RiskCalculator.calculate(50, 50, 101))
        );
    }

    @Test
    @DisplayName("NaN та нескінченності відхиляються для всіх факторів")
    void nonFiniteFactorsAreRejected() {
        double[] invalid = {
            Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY
        };
        for (double value : invalid) {
            assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                    () -> RiskCalculator.calculate(value, 50, 50)),
                () -> assertThrows(IllegalArgumentException.class,
                    () -> RiskCalculator.calculate(50, value, 50)),
                () -> assertThrows(IllegalArgumentException.class,
                    () -> RiskCalculator.calculate(50, 50, value))
            );
        }
    }

    @Test
    @DisplayName("Послідовні переходи життєвого циклу дозволені")
    void forwardTransitionsAreAllowed() {
        assertAll(
            () -> assertTrue(RequestsService.isAllowedTransition(
                RequestState.NEW, RequestState.ASSIGNED)),
            () -> assertTrue(RequestsService.isAllowedTransition(
                RequestState.ASSIGNED, RequestState.IN_PROGRESS)),
            () -> assertTrue(RequestsService.isAllowedTransition(
                RequestState.IN_PROGRESS, RequestState.COMPLETED))
        );
    }

    @Test
    @DisplayName("Пропуск етапів заявки заборонений")
    void skippingStatesIsRejected() {
        assertAll(
            () -> assertFalse(RequestsService.isAllowedTransition(
                RequestState.NEW, RequestState.IN_PROGRESS)),
            () -> assertFalse(RequestsService.isAllowedTransition(
                RequestState.NEW, RequestState.COMPLETED)),
            () -> assertFalse(RequestsService.isAllowedTransition(
                RequestState.ASSIGNED, RequestState.COMPLETED))
        );
    }

    @Test
    @DisplayName("Повернення заявки до попередніх станів заборонене")
    void backwardTransitionsAreRejected() {
        assertAll(
            () -> assertFalse(RequestsService.isAllowedTransition(
                RequestState.ASSIGNED, RequestState.NEW)),
            () -> assertFalse(RequestsService.isAllowedTransition(
                RequestState.IN_PROGRESS, RequestState.ASSIGNED)),
            () -> assertFalse(RequestsService.isAllowedTransition(
                RequestState.COMPLETED, RequestState.IN_PROGRESS))
        );
    }

    @Test
    @DisplayName("Повторне встановлення поточного стану заборонене")
    void transitionsToSameStateAreRejected() {
        for (RequestState state : RequestState.values()) {
            assertFalse(RequestsService.isAllowedTransition(state, state));
        }
    }

    @Test
    @DisplayName("Номер із нульовою ціною проходить валідацію")
    void zeroRoomPriceIsAllowed() {
        Rooms room = new Rooms(null, 101L, 0L, 0L);
        assertDoesNotThrow(() -> RoomsService.validate(room));
    }

    @Test
    @DisplayName("Від'ємна ціна номера відхиляється")
    void negativeRoomPriceIsRejected() {
        Rooms room = new Rooms(null, 101L, -1L, 0L);
        assertThrows(IllegalArgumentException.class,
            () -> RoomsService.validate(room));
    }

    @Test
    @DisplayName("Заповнені дані фахівця проходять валідацію")
    void validSpecialistIsAccepted() {
        Specialists specialist = new Specialists(
            null, "Віталій", "Малюна", "Електрик");
        assertDoesNotThrow(() -> SpecialistsService.validate(specialist));
    }

    @Test
    @DisplayName("Прізвище фахівця з пробілів відхиляється")
    void blankSpecialistSurnameIsRejected() {
        Specialists specialist = new Specialists(
            null, "Віталій", "   ", "Електрик");
        assertThrows(IllegalArgumentException.class,
            () -> SpecialistsService.validate(specialist));
    }

    @Test
    @DisplayName("Коректна заявка може не мати призначеного фахівця")
    void requestWithoutSpecialistIsAccepted() {
        Requests request = validRequest();
        assertDoesNotThrow(() -> RequestsService.validate(request));
    }

    @Test
    @DisplayName("Заявка без опису проблеми відхиляється")
    void blankRequestDescriptionIsRejected() {
        Requests request = validRequest();
        request.setDescription("   ");
        assertThrows(IllegalArgumentException.class,
            () -> RequestsService.validate(request));
    }

    @Test
    @DisplayName("Назва обладнання до 255 символів дозволена, 256 відхиляється")
    void equipmentNameLengthBoundaryIsEnforced() {
        Equipment equipment = new Equipment(null, 1L, "A".repeat(255));
        assertDoesNotThrow(() -> EquipmentService.validate(equipment));
        equipment.setName("A".repeat(256));
        assertThrows(IllegalArgumentException.class,
            () -> EquipmentService.validate(equipment));
    }

    @Test
    @DisplayName("Порожня назва обладнання відхиляється")
    void blankEquipmentNameIsRejected() {
        Equipment equipment = new Equipment(null, 1L, "   ");
        assertThrows(IllegalArgumentException.class,
            () -> EquipmentService.validate(equipment));
    }

    @Test
    @DisplayName("Нульовий ID кімнати в обладнанні відхиляється")
    void zeroEquipmentRoomIdIsRejected() {
        Equipment equipment = new Equipment(null, 0L, "Телевізор");
        assertThrows(IllegalArgumentException.class,
            () -> EquipmentService.validate(equipment));
    }

    @Test
    @DisplayName("Запис виконання без опису роботи відхиляється")
    void blankExecutionDescriptionIsRejected() {
        ExecutionRecords record = new ExecutionRecords(null, 1L, "   ");
        assertThrows(IllegalArgumentException.class,
            () -> ExecutionRecordsService.validate(record));
    }

    private Requests validRequest() {
        // roomNumber у серверній моделі означає ID кімнати.
        // Тут перевіряється тільки валідація значень, без доступу до MySQL.
        return new Requests(null, 1L, null, "Не працює кондиціонер",
            RequestState.NEW, 80, 60, 40);
    }
}
