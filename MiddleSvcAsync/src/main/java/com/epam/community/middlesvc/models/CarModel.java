package com.epam.community.middlesvc.models;

import lombok.Builder;

@Builder
public record CarModel(
        int id,
        String model,
        int year,
        int dealerId,
        String dealer,
        int price,
        String manufacturer,
        int manufacturerId,
        CarFullTypeEnum fullType,
        CarTypeEnum type) {
}
