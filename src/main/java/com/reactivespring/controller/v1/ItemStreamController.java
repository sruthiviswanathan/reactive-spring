package com.reactivespring.controller.v1;

import com.reactivespring.document.ItemCapped;
import com.reactivespring.repository.ItemCappedRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@Slf4j
public class ItemStreamController {

    @Autowired
    ItemCappedRepository itemCappedRepository;

//    @GetMapping(value = "/stream/items", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<ItemCapped> getItemStream() {
        // Endpoint wont work since data loader is not present and no data is added to itemCappedRepository
        return itemCappedRepository.findItemsBy();
    }
}
