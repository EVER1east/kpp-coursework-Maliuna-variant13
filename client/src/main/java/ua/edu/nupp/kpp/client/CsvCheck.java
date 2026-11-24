package ua.edu.nupp.kpp.client;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class CsvCheck {

    public static void main(String[] args) throws IOException {
        Path path = Path.of("sample-data", "rooms.csv");

        List<String> lines = Files.readAllLines(
                path,
                StandardCharsets.UTF_8
        );

        if (lines.size() < 2) {
            throw new IllegalArgumentException(
                    "CSV має містити заголовок і хоча б один рядок даних"
            );
        }

        String line = lines.get(1);
        String[] values = line.split(";", -1);

        if (values.length != 3) {
            throw new IllegalArgumentException(
                    "У рядку CSV повинно бути 3 стовпці"
            );
        }

        Room room;

        try {
            room = new Room(
                    null,
                    Long.parseLong(values[0].trim()),
                    Long.parseLong(values[1].trim()),
                    Long.parseLong(values[2].trim())
            );
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Номер кімнати, ціна та статус мають бути цілими числами",
                    e
            );
        }

        HttpHotelClient api = new HttpHotelClient();
        api.addRoom(room);

        System.out.println(
                "Імпортовано кімнату №" + room.getRoomNumber()
        );
    }
}