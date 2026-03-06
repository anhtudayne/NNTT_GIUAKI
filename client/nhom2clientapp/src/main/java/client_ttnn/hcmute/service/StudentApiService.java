package client_ttnn.hcmute.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import client_ttnn.hcmute.model.Student;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class StudentApiService {
    private static final String BASE_URL = "http://localhost:8080/api/students";
    private final HttpClient httpClient;
    private final Gson gson;

    public StudentApiService() {
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    // GET All Students
    public List<Student> getAllStudents() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), new TypeToken<List<Student>>(){}.getType());
        } else {
            throw new Exception("Failed to get students: " + response.statusCode());
        }
    }

    // GET Student by ID
    public Student getStudentById(Long id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), Student.class);
        } else {
            throw new Exception("Failed to get student: " + response.statusCode());
        }
    }

    // POST Create Student
    public Student createStudent(Student student) throws Exception {
        String jsonBody = gson.toJson(student);
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 201 || response.statusCode() == 200) {
            return gson.fromJson(response.body(), Student.class);
        } else {
            throw new Exception("Failed to create student: " + response.statusCode());
        }
    }

    // PUT Update Student
    public Student updateStudent(Long id, Student student) throws Exception {
        String jsonBody = gson.toJson(student);
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), Student.class);
        } else {
            throw new Exception("Failed to update student: " + response.statusCode());
        }
    }

    // DELETE Student
    public void deleteStudent(Long id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id))
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() != 204 && response.statusCode() != 200) {
            throw new Exception("Failed to delete student: " + response.statusCode());
        }
    }

    // Search Students by Name
    public List<Student> searchByName(String name) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/search?name=" + name))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), new TypeToken<List<Student>>(){}.getType());
        } else {
            throw new Exception("Failed to search students: " + response.statusCode());
        }
    }
}
