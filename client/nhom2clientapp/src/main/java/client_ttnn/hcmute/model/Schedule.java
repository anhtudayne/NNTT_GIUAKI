package client_ttnn.hcmute.model;

public class Schedule {
    private Integer scheduleId;
    private Classes classEntity; 
    private Room room; 
    private String date; 
    private String startTime; 
    private String endTime; 

    public Schedule() {}

    public Integer getScheduleId() { return scheduleId; }
    public void setScheduleId(Integer scheduleId) { this.scheduleId = scheduleId; }

    public Classes getClassEntity() { return classEntity; }
    public void setClassEntity(Classes classEntity) { this.classEntity = classEntity; }

    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
}
