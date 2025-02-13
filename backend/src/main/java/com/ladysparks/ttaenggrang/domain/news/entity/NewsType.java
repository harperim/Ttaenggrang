package com.ladysparks.ttaenggrang.domain.news.entity;

public enum NewsType {
    호재, 악재;

    public static NewsType fromString(String text) {
        for (NewsType type : NewsType.values()) {
            if (type.name().equalsIgnoreCase(text)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown news type: " + text);
    }
}
