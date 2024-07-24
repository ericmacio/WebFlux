package com.eric.webflux.sec06.config;

import com.eric.webflux.sec06.exceptions.CustomerNotFoundException;
import com.eric.webflux.sec06.exceptions.InvalidInputException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.function.Consumer;

@Service
public class ApplicationExceptionHandler {

    public Mono<ServerResponse> handleException(CustomerNotFoundException ex, ServerRequest request) {
        return handleException(HttpStatus.NOT_FOUND, ex, request, problemDetail -> {
                    problemDetail.setType(URI.create("http://example.com/problems/customer-not-found"));
                    problemDetail.setTitle("Customer not found");
                });
    }

    public Mono<ServerResponse> handleException(InvalidInputException ex, ServerRequest request) {
        return handleException(HttpStatus.BAD_REQUEST, ex, request, problemDetail -> {
                    problemDetail.setType(URI.create("http://example.com/problems/invalid-input"));
                    problemDetail.setTitle("Invalid input");
                });
    }

    private Mono<ServerResponse> handleException(HttpStatus httpStatus, Exception ex, ServerRequest request, Consumer<ProblemDetail> consumer) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(httpStatus, ex.getMessage());
        consumer.accept(problemDetail);
        problemDetail.setInstance(URI.create(request.path()));
        return ServerResponse.status(httpStatus).bodyValue(problemDetail);
    }
}
