package com.reactivespring.controller.v1;

import com.reactivespring.document.Item;
import com.reactivespring.repository.ItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
public class ItemController {

    @Autowired
    ItemRepository itemRepository;

    @GetMapping("/items")
    public Flux<Item> getAllItems() {
        return itemRepository.findAll();
    }

    @PostMapping("/item")
    public Mono<ResponseEntity<Item>> saveItem(@RequestBody Item item) {
        Mono<Item> savedItem = itemRepository.save(item);
        return Mono.just(new ResponseEntity(savedItem, HttpStatus.CREATED));
    }

    @GetMapping("/item/{id}")
    public Mono<ResponseEntity<Item>> getItemById(@PathVariable String id) {
        return itemRepository.findById(id)
                .map((item) -> new ResponseEntity<>(item, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/item/{id}")
    public Mono<ResponseEntity<Item>> updateItem(@RequestBody Item item, @PathVariable String id) {
        return itemRepository.findById(id)
                .flatMap(item1 -> {
                    item1.setPrice(item.getPrice());
                    item1.setDescription(item.getDescription());
                    return itemRepository.save(item1);
                })
                .map(updatedItem -> new ResponseEntity<>(updatedItem, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/item/{id}")
    public Mono<Void> deleteItem(@PathVariable String id) {
        return itemRepository.deleteById(id);
    }

    @GetMapping("/items/runtimeException")
    public Flux<Item> runTimeException() {
        return itemRepository.findAll()
                .concatWith(Mono.error(new RuntimeException("Runtime exception occurred.")));
    }
}
