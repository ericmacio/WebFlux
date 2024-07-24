package com.eric.webflux.sec06.config;

import com.eric.webflux.sec06.exceptions.CustomerNotFoundException;
import com.eric.webflux.sec06.exceptions.InvalidInputException;
import com.eric.webflux.sec06.filter.AuthenticationRequestFilter;
import com.eric.webflux.sec06.filter.AuthorizationRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterConfiguration {

    private static final String API_V1 = "/api/v1";

    @Autowired
    private CustomerRequestHandler customerRequestHandler;

    @Autowired
    private HelloRequestHandler helloRequestHandler;

    @Autowired
    private ApplicationExceptionHandler applicationExceptionHandler;

    @Bean
    public RouterFunction<ServerResponse> generalRoutes() {
        return route()
                    .path(API_V1, () ->
                            route()
                                    .path("/customers", this::customerRoutes)
                                    .path("/hello", this::helloRoutes)
                                    .build())
                    .build();
    }

    @Bean
    public RouterFunction<ServerResponse> customerRoutes() {
        return route()
                    .GET("", customerRequestHandler::getAllCustomers)
                    .GET("/paginated", customerRequestHandler::getPaginatedCustomers)
                    .GET("/{id}", customerRequestHandler::getCustomer)
                    .POST("", customerRequestHandler::postCustomer)
                    .PUT("/{id}", customerRequestHandler::putCustomer)
                    .DELETE("/{id}", customerRequestHandler::deleteCustomer)
                    .onError(CustomerNotFoundException.class, applicationExceptionHandler::handleException)
                    .onError(InvalidInputException.class, applicationExceptionHandler::handleException)
                    .filter(AuthenticationRequestFilter::validate)
                    .filter(AuthorizationRequestFilter::validate)
                    .build();

    }

    @Bean
    public RouterFunction<ServerResponse> helloRoutes() {
        return route()
                .GET("", helloRequestHandler::get)
                .build();
    }
}
