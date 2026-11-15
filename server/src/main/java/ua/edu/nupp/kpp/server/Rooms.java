package ua.edu.nupp.kpp.server;

public class Rooms {
    private Long id;
    private Long roomNumber;
    private Long price;
    private Long status;

    public Rooms() {
    }

    public Rooms(Long id, Long roomNumber, Long price, Long status) {
        this.id = id;
        this.roomNumber = roomNumber;
        this.price = price;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(Long roomNumber) {
        this.roomNumber = roomNumber;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    void main() {
    }


}




