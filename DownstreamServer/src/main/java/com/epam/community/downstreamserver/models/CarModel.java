package com.epam.community.downstreamserver.models;

public record CarModel(int id, String model, ManufacturerModel manufacturer, int year, String fullType, String type) {
}
