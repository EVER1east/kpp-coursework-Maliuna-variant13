package ua.edu.nupp.kpp.client;

public class Equipment {
    private Long id;
    private long room_id;

    private String name;


    public Equipment() {}
    public Equipment(Long id, long room_id, String name) {
        this.id = id;
        this.room_id = room_id;
        this.name = name;
    }

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}

    public long getRoomId() {return room_id;}
    public void setRoomId(long roomId) {this.room_id = roomId;}

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
}

