package com.gateway.gateway.resilience4jTool;

import io.github.resilience4j.common.retry.configuration.RetryConfigCustomizer;
import io.github.resilience4j.core.IntervalFunction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Slf4j
@Configuration
public class Resilience4jConfig {
    @Bean
    public RetryConfigCustomizer RetryConfigCustomizertestCustomizer() {
        return RetryConfigCustomizer.of("getAccessToken", builder -> {
            builder.intervalFunction(IntervalFunction.of(Duration.ofSeconds(3).toMillis())).build();
        });
    }
}
