package ua.edu.nupp.kpp.client;

import javafx.concurrent.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ImportRoomsTask extends Task<Integer> {

    private static final Logger LOG =
            Logger.getLogger(ImportRoomsTask.class.getName());

    private final Path path;
    private final HttpHotelClient api;

    public ImportRoomsTask(Path path, HttpHotelClient api) {
        this.path = path;
        this.api = api;
    }

    @Override
    protected Integer call() throws Exception {
        updateMessage("Читання CSV...");
        updateProgress(0, 1);

        try {
            return importRooms();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Помилка імпорту кімнат", e);
            throw e;
        }
    }

    private int importRooms() throws IOException {
        List<String> lines = Files.readAllLines(
                path,
                StandardCharsets.UTF_8
        );

        if (lines.isEmpty()) {
            throw new IllegalArgumentException("CSV-файл порожній");
        }

        String header = lines.get(0);

        if (header.startsWith("\uFEFF")) {
            header = header.substring(1);
        }

        if (!header.trim().equals("r_number;price;status")) {
            throw new IllegalArgumentException(
                    "Очікується заголовок: r_number;price;status"
            );
        }

        long total = lines.stream()
                .skip(1)
                .filter(line -> !line.isBlank())
                .count();

        if (total == 0) {
            updateProgress(1, 1);
            updateMessage("CSV не містить кімнат");
            return 0;
        }

        int imported = 0;
        LOG.info("Початок імпорту кімнат із " + path);

        for (int i = 1; i < lines.size(); i++) {
            if (isCancelled()) {
                LOG.info("Імпорт скасовано. Підтверджено: " + imported);
                return imported;
            }

            String line = lines.get(i);

            if (line.isBlank()) {
                continue;
            }

            int lineNumber = i + 1;
            Room room;

            try {
                room = parseRoom(line);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(
                        "Рядок " + lineNumber + ": " + e.getMessage()
                                + ". Уже імпортовано: " + imported,
                        e
                );
            }

            updateMessage("Обробка рядка " + lineNumber + "...");

            try {
                api.addRoom(room);
            } catch (RuntimeException e) {
                throw new IOException(
                        "Помилка HTTP у рядку " + lineNumber
                                + ". Підтверджено записів: " + imported
                                + ". Подробиці — у журналі",
                        e
                );
            }

            imported++;

            updateProgress(imported, total);
            updateMessage("Імпортовано " + imported + " із " + total);

            LOG.info("Імпортовано кімнату №" + room.getRoomNumber());
        }

        LOG.info("Імпорт завершено. Додано кімнат: " + imported);
        return imported;
    }

    private Room parseRoom(String line) {
        String[] values = line.split(";", -1);

        if (values.length != 3) {
            throw new IllegalArgumentException(
                    "повинно бути рівно 3 стовпці"
            );
        }

        try {
            return new Room(
                    null,
                    Long.parseLong(values[0].trim()),
                    Long.parseLong(values[1].trim()),
                    Long.parseLong(values[2].trim())
            );
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "номер кімнати, ціна та статус мають бути цілими числами",
                    e
            );
        }
    }
}