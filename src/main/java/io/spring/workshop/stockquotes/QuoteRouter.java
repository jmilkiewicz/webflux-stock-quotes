package io.spring.workshop.stockquotes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.*;

@Configuration
public class QuoteRouter {
    @Autowired
    private QuoteHandler quoteHandler;

    @Bean
    public RouterFunction<ServerResponse> createOne() {
        return RouterFunctions
                .route(RequestPredicates.GET("/hello"), quoteHandler::hello)
                .andRoute(RequestPredicates.GET("/helloStream"), quoteHandler::helloStream)
                .andRoute(RequestPredicates.GET("/helloStreamInit"), quoteHandler::helloStreamInit)
                .andRoute(RequestPredicates.POST("/echo"), quoteHandler::echo)
                .andRoute(RequestPredicates.GET("/quotes"), quoteHandler::quotes)
                .andRoute(RequestPredicates.GET("/initialQuotes"), quoteHandler::initialQuotes);
    }
}
