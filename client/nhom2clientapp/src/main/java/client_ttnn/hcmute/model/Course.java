package client_ttnn.hcmute.model;

public class Course {
    private Long courseId;
    private String courseName;
    private String description;
    private String level;
    private Integer duration;
    private Double fee;
    private String status;

    public Course() {
    }

    public Course(String courseName, String description, String level, Integer duration, Double fee, String status) {
        this.courseName = courseName;
        this.description = description;
        this.level = level;
        this.duration = duration;
        this.fee = fee;
        this.status = status;
    }

    // Getters and Setters
    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Double getFee() {
        return fee;
    }

    public void setFee(Double fee) {
        this.fee = fee;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Course{" +
                "courseId=" + courseId +
                ", courseName='" + courseName + '\'' +
                ", description='" + description + '\'' +
                ", level='" + level + '\'' +
                ", duration=" + duration +
                ", fee=" + fee +
                ", status='" + status + '\'' +
                '}';
    }
}
