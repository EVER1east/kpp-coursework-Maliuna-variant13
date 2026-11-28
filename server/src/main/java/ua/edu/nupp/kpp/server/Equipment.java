package ua.edu.nupp.kpp.server;

public class Equipment {
    private Long id;
    private long roomId;
    private String name;

    public Equipment() {
    }

    public Equipment(Long id, long roomId, String name) {
        this.id = id;
        this.roomId = roomId;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getRoomId() {
        return roomId;
    }

    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}




