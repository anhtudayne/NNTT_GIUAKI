package client_ttnn.hcmute.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import client_ttnn.hcmute.model.Classes;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class ClassesApiService {
    private static final String BASE_URL = "http://localhost:8080/api/classes";
    private final HttpClient httpClient;
    private final Gson gson;

    public ClassesApiService() {
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    // GET All Classes
    public List<Classes> getAllClasses() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), new TypeToken<List<Classes>>(){}.getType());
        } else {
            throw new Exception("Failed to get classes: " + response.statusCode());
        }
    }

    // GET Class by ID
    public Classes getClassById(Long id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), Classes.class);
        } else {
            throw new Exception("Failed to get class: " + response.statusCode());
        }
    }

    // POST Create Class
    public Classes createClass(Classes classes) throws Exception {
        String jsonBody = gson.toJson(classes);
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 201 || response.statusCode() == 200) {
            return gson.fromJson(response.body(), Classes.class);
        } else {
            throw new Exception("Failed to create class: " + response.statusCode());
        }
    }

    // PUT Update Class
    public Classes updateClass(Long id, Classes classes) throws Exception {
        String jsonBody = gson.toJson(classes);
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), Classes.class);
        } else {
            throw new Exception("Failed to update class: " + response.statusCode());
        }
    }

    // DELETE Class
    public void deleteClass(Long id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id))
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() != 200 && response.statusCode() != 204) {
            throw new Exception("Failed to delete class: " + response.statusCode());
        }
    }

    // Search Class by Name
    public List<Classes> searchByName(String name) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/search?name=" + name))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), new TypeToken<List<Classes>>(){}.getType());
        } else {
            throw new Exception("Failed to search classes: " + response.statusCode());
        }
    }
}
