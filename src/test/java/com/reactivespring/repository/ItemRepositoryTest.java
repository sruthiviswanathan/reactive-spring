package com.reactivespring.repository;

import com.reactivespring.document.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@ExtendWith(SpringExtension.class)
@DirtiesContext
public class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    List<Item> itemList = Arrays.asList(
            Item.builder().description("TV").price(300.00).build(),
            Item.builder().description("Fridge").price(400.00).build(),
            Item.builder().description("Washer").price(600.00).build(),
            Item.builder().id("WACT123").description("Watch").price(600.00).build()
    );

    @BeforeEach
    public void setUp() {
        itemRepository.deleteAll()
                .thenMany(Flux.fromIterable(itemList))
                .flatMap(itemRepository::save)
                .doOnNext(item -> System.out.println("Saved Item: " + item))
                .blockLast(); // waits for all the above operations to complete
    }

    @Test
    public void getAllItems() {
        StepVerifier.create(itemRepository.findAll())
                .expectSubscription()
                .expectNextCount(4)
                .verifyComplete();
    }

    @Test
    public void getItemById() {
        String expectedItemDescription = "Watch";
        StepVerifier.create(itemRepository.findById("WACT123"))
                .expectSubscription()
//                .expectNextMatches(response -> response.getDescription().equals(expectedItemDescription))
                .consumeNextWith(response -> assertEquals(expectedItemDescription, response.getDescription()))
                .verifyComplete();

    }

    @Test
    public void getItemByDescription() {
        StepVerifier.create(itemRepository.findByDescription("Washer").log("findItemByDescription: "))
                .expectSubscription()
                .expectNextMatches(response -> response.getDescription().equals("Washer"))
                .verifyComplete();
    }

    @Test
    public void saveItem() {
        // .save return Mono
        StepVerifier.create(itemRepository.save(Item.builder().description("Headphones").price(450.00).build()))
                .expectSubscription()
                .consumeNextWith(response -> System.out.println("Saved item: " + response.getDescription()))
                .verifyComplete();
    }

    @Test
    public void updateItemByDoublingPrice() {

        Flux<Item> updatedItem = itemRepository.findByDescription("Fridge")
                .map(item -> {
                    item.setPrice(item.getPrice() * 2); // Updating price
                    return item;
                })
                .flatMap(item -> itemRepository.save(item)); // Updating item

        StepVerifier.create(updatedItem)
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();

    }

    @Test
    public void deleteItemById() {
        StepVerifier.create(itemRepository.deleteById("WACT123"))
                .expectSubscription()
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    public void deleteItem() {
        Mono<Item> itemToBeDeleted = itemRepository.findById("WACT123");
        StepVerifier.create(itemRepository.delete(itemToBeDeleted.log().block()))
                .expectSubscription()
                .expectNextCount(0)
                .verifyComplete();
    }

}