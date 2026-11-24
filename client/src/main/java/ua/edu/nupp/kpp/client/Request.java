package ua.edu.nupp.kpp.client;

public class Request {
    private Long id;
    private long roomNumber;
    private Long specialistId;
    private RequestState state;
    private String description;

    private double f1;
    private double f2;
    private double f3;
    private double riskIndex;



    public Request() {}
    public Request(Long id, long roomNumber, Long specialistId, RequestState state, String description,
                       double f1, double f2, double f3) {
        this.id = id; this.roomNumber = roomNumber; this.specialistId = specialistId; this.state = state; this.description = description;
        this.f1 = f1; this.f2 = f2; this.f3 = f3;
    }


    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}

    public long getRoomNumber() {return roomNumber;}
    public void setRoomNumber(long roomNumber) {this.roomNumber = roomNumber;}

    public Long getSpecialistId() {return specialistId;}
    public void setSpecialistId(Long specialistId) {this.specialistId = specialistId;}

    public RequestState getState() {return state;}
    public void setState(RequestState state) {this.state = state;}

    public String getDescription() {return description;}
    public void setDescription(String description) {this.description = description;}

    public double getF1() {return f1;}
    public void setF1(double f1) {this.f1 = f1;}

    public double getF2() {return f2;}
    public void setF2(double f2) {this.f2 = f2;}

    public double getF3() {return f3;}
    public void setF3(double f3) {this.f3 = f3;}

    public double getRiskIndex() {
        return riskIndex;
    }
    public void setRiskIndex(double riskIndex) {
        this.riskIndex = riskIndex;
    }
}

