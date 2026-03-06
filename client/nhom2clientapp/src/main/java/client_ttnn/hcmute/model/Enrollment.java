package client_ttnn.hcmute.model;

public class Enrollment {
    private Long enrollmentId;
    private Student student;
    private Classes classEntity;
    private String enrollmentDate;
    private String status;
    private String result;

    public Enrollment() {
    }

    public Enrollment(Student student, Classes classEntity, String enrollmentDate, String status, String result) {
        this.student = student;
        this.classEntity = classEntity;
        this.enrollmentDate = enrollmentDate;
        this.status = status;
        this.result = result;
    }

    public Long getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(Long enrollmentId) {
        this.enrollmentId = enrollmentId;
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

    public String getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(String enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
