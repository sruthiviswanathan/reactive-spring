package com.reactivespring.controller.v1;

import com.reactivespring.document.Item;
import com.reactivespring.repository.ItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    public Mono<Item> saveItem(@RequestBody Item item) {
        return itemRepository.save(item);
    }

    @PutMapping("/item/{id}")
    public Mono<Item> updateItem(@RequestBody Item item, @PathVariable String id) {
        Mono<Item> updatedItem = itemRepository.findById(id)
                .map(item1 -> {
                    item1.setPrice(item.getPrice());
                    item1.setDescription(item.getDescription());
                    return item1;
                })
                .flatMap(item1 -> itemRepository.save(item1));

        return updatedItem;
    }

    @DeleteMapping("/item/{id}")
    public Mono<Void> deleteItem(@PathVariable String id) {
        return itemRepository.findById(id)
                .flatMap(item1 -> itemRepository.deleteById(id));
    }
}
