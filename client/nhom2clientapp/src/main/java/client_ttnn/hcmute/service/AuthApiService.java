package client_ttnn.hcmute.service;

import client_ttnn.hcmute.model.UserAccount;
import com.google.gson.Gson;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class AuthApiService {
    private static final String API_URL = "http://localhost:8080/api/auth";
    private final HttpClient httpClient;
    private final Gson gson;

    public AuthApiService() {
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    /**
     * Hàm gọi API Đăng nhập
     * @param username tên tài khoản
     * @param password mật khẩu
     * @return UserAccount chứa Role nếu thành công, null nếu thất bại
     */
    public UserAccount login(String username, String password) throws Exception {
        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", username);
        credentials.put("password", password);

        String jsonPayload = gson.toJson(credentials);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(API_URL + "/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), UserAccount.class);
        } else {
            throw new Exception("Đăng nhập thất bại: " + response.body());
        }
    }
}
