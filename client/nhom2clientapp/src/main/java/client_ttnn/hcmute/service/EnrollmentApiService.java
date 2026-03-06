package client_ttnn.hcmute.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import client_ttnn.hcmute.model.Enrollment;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class EnrollmentApiService {
    private static final String BASE_URL = "http://localhost:8080/api/enrollments";
    private final HttpClient httpClient;
    private final Gson gson;

    public EnrollmentApiService() {
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    public List<Enrollment> getAllEnrollments() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), new TypeToken<List<Enrollment>>() {}.getType());
        }
        throw new Exception("Failed to get enrollments: " + response.statusCode());
    }

    public Enrollment getEnrollmentById(Long id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), Enrollment.class);
        }
        throw new Exception("Failed to get enrollment: " + response.statusCode());
    }

    public Enrollment createEnrollment(Enrollment enrollment) throws Exception {
        String jsonBody = gson.toJson(enrollment);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 201 || response.statusCode() == 200) {
            return gson.fromJson(response.body(), Enrollment.class);
        }
        throw new Exception("Failed to create enrollment: " + response.statusCode());
    }

    public Enrollment updateEnrollment(Long id, Enrollment enrollment) throws Exception {
        String jsonBody = gson.toJson(enrollment);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), Enrollment.class);
        }
        throw new Exception("Failed to update enrollment: " + response.statusCode());
    }

    public void deleteEnrollment(Long id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id))
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200 && response.statusCode() != 204) {
            throw new Exception("Failed to delete enrollment: " + response.statusCode());
        }
    }
}
