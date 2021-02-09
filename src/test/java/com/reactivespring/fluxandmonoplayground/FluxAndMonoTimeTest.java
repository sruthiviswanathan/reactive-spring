package com.reactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class FluxAndMonoTimeTest {

    @Test
    public void infiniteSequence() throws InterruptedException {
        Flux<Long> infiniteFlux = Flux.interval(Duration.ofMillis(200)).log(); // start from 0 -> ...
        infiniteFlux.subscribe((element) -> System.out.println("Value is : " + element));
        Thread.sleep(3000);
    }

    @Test
    public void finiteSequenceTest() {
        Flux<Long> finiteFlux = Flux.interval(Duration.ofMillis(200)) // start from 0 -> ...
                .take(3)
                .log();

        StepVerifier.create(finiteFlux)
                .expectSubscription()
                .expectNext(0L, 1L, 2L)
                .verifyComplete();
    }

    @Test
    public void finiteSequenceMapTest() {
        Flux<Integer> finiteFlux = Flux.interval(Duration.ofMillis(200)) // start from 0 -> ...
                .map(value -> value.intValue())
                .take(3)
                .log();

        StepVerifier.create(finiteFlux)
                .expectSubscription()
                .expectNext(0, 1, 2)
                .verifyComplete();
    }

    @Test
    public void finiteSequenceMapTestWithDelay() {
        Flux<Integer> finiteFlux = Flux.interval(Duration.ofMillis(200)) // start from 0 -> ...
                .delayElements(Duration.ofSeconds(1))
                .map(value -> value.intValue())
                .take(3)
                .log();

        StepVerifier.create(finiteFlux)
                .expectSubscription()
                .expectNext(0, 1, 2)
                .verifyComplete();
    }
}
