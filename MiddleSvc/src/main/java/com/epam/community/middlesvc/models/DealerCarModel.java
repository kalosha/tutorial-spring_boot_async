package com.epam.community.middlesvc.models;

public record DealerCarModel(
        int id,
        String model,
        int year,
        String manufacturer,
        int manufacturerId,
        CarFullTypeEnum fullType,
        CarTypeEnum type) {
}
