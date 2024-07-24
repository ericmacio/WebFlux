package com.eric.webflux.sec06.filter;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;


public class AuthorizationRequestFilter {

    public static final Map<String, Category> TOKEN_CATEGORY_MAP = Map.of(
            "secret123", Category.STANDARD,
            "secret456", Category.PRIME
    );

    public static Mono<ServerResponse> validate(ServerRequest request, HandlerFunction<ServerResponse> next) {
        Category category = (Category) request.attributes().getOrDefault("category", Category.STANDARD);
        return switch(category) {
            case STANDARD -> standard(request, next);
            case PRIME -> prime(request, next);
        };
    }

    private static Mono<ServerResponse> prime(ServerRequest request, HandlerFunction<ServerResponse> next) {
        return next.handle(request);
    }

    private static Mono<ServerResponse> standard(ServerRequest request, HandlerFunction<ServerResponse> next) {
        var isGetRequest = HttpMethod.GET.equals(request.method());
        if (isGetRequest) {
            return next.handle(request);
        }
        return ServerResponse.status(HttpStatus.FORBIDDEN).build();
    }
}
