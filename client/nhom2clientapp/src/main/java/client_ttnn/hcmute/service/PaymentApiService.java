package client_ttnn.hcmute.service;

import client_ttnn.hcmute.model.Payment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class PaymentApiService {

    private static final String BASE_URL = "http://localhost:8080/api/payments";
    private final HttpClient httpClient;
    private final Gson gson;

    public PaymentApiService() {
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    public List<Payment> getAllPayments() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), new TypeToken<List<Payment>>() {}.getType());
        }
        throw new Exception("Failed to get payments: " + response.statusCode());
    }

    public Payment getPaymentById(Long id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), Payment.class);
        }
        throw new Exception("Failed to get payment: " + response.statusCode());
    }

    public Payment createPayment(Payment payment) throws Exception {
        String jsonBody = gson.toJson(payment);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 201 || response.statusCode() == 200) {
            return gson.fromJson(response.body(), Payment.class);
        }
        throw new Exception("Failed to create payment: " + response.statusCode());
    }

    public Payment updatePayment(Long id, Payment payment) throws Exception {
        String jsonBody = gson.toJson(payment);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), Payment.class);
        }
        throw new Exception("Failed to update payment: " + response.statusCode());
    }

    public void deletePayment(Long id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id))
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200 && response.statusCode() != 204) {
            throw new Exception("Failed to delete payment: " + response.statusCode());
        }
    }
}

