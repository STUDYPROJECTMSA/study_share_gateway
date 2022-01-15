package com.gateway.gateway.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
public class GwErrorResponse {
    private final String errorMessage;
    private final int errorCode;
    private final LocalDateTime localDateTime;
    private Map<String, Object> addtionInfo = new HashMap<>();

    public static GwErrorResponse defaultBuild(String errorMessage, int errorCode) {
        return new GwErrorResponse("errorMessage", 111, LocalDateTime.now());
    }
}
