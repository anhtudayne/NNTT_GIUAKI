package client_ttnn.hcmute.service;

import client_ttnn.hcmute.model.Attendance;
import client_ttnn.hcmute.model.Student;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.lang.reflect.Type;
import java.util.List;

public class AttendanceApiService {
    private static final String API_URL = "http://localhost:8080/api/attendances";
    private final HttpClient httpClient;
    private final Gson gson;

    public AttendanceApiService() {
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    // 1. Gọi API Get List Học viên đang học trong Lớp (Thứ tự Load Grid)
    public List<Student> getStudentsByClassId(Integer classId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(API_URL + "/class/" + classId + "/students"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            Type listType = new TypeToken<List<Student>>(){}.getType();
            return gson.fromJson(response.body(), listType);
        } else {
            throw new Exception("Lỗi Load Học sinh lớp: " + response.body());
        }
    }

    // 2. Gọi API Lưu toàn bộ Dòng Checkbox List<Attendance> cho ngày đó xuống Data (Batch-Save)
    public boolean saveBatchAttendances(List<Attendance> attendances) throws Exception {
        String jsonPayload = gson.toJson(attendances);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(API_URL + "/batch-save"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 201 || response.statusCode() == 200) {
            return true;
        } else {
            throw new Exception("Lỗi khi Điểm danh: " + response.body());
        }
    }
}
