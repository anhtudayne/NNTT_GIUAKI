package client_ttnn.hcmute.model;

public class Certificate {
    private Long certificateId;
    private Student student;
    private Course course;
    private String certificateName;
    private String issueDate;

    public Certificate() {
    }

    public Certificate(Student student, Course course, String certificateName, String issueDate) {
        this.student = student;
        this.course = course;
        this.certificateName = certificateName;
        this.issueDate = issueDate;
    }

    public Long getCertificateId() {
        return certificateId;
    }

    public void setCertificateId(Long certificateId) {
        this.certificateId = certificateId;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public String getCertificateName() {
        return certificateName;
    }

    public void setCertificateName(String certificateName) {
        this.certificateName = certificateName;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }
}
