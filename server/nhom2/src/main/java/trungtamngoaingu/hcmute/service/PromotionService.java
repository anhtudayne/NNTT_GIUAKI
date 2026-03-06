package trungtamngoaingu.hcmute.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import trungtamngoaingu.hcmute.entity.Promotion;
import trungtamngoaingu.hcmute.repository.PromotionRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PromotionService {
    @Autowired
    private PromotionRepository promotionRepository;

    public List<Promotion> getAllPromotions() {
        return promotionRepository.myGetAll();
    }

    public Optional<Promotion> getPromotionById(Integer id) {
        return promotionRepository.findById(id);
    }

    public Promotion createPromotion(Promotion promotion) {
        return promotionRepository.save(promotion);
    }

    public Promotion updatePromotion(Integer id, Promotion promotion) {
        if (promotionRepository.existsById(id)) {
            promotion.setPromotionId(id);
            return promotionRepository.save(promotion);
        }
        return null;
    }

    public void deletePromotion(Integer id) {
        promotionRepository.deleteById(id);
    }

    /**
     * Tìm promotion theo mã (không phân biệt hoa thường) bằng cách đọc toàn bộ danh sách
     * rồi filter bằng Java Stream.
     */
    public Optional<Promotion> findByCode(String promoCode) {
        if (promoCode == null || promoCode.isBlank()) {
            return Optional.empty();
        }
        String normalized = promoCode.trim().toLowerCase();
        return promotionRepository.myGetAll()
                .stream()
                .filter(p -> p.getPromoCode() != null
                        && p.getPromoCode().trim().toLowerCase().equals(normalized))
                .findFirst();
    }

    /**
     * Lấy danh sách promotion còn hiệu lực tại một ngày cụ thể.
     * - Nếu startDate/endDate null sẽ được hiểu là không giới hạn về phía đó.
     */
    public List<Promotion> getActivePromotions(LocalDate date) {
        LocalDate target = (date != null) ? date : LocalDate.now();

        return promotionRepository.myGetAll()
                .stream()
                .filter(p -> isPromotionActiveOnDate(p, target))
                .collect(Collectors.toList());
    }

    private boolean isPromotionActiveOnDate(Promotion promotion, LocalDate date) {
        if (promotion == null) return false;

        LocalDate start = promotion.getStartDate();
        LocalDate end = promotion.getEndDate();

        boolean afterStart = (start == null) || !date.isBefore(start);
        boolean beforeEnd = (end == null) || !date.isAfter(end);
        return afterStart && beforeEnd;
    }

    /**
     * Validate một mã promotion tại thời điểm cho trước.
     * - Trả về Optional rỗng nếu không tìm thấy hoặc đã hết hiệu lực.
     */
    public Optional<Promotion> validatePromotion(String promoCode, LocalDate date) {
        LocalDate target = (date != null) ? date : LocalDate.now();
        return findByCode(promoCode)
                .filter(p -> isPromotionActiveOnDate(p, target));
    }
}
