package com.epam.community.middlesvc.models;

public enum CarFullTypeEnum {
    ELECTRIC,
    GASOLINE,
    DIESEL,
    HYBRID,
    SPECIAL;

    public static CarFullTypeEnum fromString(final String type) {
        return valueOf(type.toUpperCase());
    }
}
