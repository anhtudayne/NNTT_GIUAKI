package client_ttnn.hcmute.model;

import java.math.BigDecimal;

public class Promotion {
    private Long promotionId;
    private String promoCode;
    private BigDecimal discountPercent;
    private String startDate;
    private String endDate;
    private String description;

    public Long getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(Long promotionId) {
        this.promotionId = promotionId;
    }

    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }

    public BigDecimal getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(BigDecimal discountPercent) {
        this.discountPercent = discountPercent;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        if (discountPercent != null) {
            return promoCode + " (" + discountPercent.stripTrailingZeros().toPlainString() + "%)";
        }
        return promoCode;
    }
}

