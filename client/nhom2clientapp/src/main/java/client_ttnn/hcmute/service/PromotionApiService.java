package client_ttnn.hcmute.service;

import client_ttnn.hcmute.model.Promotion;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class PromotionApiService {

    private static final String BASE_URL = "http://localhost:8080/api/promotions";
    private final HttpClient httpClient;
    private final Gson gson;

    public PromotionApiService() {
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    public List<Promotion> getAllPromotions() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), new TypeToken<List<Promotion>>() {}.getType());
        }
        throw new Exception("Failed to get promotions: " + response.statusCode());
    }

    public List<Promotion> getActivePromotions(String date) throws Exception {
        String url = BASE_URL + "/active";
        if (date != null && !date.isBlank()) {
            url += "?date=" + date;
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), new TypeToken<List<Promotion>>() {}.getType());
        }
        throw new Exception("Failed to get active promotions: " + response.statusCode());
    }

    public Promotion getPromotionById(Long id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), Promotion.class);
        }
        throw new Exception("Failed to get promotion: " + response.statusCode());
    }

    public Promotion createPromotion(Promotion promotion) throws Exception {
        String jsonBody = gson.toJson(promotion);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 201 || response.statusCode() == 200) {
            return gson.fromJson(response.body(), Promotion.class);
        }
        throw new Exception("Failed to create promotion: " + response.statusCode());
    }

    public Promotion updatePromotion(Long id, Promotion promotion) throws Exception {
        String jsonBody = gson.toJson(promotion);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), Promotion.class);
        }
        throw new Exception("Failed to update promotion: " + response.statusCode());
    }

    public void deletePromotion(Long id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id))
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200 && response.statusCode() != 204) {
            throw new Exception("Failed to delete promotion: " + response.statusCode());
        }
    }

    public Promotion findByCode(String code) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/by-code?code=" + code))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), Promotion.class);
        }
        throw new Exception("Failed to find promotion by code: " + response.statusCode());
    }
}

