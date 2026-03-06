package client_ttnn.hcmute.service;

import client_ttnn.hcmute.model.Invoice;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class InvoiceApiService {

    private static final String BASE_URL = "http://localhost:8080/api/invoices";
    private final HttpClient httpClient;
    private final Gson gson;

    public InvoiceApiService() {
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    public List<Invoice> getAllInvoices() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), new TypeToken<List<Invoice>>() {}.getType());
        }
        throw new Exception("Failed to get invoices: " + response.statusCode());
    }

    public Invoice getInvoiceById(Long id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), Invoice.class);
        }
        throw new Exception("Failed to get invoice: " + response.statusCode());
    }

    public Invoice createInvoice(Invoice invoice) throws Exception {
        String jsonBody = gson.toJson(invoice);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 201 || response.statusCode() == 200) {
            return gson.fromJson(response.body(), Invoice.class);
        }
        throw new Exception("Failed to create invoice: " + response.statusCode());
    }

    public Invoice updateInvoice(Long id, Invoice invoice) throws Exception {
        String jsonBody = gson.toJson(invoice);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), Invoice.class);
        }
        throw new Exception("Failed to update invoice: " + response.statusCode());
    }

    public void deleteInvoice(Long id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id))
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200 && response.statusCode() != 204) {
            throw new Exception("Failed to delete invoice: " + response.statusCode());
        }
    }
}

