package client_ttnn.hcmute.model;

public class Classes {
    private Long classId;
    private String className;
    private Course course;
    private Teacher teacher;
    private Room room;
    private String startDate;
    private String endDate;
    private Integer maxStudent;
    private String status;

    public Classes() {
    }

    public Classes(String className, Course course, Teacher teacher, Room room, 
                   String startDate, String endDate, Integer maxStudent, String status) {
        this.className = className;
        this.course = course;
        this.teacher = teacher;
        this.room = room;
        this.startDate = startDate;
        this.endDate = endDate;
        this.maxStudent = maxStudent;
        this.status = status;
    }

    // Getters and Setters
    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Integer getMaxStudent() {
        return maxStudent;
    }

    public void setMaxStudent(Integer maxStudent) {
        this.maxStudent = maxStudent;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Classes{" +
                "classId=" + classId +
                ", className='" + className + '\'' +
                ", course=" + (course != null ? course.getCourseId() : null) +
                ", teacher=" + (teacher != null ? teacher.getTeacherId() : null) +
                ", room=" + (room != null ? room.getRoomId() : null) +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", maxStudent=" + maxStudent +
                ", status='" + status + '\'' +
                '}';
    }
}
