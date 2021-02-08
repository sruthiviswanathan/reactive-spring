package com.reactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.util.retry.Retry;

import java.time.Duration;

public class FluxAndMonoErrorTest {

    @Test
    public void fluxErrorHandling() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception occurred")))
                .concatWith(Flux.just("D"))
                .onErrorResume((e) -> { // Executed on error and returns Flux
                    System.out.println(e);
                    return Flux.just("default", "default1");
                });

        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A", "B","C")
                .expectNext("default", "default1")
//                .expectError(RuntimeException.class)
//                .verify()
                .verifyComplete();
    }

    @Test
    public void fluxErrorHandlingOnErrorReturn() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception occurred")))
                .concatWith(Flux.just("D"))
                .onErrorReturn("default");

        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A", "B","C")
                .expectNext("default")
                .verifyComplete();
    }

    @Test
    public void fluxErrorHandlingOnErrorMap() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception occurred")))
                .concatWith(Flux.just("D"))
                .onErrorMap((e) -> {
                   return new CustomException(e);
                }).retry(2); // retries the operation again

        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A", "B","C")
                .expectNext("A", "B","C")
                .expectNext("A", "B","C")
                .expectError(CustomException.class)
                .verify();
    }

    @Test
    public void fluxErrorHandlingOnErrorMapWithRetryBackOff() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception occurred")))
                .concatWith(Flux.just("D"))
                .onErrorMap((e) -> {
                    return new CustomException(e);
                })
                .retryWhen(
                        Retry
                                .backoff(2, Duration.ofSeconds(5))
                                .onRetryExhaustedThrow((spec, rs) -> rs.failure())
                );

        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A", "B","C")
                .expectNext("A", "B","C")
                .expectNext("A", "B","C")
                .expectError(CustomException.class)
                .verify();
    }
}
