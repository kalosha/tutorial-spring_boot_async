package com.epam.community.middlesvc.models;

import lombok.Builder;

import java.util.List;

@Builder
public record StateModel(
        int id,
        String code,
        int priceLimit,
        List<DiscountModel> discounts) {
}
