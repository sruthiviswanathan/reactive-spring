package com.reactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class FluxAndMonoTest {


    @Test
    public void fluxTest() {
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                .concatWith(Flux.error(new RuntimeException("Exception occurred")))  // Attaching error with the flux
                .concatWith(Flux.just("After error")) // Trying to send after error is sent - Flux do not send it
                .log();

        stringFlux.subscribe(System.out::println,
                (error) -> { // Handling error with the subscribe method
                    System.err.println("Exception is: " + error);
                },
                () -> {
                    System.out.println("Completed"); // Handling completion of event
                });
    }


    @Test
    public void fluxTestElementsWithoutError() {
        Flux<String> stringFlux =  Flux.just("Spring", "Spring Boot", "Reactive Spring").log();

        StepVerifier.create(stringFlux)
                .expectNext("Spring")
                .expectNext("Spring Boot")
                .expectNext("Reactive Spring")
                .verifyComplete(); // Starts the flow of flux to subscriber
    }

    @Test
    public void fluxTestElementsWithError() {
        Flux<String> stringFlux =  Flux.just("Spring", "Spring Boot", "Reactive Spring")
                .concatWith(Flux.error(new RuntimeException("Exception occurred")))
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("Spring")
                .expectNext("Spring Boot")
                .expectNext("Reactive Spring")
//              .expectNext("Spring", "Spring Boot", "Reactive Spring") // Alternative syntax for the above 3 lines
                .expectErrorMessage("Exception occurred") // Verify the error message occurred
//              .expectError(RuntimeException.class) // Verify the error occurred
                .verify();
    }

    @Test
    public void fluxTestElementsCount() {
        Flux<String> stringFlux =  Flux.just("Spring", "Spring Boot", "Reactive Spring")
                .log();

        StepVerifier.create(stringFlux)
                .expectNextCount(3)
                .verifyComplete();
    }


    @Test
    public void MonoTest() {
        Mono<String> stringMono = Mono.just("Spring");

        StepVerifier.create(stringMono.log())
                .expectNext("Spring")
                .verifyComplete();
    }

    @Test
    public void MonoTestWithError() {

        StepVerifier.create(Mono.error(new RuntimeException("Exception Occurred")).log())
                .expectError(RuntimeException.class)
                .verify();
    }

}
