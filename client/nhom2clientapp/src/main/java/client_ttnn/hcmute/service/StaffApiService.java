package client_ttnn.hcmute.service;

import client_ttnn.hcmute.model.Staff;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class StaffApiService {
    private static final String API_URL = "http://localhost:8080/api/staff";
    private final HttpClient httpClient;
    private final Gson gson;

    public StaffApiService() {
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    public List<Staff> getAllStaff() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(API_URL))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return gson.fromJson(response.body(), new TypeToken<List<Staff>>(){}.getType());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    // GỌI ENDPOINT STREAM TÌM KIẾM THEO TÊN
    public List<Staff> searchStaffByName(String name) {
        try {
            String uriStr = API_URL + "/search?name=" + java.net.URLEncoder.encode(name, "UTF-8");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(uriStr))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return gson.fromJson(response.body(), new TypeToken<List<Staff>>(){}.getType());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    // GỌI ENDPOINT STREAM LỌC THEO VAI TRÒ
    public List<Staff> getStaffByRole(String role) {
        try {
            String uriStr = API_URL + "/role?role=" + java.net.URLEncoder.encode(role, "UTF-8");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(uriStr))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return gson.fromJson(response.body(), new TypeToken<List<Staff>>(){}.getType());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public Staff createStaff(Staff staff) {
        try {
            String jsonPayload = gson.toJson(staff);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(API_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 201) {
                return gson.fromJson(response.body(), Staff.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateStaff(Staff staff) {
        try {
            String jsonPayload = gson.toJson(staff);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(API_URL + "/" + staff.getStaffId()))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteStaff(Integer id) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(API_URL + "/" + id))
                    .DELETE()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 204;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
