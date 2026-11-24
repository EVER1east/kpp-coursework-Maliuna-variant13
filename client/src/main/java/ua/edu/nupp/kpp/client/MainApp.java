package ua.edu.nupp.kpp.client;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.logging.Logger;

public class MainApp extends Application {
    private final HttpHotelClient api= new HttpHotelClient();
    @Override
    public void start(Stage stage) throws Exception {
        TabPane tabs=new TabPane(
                new Tab("Номери", roomPane()),
                new Tab("Фахівці",specialistPane()),
                new Tab("Заявки",requestsPane()),
                new Tab("Звіти про виконану роботу",executionRecordsPane())
        );
        tabs.getTabs().forEach(t->t.setClosable(false));
        stage.setScene(new Scene(tabs,1050,650)); stage.setTitle("Технічні заявки готельного комплексу"); stage.show();
        refreshAll();
    }
    private final TableView<Room> rooms = new TableView<>();
    private final TableView<Specialist> specialists = new TableView<>();
    private final TableView<Request> requests = new TableView<>();
    private final TableView<ExecutionRecord> executionRecords = new TableView<>();
    private Pane roomPane() {
        rooms.getColumns().setAll(
                col("ID", "id"),
                col("Номер кімнати", "roomNumber"),
                col("Ціна", "price"),
                col("Статус", "status")
        );

        TextField roomNumber = new TextField();
        TextField price = new TextField();
        TextField status = new TextField();

        Button add = new Button("Додати");
        Button update = new Button("Змінити");
        Button delete = new Button("Видалити");
        Button refresh = new Button("Оновити");

        add.setOnAction(e -> safe(() -> {
            Room room = new Room(
                    null,
                    number(roomNumber, "Номер кімнати"),
                    number(price, "Ціна"),
                    number(status, "Статус")
            );

            api.addRoom(room);
            refreshRooms();
        }));

        update.setOnAction(e -> safe(() -> {
            Room current = selected(rooms);

            Room room = new Room(
                    current.getId(),
                    number(roomNumber, "Номер кімнати"),
                    number(price, "Ціна"),
                    number(status, "Статус")
            );

            api.updateRoom(room);
            refreshRooms();
        }));

        delete.setOnAction(e -> safe(() -> {
            Room current = selected(rooms);
            api.deleteRoom(current.getId());
            refreshRooms();
        }));

        refresh.setOnAction(e -> refreshRooms());

        rooms.getSelectionModel().selectedItemProperty()
                .addListener((observable, previous, room) -> {
                    if (room != null) {
                        roomNumber.setText(String.valueOf(room.getRoomNumber()));
                        price.setText(String.valueOf(room.getPrice()));
                        status.setText(String.valueOf(room.getStatus()));
                    }
                });

        VBox form = new VBox(
                8,
                new Label("Номер кімнати"), roomNumber,
                new Label("Ціна"), price,
                new Label("Статус"), status,
                new HBox(8, add, update, delete, refresh)
        );

        return layout(rooms, form);
    }

    private Pane specialistPane() {
        specialists.getColumns().setAll(
                col("ID", "id"),
                col("Ім'я працівника", "name"),
                col("Прізвище", "surname"),
                col("Посада", "post")
        );

        TextField name = new TextField();
        TextField surname = new TextField();
        TextField post = new TextField();

        Button add = new Button("Додати");
        Button update = new Button("Змінити");
        Button delete = new Button("Видалити");
        Button refresh = new Button("Оновити");

        add.setOnAction(e -> safe(() -> {
            Specialist specialist = new Specialist(
                    null,
                    text(name, "Ім'я"),
                    text(surname, "Прізвище"),
                    text(post, "Статус")
            );

            api.addSpecialist(specialist);
            refreshSpecialists();
        }));

        update.setOnAction(e -> safe(() -> {
            Specialist current = selected(specialists);

            Specialist specialist = new Specialist(
                    current.getId(),
                    text(name, "Ім'я"),
                    text(surname, "Прізвище"),
                    text(post, "Статус")
            );

            api.updateSpecialist(specialist);
            refreshSpecialists();
        }));

        delete.setOnAction(e -> safe(() -> {
            Specialist current = selected(specialists);
            api.deleteSpecialist(current.getId());
            refreshSpecialists();
        }));

        refresh.setOnAction(e -> refreshSpecialists());

        specialists.getSelectionModel().selectedItemProperty()
                .addListener((observable, previous, specialist) -> {
                    if (specialist != null) {
                        name.setText(String.valueOf(specialist.getName()));
                        surname.setText(String.valueOf(specialist.getSurname()));
                        post.setText(String.valueOf(specialist.getPost()));
                    }
                });

        VBox form = new VBox(
                8,
                new Label("Ім'я спеціаліста"), name,
                new Label("Прізвище"), surname,
                new Label("Посада"), post,
                new HBox(8, add, update, delete, refresh)
        );

        return layout(specialists, form);
    }

