package client_ttnn.hcmute.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import client_ttnn.hcmute.model.PlacementTest;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class PlacementTestApiService {
    private static final String BASE_URL = "http://localhost:8080/api/placement-tests";
    private final HttpClient httpClient;
    private final Gson gson;

    public PlacementTestApiService() {
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    public List<PlacementTest> getAllPlacementTests() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), new TypeToken<List<PlacementTest>>() {}.getType());
        }
        throw new Exception("Failed to get placement tests: " + response.statusCode());
    }

    public PlacementTest getPlacementTestById(Long id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), PlacementTest.class);
        }
        throw new Exception("Failed to get placement test: " + response.statusCode());
    }

    public PlacementTest createPlacementTest(PlacementTest placementTest) throws Exception {
        String jsonBody = gson.toJson(placementTest);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 201 || response.statusCode() == 200) {
            return gson.fromJson(response.body(), PlacementTest.class);
        }
        throw new Exception("Failed to create placement test: " + response.statusCode());
    }

    public PlacementTest updatePlacementTest(Long id, PlacementTest placementTest) throws Exception {
        String jsonBody = gson.toJson(placementTest);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), PlacementTest.class);
        }
        throw new Exception("Failed to update placement test: " + response.statusCode());
    }

    public void deletePlacementTest(Long id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id))
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200 && response.statusCode() != 204) {
            throw new Exception("Failed to delete placement test: " + response.statusCode());
        }
    }
}
