package trungtamngoaingu.hcmute.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import trungtamngoaingu.hcmute.entity.Attendance;
import trungtamngoaingu.hcmute.service.AttendanceService;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/attendances")
@CrossOrigin(origins = "*")
public class AttendanceController {
    @Autowired
    private AttendanceService attendanceService;

    @GetMapping
    public ResponseEntity<List<Attendance>> getAllAttendances() {
        return ResponseEntity.ok(attendanceService.getAllAttendances());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Attendance> getAttendanceById(@PathVariable Integer id) {
        Optional<Attendance> attendance = attendanceService.getAttendanceById(id);
        return attendance.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Attendance> createAttendance(@RequestBody Attendance attendance) {
        Attendance saved = attendanceService.createAttendance(attendance);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Attendance> updateAttendance(@PathVariable Integer id, @RequestBody Attendance attendance) {
        Attendance updated = attendanceService.updateAttendance(id, attendance);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttendance(@PathVariable Integer id) {
        attendanceService.deleteAttendance(id);
        return ResponseEntity.noContent().build();
    }

    // ===================================================================================
    // TÍNH NĂNG GIAI ĐOẠN 4: ĐIỂM DANH HÀNG LOẠT LỚP HỌC
    // ===================================================================================

    // Lấy toàn bộ danh sách Học viên của 1 Lớp (Thông qua Enrollment) để hiện lên Grid điểm danh
    @GetMapping("/class/{classId}/students")
    public ResponseEntity<List<trungtamngoaingu.hcmute.entity.Student>> getStudentsByClassId(@PathVariable Integer classId) {
        List<trungtamngoaingu.hcmute.entity.Student> students = attendanceService.getStudentsByClassId(classId);
        return ResponseEntity.ok(students);
    }

    // Lưu hàng loạt Điểm danh của cả 1 lớp bằng 1 API duy nhất
    @PostMapping("/batch-save")
    public ResponseEntity<List<Attendance>> saveBatchAttendances(@RequestBody List<Attendance> attendances) {
        if (attendances == null || attendances.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        List<Attendance> saved = attendanceService.saveBatchAttendances(attendances);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}
