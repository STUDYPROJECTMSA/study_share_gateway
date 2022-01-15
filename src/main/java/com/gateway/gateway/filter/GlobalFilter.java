package com.gateway.gateway.filter;

import com.gateway.gateway.dto.Config;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class GlobalFilter extends AbstractGatewayFilterFactory<Config> {
    public GlobalFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            log.info("GlobalFilter baseMessage: {}", config.getBaseMessage());

            if (config.isPreLogger()) {
                log.info("errorMessage:{}", exchange.getRequest());
            }
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                        if (config.isPostLogger()) {
                            log.info("GlobalFilter End : {}", exchange.getRequest());
                        }
                    })
            );
        });
    }
}
