package com.epam.community.middlesvc.models;

import lombok.Builder;

@Builder
public record IdNameModel(
        int id,
        String name) {
}
