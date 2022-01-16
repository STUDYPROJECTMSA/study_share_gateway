package com.gateway.gateway.filter;

import com.gateway.gateway.dto.ConfigMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class GlobalFilter extends AbstractGatewayFilterFactory<ConfigMessage> {
    public GlobalFilter() {
        super(ConfigMessage.class);
    }

    @Override
    public GatewayFilter apply(ConfigMessage configMessage) {
        return ((exchange, chain) -> {
            log.info("GlobalFilter baseMessage: {}", configMessage.getBaseMessage());

            if (configMessage.isPreLogger()) {
                log.info("errorMessage:{}", exchange.getRequest());
            }
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                        if (configMessage.isPostLogger()) {
                            log.info("GlobalFilter End : {}", exchange.getRequest());
                        }
                    })
            );
        });
    }
}
