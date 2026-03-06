package client_ttnn.hcmute.service;

import client_ttnn.hcmute.model.DashboardSummary;
import client_ttnn.hcmute.model.RevenueByMonth;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class DashboardApiService {

    private static final String BASE_URL = "http://localhost:8080/api/dashboard";
    private final HttpClient httpClient;
    private final Gson gson;

    public DashboardApiService() {
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    public DashboardSummary getSummary() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/summary"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), DashboardSummary.class);
        }
        throw new Exception("Failed to load dashboard summary: " + response.statusCode());
    }

    public List<RevenueByMonth> getRevenueByMonth(Integer year) throws Exception {
        String url = BASE_URL + "/revenue-by-month";
        if (year != null) {
            url += "?year=" + year;
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            Type listType = new TypeToken<List<RevenueByMonth>>() {}.getType();
            return gson.fromJson(response.body(), listType);
        }
        throw new Exception("Failed to load revenue by month: " + response.statusCode());
    }
}

