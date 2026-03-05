package client_ttnn.hcmute.service;

import client_ttnn.hcmute.model.Teacher;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class TeacherApiService {
    private static final String BASE_URL = "http://localhost:8080/api/teachers";
    private final HttpClient httpClient;
    private final Gson gson;

    public TeacherApiService() {
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    public List<Teacher> getAllTeachers() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), new TypeToken<List<Teacher>>(){}.getType());
        }
        throw new Exception("Lỗi lấy danh sách giảng viên: " + response.statusCode());
    }

    public List<Teacher> searchByName(String name) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/search?name=" + name))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), new TypeToken<List<Teacher>>(){}.getType());
        }
        throw new Exception("Lỗi tìm kiếm giảng viên: " + response.statusCode());
    }
    
    public List<Teacher> getActiveTeachers() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/active"))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), new TypeToken<List<Teacher>>(){}.getType());
        }
        throw new Exception("Lỗi tải danh sách giảng viên Active: " + response.statusCode());
    }

    public Teacher createTeacher(Teacher teacher) throws Exception {
        String jsonBody = gson.toJson(teacher);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 201 || response.statusCode() == 200) {
            return gson.fromJson(response.body(), Teacher.class);
        }
        throw new Exception("Lỗi thêm giảng viên: " + response.statusCode());
    }

    public Teacher updateTeacher(Integer id, Teacher teacher) throws Exception {
        String jsonBody = gson.toJson(teacher);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), Teacher.class);
        }
        throw new Exception("Lỗi cập nhật giảng viên: " + response.statusCode());
    }

    public void deleteTeacher(Integer id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id))
                .DELETE()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 204 && response.statusCode() != 200) {
            throw new Exception("Lỗi xóa giảng viên: " + response.statusCode());
        }
    }
}
