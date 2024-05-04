package com.epam.community.middlesvc.clients.responses;

public record CarResponse(int id,
                          String model,
                          IdNameResponse manufacturer,
                          int year,
                          String fullType,
                          String type) {
}
