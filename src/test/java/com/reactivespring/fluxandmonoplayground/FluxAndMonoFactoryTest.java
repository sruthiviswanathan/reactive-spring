package com.reactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class FluxAndMonoFactoryTest {

    List<String> names = Arrays.asList("Adam", "Anna", "Jack", "Jennie");

    // Factory methods for flux
    // 1. fromIterable()
    // 2. fromArray()
    // 3. fromStream()
    // 4. range()

    @Test
    public void fluxUsingIterable() {
        Flux<String> namesFlux = Flux.fromIterable(names);
        StepVerifier.create(namesFlux.log())
                .expectNext("Adam", "Anna", "Jack", "Jennie")
                .verifyComplete();
    }

    @Test
    public void fluxUsingArray() {
        String[] names = new String[]{"Adam", "Anna", "Jack", "Jennie"};

        Flux<String> stringArrayFlux = Flux.fromArray(names);
        StepVerifier.create(stringArrayFlux.log())
                .expectNext("Adam", "Anna", "Jack", "Jennie")
                .verifyComplete();
    }

    @Test
    public void fluxUsingStream() {
        Flux<String> namesFlux = Flux.fromStream(names.stream());

        StepVerifier.create(namesFlux.log())
                .expectNext("Adam", "Anna", "Jack", "Jennie")
                .verifyComplete();
    }

    @Test
    public void FluxUsingRange() {
        Flux<Integer> integerFlux = Flux.range(1, 5);

        StepVerifier.create(integerFlux.log())
                .expectNextCount(5)
                .verifyComplete();
    }


    // Factory methods for Mono
    // 1. justOrEmpty()
    // 2. fromSupplier()
    // 3.

    @Test
    public void monoUsingJustOrEmpty() {
        // Pass valid or null value
        Mono<String> emptyMono = Mono.justOrEmpty(null); // Output: Mono.Empty();
        StepVerifier.create(emptyMono.log())
                .verifyComplete(); // Cannot expectNext on empty Mono
    }

    @Test
    public void monoUsingSupplier() {
        // Supplier -
        Supplier<String> stringSupplier = () -> "Adam";
        Mono<String> stringMono = Mono.fromSupplier(stringSupplier);

        System.out.println(stringSupplier.get()); // Get the value from the supplier

        StepVerifier.create(stringMono.log())
                .expectNext("Adam")
                .verifyComplete();

    }

}
