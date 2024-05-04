package com.epam.community.middlesvc.models;

public enum CarTypeEnum {
    SEDAN,
    SUV,
    TRUCK;

    public static CarTypeEnum fromString(String type) {
        return valueOf(type.toUpperCase());
    }
}
