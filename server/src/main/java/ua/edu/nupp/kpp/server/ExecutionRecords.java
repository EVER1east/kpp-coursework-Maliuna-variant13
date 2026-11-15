package ua.edu.nupp.kpp.server;



public class ExecutionRecords {
    private Long id;
    private long requestNumber;
    private String workDone;

    public ExecutionRecords(Long id, long requestNumber, String workDone) {
        this.id = id;
        this.requestNumber = requestNumber;
        this.workDone = workDone;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getRequestNumber() {
        return requestNumber;
    }

    public void setRequestNumber(long requestNumber) {
        this.requestNumber = requestNumber;
    }

    public String getWorkDone() {
        return workDone;
    }

    public void setWorkDone(String workDone) {
        this.workDone = workDone;
    }
}