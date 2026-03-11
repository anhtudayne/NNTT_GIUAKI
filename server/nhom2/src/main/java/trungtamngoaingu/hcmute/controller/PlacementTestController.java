package trungtamngoaingu.hcmute.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import trungtamngoaingu.hcmute.dto.PlacementTestRecommendationDTO;
import trungtamngoaingu.hcmute.entity.Enrollment;
import trungtamngoaingu.hcmute.entity.PlacementTest;
import trungtamngoaingu.hcmute.service.PlacementTestService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/placement-tests")
@CrossOrigin(origins = "*")
public class PlacementTestController {
    @Autowired
    private PlacementTestService placementTestService;

    @GetMapping
    public ResponseEntity<List<PlacementTest>> getAllPlacementTests() {
        return ResponseEntity.ok(placementTestService.getAllPlacementTests());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlacementTest> getPlacementTestById(@PathVariable Integer id) {
        Optional<PlacementTest> test = placementTestService.getPlacementTestById(id);
        return test.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PlacementTest> createPlacementTest(@RequestBody PlacementTest test) {
        PlacementTest saved = placementTestService.createPlacementTest(test);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlacementTest> updatePlacementTest(@PathVariable Integer id, @RequestBody PlacementTest test) {
        PlacementTest updated = placementTestService.updatePlacementTest(id, test);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlacementTest(@PathVariable Integer id) {
        placementTestService.deletePlacementTest(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Gợi ý khóa học/lớp phù hợp cho một PlacementTest.
     * Ví dụ: GET /api/placement-tests/1/recommendations
     */
    @GetMapping("/{id}/recommendations")
    public ResponseEntity<PlacementTestRecommendationDTO> getRecommendations(@PathVariable Integer id) {
        Optional<PlacementTestRecommendationDTO> dto = placementTestService.getRecommendations(id);
        return dto.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Ghi danh học viên (từ PlacementTest) vào một lớp.
     * Ví dụ: POST /api/placement-tests/1/enroll?classId=2
     */
    @PostMapping("/{id}/enroll")
    public ResponseEntity<Enrollment> enrollFromPlacementTest(
            @PathVariable Integer id,
            @RequestParam("classId") Integer classId) {
        Optional<Enrollment> enrollment = placementTestService.enrollFromPlacementTest(id, classId);
        return enrollment
                .map(e -> ResponseEntity.status(HttpStatus.CREATED).body(e))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
    }
}
