package client_ttnn.hcmute.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import client_ttnn.hcmute.model.Certificate;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class CertificateApiService {
    private static final String BASE_URL = "http://localhost:8080/api/certificates";
    private final HttpClient httpClient;
    private final Gson gson;

    public CertificateApiService() {
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    public List<Certificate> getAllCertificates() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), new TypeToken<List<Certificate>>() {}.getType());
        }
        throw new Exception("Failed to get certificates: " + response.statusCode());
    }

    public Certificate getCertificateById(Long id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), Certificate.class);
        }
        throw new Exception("Failed to get certificate: " + response.statusCode());
    }

    public Certificate createCertificate(Certificate certificate) throws Exception {
        String jsonBody = gson.toJson(certificate);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 201 || response.statusCode() == 200) {
            return gson.fromJson(response.body(), Certificate.class);
        }
        throw new Exception("Failed to create certificate: " + response.statusCode());
    }

    public Certificate updateCertificate(Long id, Certificate certificate) throws Exception {
        String jsonBody = gson.toJson(certificate);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), Certificate.class);
        }
        throw new Exception("Failed to update certificate: " + response.statusCode());
    }

    public void deleteCertificate(Long id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id))
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200 && response.statusCode() != 204) {
            throw new Exception("Failed to delete certificate: " + response.statusCode());
        }
    }
}
