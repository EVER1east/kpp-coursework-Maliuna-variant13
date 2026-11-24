package ua.edu.nupp.kpp.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class HttpHotelClient {
    private static final String BASE = "http://localhost:8080/server-1.0-SNAPSHOT";
    private final HttpClient http=HttpClient.newHttpClient();
    private final ObjectMapper mapper=new ObjectMapper();

    public List<Room> getRooms() {
        return Arrays.asList(
                read(get("/rooms"), Room[].class)
        );
    }

    public void addRoom(Room room) {
        post("/rooms",
                "action=add"
                        + "&roomNumber=" + room.getRoomNumber()
                        + "&price=" + room.getPrice()
                        + "&status=" + room.getStatus());
    }

    public void updateRoom(Room room) {
        post("/rooms",
                "action=update"
                        + "&id=" + room.getId()
                        + "&roomNumber=" + room.getRoomNumber()
                        + "&price=" + room.getPrice()
                        + "&status=" + room.getStatus());
    }

    public void deleteRoom(long id) {
        post("/rooms", "action=delete&id=" + id);
    }

    public List<Specialist> getSpecialists() {
        return Arrays.asList(
                read(get("/specialists"), Specialist[].class)
        );
    }

    public void addSpecialist(Specialist specialist) {
        post("/specialists",
                "action=add"
                        + "&name=" + enc(specialist.getName())
                        + "&surname=" + enc(specialist.getSurname())
                        + "&post=" + enc(specialist.getPost())
        );
    }

    public void updateSpecialist(Specialist specialist) {
        post("/specialists",
                "action=update"
                        + "&id=" + specialist.getId()
                        + "&name=" + enc(specialist.getName())
                        + "&surname=" + enc(specialist.getSurname())
                        + "&post=" + enc(specialist.getPost())
        );
    }

    public void deleteSpecialist(long id) {
        post("/specialists", "action=delete&id=" + id);
    }

    private String requestForm(String action, Request request) {
        String form = "action=" + action
                + "&roomNumber=" + request.getRoomNumber()
                + "&description=" + enc(request.getDescription())
                + "&f1=" + factor(request.getF1())
                + "&f2=" + factor(request.getF2())
                + "&f3=" + factor(request.getF3());

        if (request.getSpecialistId() != null) {
            form += "&specialistId=" + request.getSpecialistId();
        }

        if ("update".equals(action)) {
            form += "&id=" + request.getId();
        }

        return form;
    }

    public List<Request> getRequests() {
        return Arrays.asList(read(get("/requests"), Request[].class));
    }

    public void addRequest(Request request) {
        post("/requests", requestForm("add", request));
    }

    public void updateRequest(Request request) {
        post("/requests", requestForm("update", request));
    }

    public void deleteRequest(long id) {
        post("/requests", "action=delete&id=" + id);
    }

    public void changeState(long id, RequestState state) {
        if (state == null) {
            throw new IllegalArgumentException("Вкажіть новий стан");
        }

        post("/requests",
                "action=changeState&id=" + id + "&state=" + state.name());
    }

    public List<ExecutionRecord> getExecutionRecords() {
        return Arrays.asList(
                read(get("/executionrecords"), ExecutionRecord[].class)
        );
    }

    public void addExecutionRecord(ExecutionRecord record) {
        post("/executionrecords", executionRecordForm("add", record));
    }

    public void updateExecutionRecord(ExecutionRecord record) {
        post("/executionrecords", executionRecordForm("update", record));
    }

    public void deleteExecutionRecord(long id) {
        post("/executionrecords", "action=delete&id=" + id);
    }

    private String factor(double value) {
        if (!Double.isFinite(value)
                || value < 0 || value > 100
                || value != Math.rint(value)) {
            throw new IllegalArgumentException(
                    "Фактори мають бути цілими числами від 0 до 100"
            );
        }

        return Long.toString((long) value);
    }

    private String executionRecordForm(String action, ExecutionRecord record) {
        String form = "action=" + action
                + "&workDone=" + enc(record.getWorkDone());

        if ("add".equals(action)) {
            form += "&requestNumber=" + record.getRequestNumber();
        }

        if ("update".equals(action)) {
            form += "&id=" + record.getId();
        }

        return form;
    }
    private String get(String path) {
        HttpRequest req=HttpRequest.newBuilder(URI.create(BASE+path)).GET().build(); return send(req);
    }
    private String post(String path,String form) {
        HttpRequest req=HttpRequest.newBuilder(URI.create(BASE+path))
                .header("Content-Type","application/x-www-form-urlencoded; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(form)).build(); return send(req);
    }
    private String send(HttpRequest req) {
        try {
            HttpResponse<String> r=http.send(req,HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if(r.statusCode()>=400) throw new RuntimeException(r.body());
            return r.body();
        } catch(Exception e) { throw new RuntimeException("Помилка HTTP: "+e.getMessage(),e); }
    }
    private <T> T read(String json,Class<T> type) {
        try { return mapper.readValue(json,type); } catch(Exception e) { throw new RuntimeException("Помилка JSON",e); }
    }
    private String enc(String s) { return URLEncoder.encode(s==null?"":s,StandardCharsets.UTF_8); }
}
