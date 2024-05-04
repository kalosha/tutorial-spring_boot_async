package com.epam.community.middlesvc.models;

import lombok.Builder;

@Builder
public record DealerCarModel(
        int id,
        String model,
        int year,
        String manufacturer,
        int manufacturerId,
        CarFullTypeEnum fullType,
        CarTypeEnum type) {
}
