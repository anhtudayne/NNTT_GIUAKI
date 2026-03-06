package client_ttnn.hcmute.model;

public class Room {
    private Long roomId;
    private String roomName;
    private Integer capacity;
    private String location;
    private String status;

    public Room() {
    }

    public Room(Long roomId) {
        this.roomId = roomId;
    }

    public Room(String roomName, Integer capacity, String location, String status) {
        this.roomName = roomName;
        this.capacity = capacity;
        this.location = location;
        this.status = status;
    }

    // Getters and Setters
    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Room{" +
                "roomId=" + roomId +
                ", roomName='" + roomName + '\'' +
                ", capacity=" + capacity +
                ", location='" + location + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
