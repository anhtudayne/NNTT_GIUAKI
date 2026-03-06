package trungtamngoaingu.hcmute.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import trungtamngoaingu.hcmute.entity.Promotion;
import trungtamngoaingu.hcmute.service.PromotionService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/promotions")
@CrossOrigin(origins = "*")
public class PromotionController {
    @Autowired
    private PromotionService promotionService;

    @GetMapping
    public ResponseEntity<List<Promotion>> getAllPromotions() {
        return ResponseEntity.ok(promotionService.getAllPromotions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Promotion> getPromotionById(@PathVariable Integer id) {
        Optional<Promotion> promotion = promotionService.getPromotionById(id);
        return promotion.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Promotion> createPromotion(@RequestBody Promotion promotion) {
        Promotion saved = promotionService.createPromotion(promotion);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Promotion> updatePromotion(@PathVariable Integer id, @RequestBody Promotion promotion) {
        Promotion updated = promotionService.updatePromotion(id, promotion);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePromotion(@PathVariable Integer id) {
        promotionService.deletePromotion(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Tìm promotion theo mã.
     * Ví dụ: GET /api/promotions/by-code?code=SUMMER2026
     */
    @GetMapping("/by-code")
    public ResponseEntity<Promotion> getPromotionByCode(@RequestParam("code") String code) {
        Optional<Promotion> promotion = promotionService.findByCode(code);
        return promotion.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Lấy danh sách promotion còn hiệu lực tại một ngày cụ thể.
     * Ví dụ:
     *  - GET /api/promotions/active
     *  - GET /api/promotions/active?date=2026-03-05
     */
    @GetMapping("/active")
    public ResponseEntity<List<Promotion>> getActivePromotions(
            @RequestParam(value = "date", required = false) String dateStr) {
        LocalDate date = null;
        if (dateStr != null && !dateStr.isBlank()) {
            date = LocalDate.parse(dateStr);
        }
        return ResponseEntity.ok(promotionService.getActivePromotions(date));
    }

    /**
     * Validate một mã promotion tại một ngày (mặc định ngày hiện tại).
     * Ví dụ:
     *  - GET /api/promotions/validate?code=SUMMER2026
     *  - GET /api/promotions/validate?code=SUMMER2026&date=2026-06-01
     */
    @GetMapping("/validate")
    public ResponseEntity<Promotion> validatePromotion(
            @RequestParam("code") String code,
            @RequestParam(value = "date", required = false) String dateStr) {
        LocalDate date = null;
        if (dateStr != null && !dateStr.isBlank()) {
            date = LocalDate.parse(dateStr);
        }

        Optional<Promotion> promotion = promotionService.validatePromotion(code, date);
        return promotion.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
