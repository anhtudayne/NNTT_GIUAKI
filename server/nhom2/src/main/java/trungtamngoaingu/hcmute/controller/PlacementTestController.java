package trungtamngoaingu.hcmute.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
}