    private Pane requestsPane() {
        requests.getColumns().setAll(
                col("ID", "id"),
                col("ID кімнати", "roomNumber"),
                col("ID фахівця", "specialistId"),
                col("Стан", "state"),
                col("Опис проблеми", "description"),
                col("F1", "f1"),
                col("F2", "f2"),
                col("F3", "f3"),
                col("Показник I", "riskIndex")
        );

        TextField roomNumber = new TextField();
        TextField specialistId = new TextField();
        TextField description = new TextField();
        TextField f1 = new TextField();
        TextField f2 = new TextField();
        TextField f3 = new TextField();

        ComboBox<RequestState> state = new ComboBox<>(
                FXCollections.observableArrayList(RequestState.values())
        );
        state.setValue(RequestState.NEW);

        Button add = new Button("Додати");
        Button update = new Button("Змінити");
        Button changeState = new Button("Змінити стан");
        Button delete = new Button("Видалити");
        Button refresh = new Button("Оновити");

        add.setOnAction(e -> safe(() -> {
            Request request = new Request(
                    null,
                    number(roomNumber, "ID кімнати"),
                    optionalNumber(specialistId, "ID фахівця"),
                    RequestState.NEW,
                    text(description, "Опис проблеми"),
                    number(f1, "F1"),
                    number(f2, "F2"),
                    number(f3, "F3")
            );

            api.addRequest(request);
            refreshRequests();
        }));

        update.setOnAction(e -> safe(() -> {
            Request current = selected(requests);

            Request request = new Request(
                    current.getId(),
                    number(roomNumber, "ID кімнати"),
                    optionalNumber(specialistId, "ID фахівця"),
                    current.getState(),
                    text(description, "Опис проблеми"),
                    number(f1, "F1"),
                    number(f2, "F2"),
                    number(f3, "F3")
            );

            api.updateRequest(request);
            refreshRequests();
        }));

        changeState.setOnAction(e -> safe(() -> {
            Request current = selected(requests);
            RequestState next = state.getValue();

            if (next == null) {
                throw new IllegalArgumentException("Виберіть новий стан");
            }

            api.changeState(current.getId(), next);
            refreshRequests();
        }));

        delete.setOnAction(e -> safe(() -> {
            api.deleteRequest(selected(requests).getId());
            refreshRequests();
        }));

        refresh.setOnAction(e -> refreshRequests());

        requests.getSelectionModel().selectedItemProperty()
                .addListener((observable, previous, request) -> {
                    if (request != null) {
                        roomNumber.setText(
                                String.valueOf(request.getRoomNumber())
                        );

                        specialistId.setText(
                                request.getSpecialistId() == null
                                        ? ""
                                        : request.getSpecialistId().toString()
                        );

                        description.setText(request.getDescription());
                        state.setValue(request.getState());

                        f1.setText(factorText(request.getF1()));
                        f2.setText(factorText(request.getF2()));
                        f3.setText(factorText(request.getF3()));
                    }
                });

        VBox form = new VBox(
                8,
                new Label("ID кімнати"), roomNumber,
                new Label("ID фахівця — можна залишити порожнім"), specialistId,
                new Label("Опис проблеми"), description,
                new Label("Вплив на гостя, 0–100"), f1,
                new Label("Тяжкість несправності, 0–100"), f2,
                new Label("Вплив на доступність номера, 0–100"), f3,
                new HBox(8, add, update),
                new Label("Стан для переходу"), state,
                changeState,
                new HBox(8, delete, refresh)
        );

        return layout(requests, form);
    }

