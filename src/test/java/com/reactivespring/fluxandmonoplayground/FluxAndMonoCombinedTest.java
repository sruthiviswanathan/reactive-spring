package com.reactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class FluxAndMonoCombinedTest {

    @Test
    public void combineUsingMerge() {
        Flux<String> flux1 = Flux.just("A", "B", "C");
        Flux<String> flux2 = Flux.just("D", "E", "F");

        Flux<String> mergedFlux = Flux.merge(flux1, flux2);

        StepVerifier.create(mergedFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    public void combineUsingMergeWithDelay() {
        Flux<String> flux1 = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
        Flux<String> flux2 = Flux.just("D", "E", "F").delayElements(Duration.ofSeconds(1));

        Flux<String> mergedFlux = Flux.merge(flux1, flux2); // Merge doesn't take order. Takes flux2 before flux1 completes

        StepVerifier.create(mergedFlux.log())
                .expectSubscription()
                .expectNextCount(6)
                .verifyComplete();
    }

    @Test
    public void combineUsingConcatWithDelay() {
        Flux<String> flux1 = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
        Flux<String> flux2 = Flux.just("D", "E", "F").delayElements(Duration.ofSeconds(1));

        Flux<String> mergedFlux = Flux.concat(flux1, flux2); // Concat maintains order but it waits for flux1 to complete

        StepVerifier.create(mergedFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    public void combineUsingZipWithDelay() {
        Flux<String> flux1 = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
        Flux<String> flux2 = Flux.just("D", "E", "F").delayElements(Duration.ofSeconds(1));

        Flux<String> mergedFlux = Flux.zip(flux1, flux2, (t1, t2) -> {
            return t1.concat(t2);
        }); // A,D: B,E: C,F

        StepVerifier.create(mergedFlux.log())
                .expectSubscription()
                .expectNext("AD", "BE", "CF")
                .verifyComplete();
    }
}
