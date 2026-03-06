package client_ttnn.hcmute.model;

public class Attendance {
    private Integer attendanceId;
    private Student student;
    private Classes classEntity;
    private String date;
    private String status;

    public Attendance() {
    }

    public Attendance(Integer attendanceId, Student student, Classes classEntity, String date, String status) {
        this.attendanceId = attendanceId;
        this.student = student;
        this.classEntity = classEntity;
        this.date = date;
        this.status = status;
    }

    public Integer getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(Integer attendanceId) {
        this.attendanceId = attendanceId;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Classes getClassEntity() {
        return classEntity;
    }

    public void setClassEntity(Classes classEntity) {
        this.classEntity = classEntity;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
