package com.epam.community.middlesvc.models;

import lombok.Builder;

@Builder
public record DiscountModel(
        int id,
        String name,
        int percent,
        CarFullTypeEnum fullType) {
}
