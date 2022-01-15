package com.gateway.gateway.validation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gateway.gateway.dto.GwErrorResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtValidationCheck {
    @Value("${security.jwt.token.security-key:secret-key}")
    private String secretKey;
    private final ObjectMapper objectMapper;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }

    public boolean isTokenExpired(String token) {
        return this.getAllClaimsFromToken(token).getExpiration().before(new Date());
    }

    public boolean isInvalid(String token) {
        return this.isTokenExpired(token);
    }

    public Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus){
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.writeWith(
                Mono.fromSupplier(()->{
                    DataBufferFactory bufferFactory = response.bufferFactory();
                    try{
                        GwErrorResponse gwErrorResponse = GwErrorResponse.defaultBuild(err,401);
                        byte[] errorResponse = objectMapper.writeValueAsBytes(gwErrorResponse);
                        return bufferFactory.wrap(errorResponse);

                    } catch (JsonProcessingException e) {
                        log.error("error", e);
                        return bufferFactory.wrap(new byte[0]);
                    }
                })
        );
    }
}
