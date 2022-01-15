package com.gateway.gateway.dto;

import lombok.Data;
import lombok.Getter;

@Data
public class Config {
    private final String baseMessage;
    private boolean preLogger;
    private boolean postLogger;
}
