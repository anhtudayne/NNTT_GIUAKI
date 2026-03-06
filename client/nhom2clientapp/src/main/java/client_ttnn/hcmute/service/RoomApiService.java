package client_ttnn.hcmute.service;

import client_ttnn.hcmute.model.Room;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class RoomApiService {
    private static final String API_URL = "http://localhost:8080/api/rooms";
    private final HttpClient httpClient;
    private final Gson gson;

    public RoomApiService() {
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    public List<Room> getAllRooms() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(API_URL))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return gson.fromJson(response.body(), new TypeToken<List<Room>>(){}.getType());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    // GỌI ENDPOINT STREAM LỌC THEO STATUS
    public List<Room> getRoomsByStatus(String status) {
        try {
            String uriStr = API_URL + "/status?status=" + java.net.URLEncoder.encode(status, "UTF-8");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(uriStr))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return gson.fromJson(response.body(), new TypeToken<List<Room>>(){}.getType());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    // GỌI ENDPOINT STREAM LỌC THEO SỨC CHỨA MIN
    public List<Room> getRoomsByMinCapacity(Integer minCapacity) {
        try {
            String uriStr = API_URL + "/capacity?minCapacity=" + minCapacity;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(uriStr))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return gson.fromJson(response.body(), new TypeToken<List<Room>>(){}.getType());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public Room createRoom(Room room) {
        try {
            String jsonPayload = gson.toJson(room);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(API_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 201) {
                return gson.fromJson(response.body(), Room.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateRoom(Room room) {
        try {
            String jsonPayload = gson.toJson(room);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(API_URL + "/" + room.getRoomId()))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteRoom(Long id) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(API_URL + "/" + id))
                    .DELETE()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 204;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
