package trungtamngoaingu.hcmute.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import trungtamngoaingu.hcmute.entity.Promotion;
import trungtamngoaingu.hcmute.repository.PromotionRepository;
import java.util.List;
import java.util.Optional;

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
}
