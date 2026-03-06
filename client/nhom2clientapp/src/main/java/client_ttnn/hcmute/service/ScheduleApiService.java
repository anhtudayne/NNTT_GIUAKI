package client_ttnn.hcmute.service;

import client_ttnn.hcmute.model.Schedule;
import client_ttnn.hcmute.model.Room;
import client_ttnn.hcmute.model.Teacher;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class ScheduleApiService {
    private static final String API_URL = "http://localhost:8080/api/schedules";
    private final HttpClient httpClient;
    private final Gson gson;

    public ScheduleApiService() {
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    public List<Schedule> getAllSchedules() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(API_URL))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return gson.fromJson(response.body(), new TypeToken<List<Schedule>>(){}.getType());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    // ==== GỌI API STREAM TỪ BACKEND ĐỂ CHECK PHÒNG TRỐNG ====
    public List<Room> getAvailableRooms(String date, String start, String end) {
        try {
            String url = String.format("%s/available-rooms?date=%s&start=%s&end=%s", API_URL, date, start, end);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
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

    // ==== GỌI API STREAM TỪ BACKEND ĐỂ CHECK GIÁO VIÊN RẢNH ====
    public List<Teacher> getAvailableTeachers(String date, String start, String end) {
        try {
            String url = String.format("%s/available-teachers?date=%s&start=%s&end=%s", API_URL, date, start, end);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return gson.fromJson(response.body(), new TypeToken<List<Teacher>>(){}.getType());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public Schedule createSchedule(Schedule schedule) {
        try {
            String jsonPayload = gson.toJson(schedule);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(API_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 201) {
                return gson.fromJson(response.body(), Schedule.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateSchedule(Schedule schedule) {
        try {
            String jsonPayload = gson.toJson(schedule);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(API_URL + "/" + schedule.getScheduleId()))
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

    public boolean deleteSchedule(Integer id) {
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
