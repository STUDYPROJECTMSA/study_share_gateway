package com.gateway.gateway.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gateway.gateway.dto.ConfigMessage;
import com.gateway.gateway.validation.JwtValidationCheck;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class GlobalAuthFilter extends AbstractGatewayFilterFactory<ConfigMessage> {
    private final ObjectMapper objectMapper;
    private final JwtValidationCheck jwtValidationCheck;

    private String getAuthHeader(ServerHttpRequest request) {
        return request.getHeaders().getOrEmpty("token").get(0);
    }

    private String getAuthRefreshHeader(ServerHttpRequest request) {
        return request.getHeaders().getOrEmpty("refresh").get(0);
    }

    private boolean isAuthMissing(ServerHttpRequest request,String path) {
        if(path.equals("refresh")){
            return this.getAuthRefreshHeader(request).isEmpty();
        }
        return this.getAuthHeader(request).isEmpty();
    }


    //TODO: auth check 가 필요없는 로직인경우 뺀다.
    @Override
    public GatewayFilter apply(ConfigMessage configMessage) {
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            String pathString = request.getURI().getPath();
            String[] extractPath = pathString.split("/");

            String token = this.getAuthHeader(request);
            if(extractPath[1].equals("refresh")){
                token = this.getAuthRefreshHeader(request);
            }

            if (this.isAuthMissing(request,extractPath[1])) {
                return jwtValidationCheck.onError(exchange, "token header is missing", HttpStatus.UNAUTHORIZED);
            }
            if (jwtValidationCheck.isInvalid(token)) {
                return jwtValidationCheck.onError(exchange, "token is Expired", HttpStatus.UNAUTHORIZED);
            }
            return chain.filter(exchange);
        });
    }
}
