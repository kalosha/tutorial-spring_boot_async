package com.epam.community.downstreamserver.models;

public record CarModel(int id,
                       String model,
                       IdNameModel manufacturer,
                       int year,
                       String fullType,
                       String type) {
}
