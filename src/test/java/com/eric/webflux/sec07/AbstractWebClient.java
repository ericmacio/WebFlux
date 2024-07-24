package com.eric.webflux.sec07;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.function.Consumer;

abstract class AbstractWebClient {

    private static final Logger log = LoggerFactory.getLogger((AbstractWebClient.class));
    private static final String BASE_URL = "http://localhost:7070/demo02";

    protected <T> Consumer<T> print() {
        return item -> log.info("received: {}", item);
    }

    protected WebClient createWebClient() {
        return createWebClient(b -> {});
    }

    protected WebClient createWebClient(Consumer<WebClient.Builder> consumer) {
        var builder = WebClient.builder().baseUrl(BASE_URL);
        consumer.accept(builder);
        return builder.build();
    }
}