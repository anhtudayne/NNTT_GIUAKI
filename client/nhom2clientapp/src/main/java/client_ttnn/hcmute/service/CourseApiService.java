package client_ttnn.hcmute.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import client_ttnn.hcmute.model.Course;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class CourseApiService {
    private static final String BASE_URL = "http://localhost:8080/api/courses";
    private final HttpClient httpClient;
    private final Gson gson;

    public CourseApiService() {
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    // GET All Courses
    public List<Course> getAllCourses() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), new TypeToken<List<Course>>(){}.getType());
        } else {
            throw new Exception("Failed to get courses: " + response.statusCode());
        }
    }

    // GET Course by ID
    public Course getCourseById(Long id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), Course.class);
        } else {
            throw new Exception("Failed to get course: " + response.statusCode());
        }
    }

    // POST Create Course
    public Course createCourse(Course course) throws Exception {
        String jsonBody = gson.toJson(course);
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 201 || response.statusCode() == 200) {
            return gson.fromJson(response.body(), Course.class);
        } else {
            throw new Exception("Failed to create course: " + response.statusCode());
        }
    }

    // PUT Update Course
    public Course updateCourse(Long id, Course course) throws Exception {
        String jsonBody = gson.toJson(course);
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), Course.class);
        } else {
            throw new Exception("Failed to update course: " + response.statusCode());
        }
    }

    // DELETE Course
    public void deleteCourse(Long id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id))
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() != 200 && response.statusCode() != 204) {
            throw new Exception("Failed to delete course: " + response.statusCode());
        }
    }

    // Search Course by Name
    public List<Course> searchByName(String name) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/search?name=" + name))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), new TypeToken<List<Course>>(){}.getType());
        } else {
            throw new Exception("Failed to search courses: " + response.statusCode());
        }
    }
}
