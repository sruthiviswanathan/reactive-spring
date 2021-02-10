package com.reactivespring.router;

import com.reactivespring.handler.ItemsHandlerFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class ItemsRouter {

    @Bean
    public RouterFunction<ServerResponse> itemsRoute(ItemsHandlerFunction itemsHandlerFunction) {
        return RouterFunctions
                .route(GET("/func/items").and(accept(MediaType.APPLICATION_JSON)), itemsHandlerFunction::getAllItems)
                .andRoute(GET("/func/item/{id}").and(accept(MediaType.APPLICATION_JSON)), itemsHandlerFunction::getItemById)
                .andRoute(POST("/func/item").and(accept(MediaType.APPLICATION_JSON)), itemsHandlerFunction::saveItem)
                .andRoute(DELETE("/func/item/{id}").and(accept(MediaType.APPLICATION_JSON)), itemsHandlerFunction::deleteItem)
                .andRoute(PUT("/func/item/{id}").and(accept(MediaType.APPLICATION_JSON)), itemsHandlerFunction::updateItem);
    }

    @Bean
    public RouterFunction<ServerResponse> errorRoutes(ItemsHandlerFunction itemsHandlerFunction) {
        return RouterFunctions
                .route(GET("/func/runtimeException")
                        .and(accept(MediaType.APPLICATION_JSON)), itemsHandlerFunction::runTimeException);
    }
}
