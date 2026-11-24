package org.example;

import ua.edu.nupp.kpp.client.HttpHotelClient;
import ua.edu.nupp.kpp.client.Room;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        HttpHotelClient client = new HttpHotelClient();

        for (Room room : client.getRooms()) {
            System.out.println(
                    "Номер: " + room.getRoomNumber() + ", ціна: " + room.getPrice() + " гривень"
            );
        }
    }
}