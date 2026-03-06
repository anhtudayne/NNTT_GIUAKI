package client_ttnn.hcmute.model;

public class UserAccount {
    private Integer accountId;
    private String username;
    private String password; // Client có thể bỏ trống cũng được
    private String role;

    public UserAccount() {
    }

    public UserAccount(Integer accountId, String username, String password, String role) {
        this.accountId = accountId;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
