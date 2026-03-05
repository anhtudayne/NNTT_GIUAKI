package client_ttnn.hcmute.model;

public class Teacher {
    private Integer teacherId;
    private String fullName;
    private String phone;
    private String email;
    private String specialty;
    // Sử dụng String yyyy-MM-dd để map dễ hơn với LocalDate của JSON
    private String hireDate;
    private String status;

    public Teacher() {
    }

    public Teacher(Integer teacherId, String fullName, String phone, String email, String specialty, String hireDate, String status) {
        this.teacherId = teacherId;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.specialty = specialty;
        this.hireDate = hireDate;
        this.status = status;
    }

    public Integer getTeacherId() { return teacherId; }
    public void setTeacherId(Integer teacherId) { this.teacherId = teacherId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }

    public String getHireDate() { return hireDate; }
    public void setHireDate(String hireDate) { this.hireDate = hireDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
