package client_ttnn.hcmute.model;

public class Staff {
    private Integer staffId;
    private String fullName;
    private String role;
    private String phone;
    private String email;

    public Staff() {}

    public Staff(Integer staffId, String fullName, String role, String phone, String email) {
        this.staffId = staffId;
        this.fullName = fullName;
        this.role = role;
        this.phone = phone;
        this.email = email;
    }

    public Integer getStaffId() { return staffId; }
    public void setStaffId(Integer staffId) { this.staffId = staffId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
