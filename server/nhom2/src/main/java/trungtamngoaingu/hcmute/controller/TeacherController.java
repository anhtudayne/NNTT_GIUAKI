package trungtamngoaingu.hcmute.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import trungtamngoaingu.hcmute.entity.Class;
import trungtamngoaingu.hcmute.entity.Teacher;
import trungtamngoaingu.hcmute.service.ClassService;
import trungtamngoaingu.hcmute.service.TeacherService;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/teachers")
@CrossOrigin(origins = "*")
public class TeacherController {
    @Autowired
    private TeacherService teacherService;

    @Autowired
    private ClassService classService;

    @GetMapping
    public ResponseEntity<List<Teacher>> getAllTeachers() {
        return ResponseEntity.ok(teacherService.getAllTeachers());
    }

    /**
     * Endpoint phân trang (giữ tương thích endpoint GET /api/teachers cũ).
     * Ví dụ: GET /api/teachers/paged?page=0&size=20&sort=teacherId&dir=asc
     */
    @GetMapping("/paged")
    public ResponseEntity<Page<Teacher>> getTeachersPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "teacherId") String sort,
            @RequestParam(defaultValue = "asc") String dir
    ) {
        Sort.Direction direction = "desc".equalsIgnoreCase(dir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort));
        return ResponseEntity.ok(teacherService.getTeachersPaged(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Teacher> getTeacherById(@PathVariable Integer id) {
        Optional<Teacher> teacher = teacherService.getTeacherById(id);
        return teacher.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Teacher> createTeacher(@RequestBody Teacher teacher) {
        Teacher saved = teacherService.createTeacher(teacher);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Teacher> updateTeacher(@PathVariable Integer id, @RequestBody Teacher teacher) {
        Teacher updated = teacherService.updateTeacher(id, teacher);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeacher(@PathVariable Integer id) {
        teacherService.deleteTeacher(id);
        return ResponseEntity.noContent().build();
    }

    // --- CÁC ENDPOINT SỬ DỤNG STREAM API ---

    @GetMapping("/search")
    public ResponseEntity<List<Teacher>> searchTeachers(@RequestParam(required = false) String name) {
        return ResponseEntity.ok(teacherService.searchTeachersByName(name));
    }

    @GetMapping("/active")
    public ResponseEntity<List<Teacher>> getActiveTeachers() {
        return ResponseEntity.ok(teacherService.getActiveTeachers());
    }

    @GetMapping("/specialty")
    public ResponseEntity<List<Teacher>> getTeachersBySpecialty(@RequestParam String specialty) {
        return ResponseEntity.ok(teacherService.getTeachersBySpecialty(specialty));
    }

    @GetMapping("/{id}/classes")
    public ResponseEntity<List<Class>> getTeachingClasses(@PathVariable Integer id) {
        return ResponseEntity.ok(classService.getClassesByTeacherId(id));
    }
}
