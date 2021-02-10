package com.reactivespring.handler;

import com.mongodb.internal.connection.Server;
import com.reactivespring.document.Item;
import com.reactivespring.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;
import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@Component
public class ItemsHandlerFunction {

    @Autowired
    ItemRepository itemRepository;

    static Mono<ServerResponse> notFound = ServerResponse.notFound().build();

    public Mono<ServerResponse> getAllItems(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemRepository.findAll(), Item.class);
    }

    public Mono<ServerResponse> getItemById(ServerRequest serverRequest) {

        Mono<Item> itemMono = itemRepository.findById(serverRequest.pathVariable("id"));

        return itemMono.flatMap(item ->
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(item)))
                .switchIfEmpty(notFound);
    }

    public Mono<ServerResponse> saveItem(ServerRequest serverRequest) {
       Mono<Item> itemToBeInserted = serverRequest.bodyToMono(Item.class);
       return itemToBeInserted.flatMap(item ->
               ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(itemRepository.save(item), Item.class));
    }

    public Mono<ServerResponse> deleteItem(ServerRequest serverRequest) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemRepository.deleteById(serverRequest.pathVariable("id")), Item.class);
    }

    public Mono<ServerResponse> updateItem(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");

        Mono<Item> finalUpdatedItem = serverRequest.bodyToMono(Item.class).flatMap(item -> {
            Mono<Item> updatedItem =  itemRepository.findById(id).flatMap(oldItem -> {
               oldItem.setPrice(item.getPrice());
               oldItem.setDescription(item.getDescription());
               return itemRepository.save(oldItem);
           });
           return updatedItem;
       });
        return finalUpdatedItem.flatMap(item ->
                ServerResponse
                    .ok()
                    .body(fromValue(item)))
                    .switchIfEmpty(notFound);

    }

}
