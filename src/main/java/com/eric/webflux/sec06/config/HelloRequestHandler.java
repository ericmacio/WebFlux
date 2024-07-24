package com.eric.webflux.sec06.config;

import com.eric.webflux.sec06.dto.CustomerDto;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;


@Service
public class HelloRequestHandler {

    public Mono<ServerResponse> get(ServerRequest request) {
        return Mono.just("Hello the world").flatMap(ServerResponse.ok()::bodyValue);
    }
}
