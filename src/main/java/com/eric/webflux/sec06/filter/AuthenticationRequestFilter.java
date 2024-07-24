package com.eric.webflux.sec06.filter;

import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Objects;


public class AuthenticationRequestFilter {

    public static final Map<String, Category> TOKEN_CATEGORY_MAP = Map.of(
            "secret123", Category.STANDARD,
            "secret456", Category.PRIME
    );

    public static Mono<ServerResponse> validate(ServerRequest request, HandlerFunction<ServerResponse> next) {
        String token = request.headers().firstHeader("X-auth-token");
        if(Objects.nonNull(token) && TOKEN_CATEGORY_MAP.containsKey(token)) {
            request.attributes().put("category", TOKEN_CATEGORY_MAP.get(token));
            return next.handle(request);
        }
        return ServerResponse.status(HttpStatus.UNAUTHORIZED).build();
    }
}
