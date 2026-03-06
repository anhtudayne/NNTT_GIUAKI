package trungtamngoaingu.hcmute.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import trungtamngoaingu.hcmute.entity.Schedule;
import trungtamngoaingu.hcmute.service.ScheduleService;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/schedules")
@CrossOrigin(origins = "*")
public class ScheduleController {
    @Autowired
    private ScheduleService scheduleService;

    @GetMapping
    public ResponseEntity<List<Schedule>> getAllSchedules() {
        return ResponseEntity.ok(scheduleService.getAllSchedules());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Schedule> getScheduleById(@PathVariable Integer id) {
        Optional<Schedule> schedule = scheduleService.getScheduleById(id);
        return schedule.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Schedule> createSchedule(@RequestBody Schedule schedule) {
        Schedule saved = scheduleService.createSchedule(schedule);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Schedule> updateSchedule(@PathVariable Integer id, @RequestBody Schedule schedule) {
        Schedule updated = scheduleService.updateSchedule(id, schedule);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Integer id) {
        scheduleService.deleteSchedule(id);
        return ResponseEntity.noContent().build();
    }

    // ===================================================================================
    // TÍNH NĂNG KHÓ: KIỂM TRA PHÒNG TRỐNG VÀ GIÁO VIÊN RẢNH BẰNG STREAM API (LAMBDA)
    // ===================================================================================

    @GetMapping("/available-rooms")
    public ResponseEntity<List<trungtamngoaingu.hcmute.entity.Room>> getAvailableRooms(
            @RequestParam("date") String dateStr,
            @RequestParam("start") String startStr,
            @RequestParam("end") String endStr) {
        java.time.LocalDate date = java.time.LocalDate.parse(dateStr);
        java.time.LocalTime start = java.time.LocalTime.parse(startStr);
        java.time.LocalTime end = java.time.LocalTime.parse(endStr);
        
        List<trungtamngoaingu.hcmute.entity.Room> rooms = scheduleService.getAvailableRooms(date, start, end);
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/available-teachers")
    public ResponseEntity<List<trungtamngoaingu.hcmute.entity.Teacher>> getAvailableTeachers(
            @RequestParam("date") String dateStr,
            @RequestParam("start") String startStr,
            @RequestParam("end") String endStr) {
        java.time.LocalDate date = java.time.LocalDate.parse(dateStr);
        java.time.LocalTime start = java.time.LocalTime.parse(startStr);
        java.time.LocalTime end = java.time.LocalTime.parse(endStr);
        
        List<trungtamngoaingu.hcmute.entity.Teacher> teachers = scheduleService.getAvailableTeachers(date, start, end);
        return ResponseEntity.ok(teachers);
    }
}
