package com.reactivespring.repository;

import com.reactivespring.document.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@ExtendWith(SpringExtension.class)
public class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    List<Item> itemList = Arrays.asList(
            Item.builder().description("TV").price(300.00).build(),
            Item.builder().description("Fridge").price(400.00).build(),
            Item.builder().description("Washer").price(600.00).build()
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
                .expectNextCount(3)
                .verifyComplete();
    }

}