    private Pane executionRecordsPane() {
        executionRecords.getColumns().setAll(
                col("ID", "id"),
                col("ID заявки", "requestNumber"),
                col("ID фахівця", "specialistId"),
                col("Виконана робота", "workDone")
        );

        TextField requestNumber = new TextField();
        TextField specialistId = new TextField();
        TextField workDone = new TextField();

        Button add = new Button("Додати");
        Button update = new Button("Змінити");
        Button delete = new Button("Видалити");
        Button refresh = new Button("Оновити");

        add.setOnAction(e -> safe(() -> {
            ExecutionRecord executionRecord = new ExecutionRecord(
                    null,
                    number(requestNumber, "ID кімнати"),
                    optionalNumber(specialistId, "ID фахівця"),
                    text(workDone, "Звіт про виконану роботу")
            );

            api.addExecutionRecord(executionRecord);
            refreshExecutionRecords();
        }));

        update.setOnAction(e -> safe(() -> {
            ExecutionRecord current = selected(executionRecords);

            ExecutionRecord executionRecord = new ExecutionRecord(
                    current.getId(),
                    number(requestNumber, "ID заявки"),
                    optionalNumber(specialistId, "ID фахівця"),
                    text(workDone, "Опис проблеми")
            );

            api.updateExecutionRecord(executionRecord);
            refreshExecutionRecords();
        }));

        delete.setOnAction(e -> safe(() -> {
            api.deleteExecutionRecord(selected(executionRecords).getId());
            refreshExecutionRecords();
        }));

        refresh.setOnAction(e -> refreshExecutionRecords());

        executionRecords.getSelectionModel().selectedItemProperty()
                .addListener((observable, previous, executionRecord) -> {
                    if (executionRecord != null) {
                        requestNumber.setText(
                                String.valueOf(executionRecord.getRequestNumber())
                        );

                        specialistId.setText(
                                executionRecord.getSpecialistId() == null
                                        ? ""
                                        : executionRecord.getSpecialistId().toString()
                        );

                        workDone.setText(executionRecord.getWorkDone());

                    }
                });

        VBox form = new VBox(
                8,
                new Label("ID заявки"), requestNumber,
                new Label("ID фахівця — можна залишити порожнім"), specialistId,
                new Label("Звіт виконаної роботи"), workDone,
                new HBox(8, add, update),
                new HBox(8, delete, refresh)
        );

        return layout(executionRecords, form);
    }

    private void refreshAll() { refreshRooms(); refreshSpecialists(); refreshRequests(); refreshExecutionRecords(); }
    private void refreshRooms() {
        safe(() -> rooms.setItems(
                FXCollections.observableArrayList(api.getRooms())
        ));
    }
    private void refreshSpecialists() {
        safe(() -> specialists.setItems(
                FXCollections.observableArrayList(api.getSpecialists())
        ));
    }

    private void refreshRequests() {
        safe(() -> requests.setItems(
                FXCollections.observableArrayList(api.getRequests())
        ));
    }

    private void refreshExecutionRecords() {
        safe(() -> executionRecords.setItems(
                FXCollections.observableArrayList(api.getExecutionRecords())
        ));
    }

    private <T> T selected(TableView<T> table) { T v=table.getSelectionModel().getSelectedItem(); if(v==null) throw new IllegalArgumentException("Виберіть запис"); return v; }
    private void safe(Runnable action) { try{ action.run(); }catch(Exception e){ LOG.warning(e.getMessage()); showError(e); } }
    private void showError(Throwable e) { Alert d=new Alert(Alert.AlertType.ERROR,e==null?"Помилка":e.getMessage(),ButtonType.OK); d.showAndWait(); }
    private Pane layout(TableView<?> table,Pane form) { SplitPane p=new SplitPane(table,form); p.setDividerPositions(0.68); BorderPane root=new BorderPane(p); root.setPadding(new Insets(10)); return root; }
    private <S,T> TableColumn<S,T> col(String title,String property) { TableColumn<S,T> c=new TableColumn<>(title); c.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>(property)); return c; }
    public static void main(String[] args) { launch(args); }

    private static final Logger LOG =
            Logger.getLogger(MainApp.class.getName());


    private String factorText(double value) {
        return java.math.BigDecimal.valueOf(value)
                .stripTrailingZeros()
                .toPlainString();
    }

    private Long optionalNumber(TextField field, String title) {
        if (field.getText().isBlank()) {
            return null;
        }

        return number(field, title);
    }

    private long number(TextField field, String title) {
    try {
        return Long.parseLong(field.getText().trim());
    } catch (NumberFormatException e) {
        throw new IllegalArgumentException(
                "Поле «" + title + "» має містити ціле число"
        );
    }
}

    private String text(TextField field, String title) {
        String value = field.getText().trim();

        if (value.isEmpty()) {
            throw new IllegalArgumentException(
                    "Заповніть поле «" + title + "»"
            );
        }

        return value;
    }
}
