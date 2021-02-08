package com.reactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static reactor.core.scheduler.Schedulers.parallel;

public class FluxAndMonoTransformTest {

    List<String> names = Arrays.asList("Adam", "Anna", "Jack", "Jennie");

    @Test
    public void transformUsingMap() {
        Flux<String> namesFlux = Flux.fromIterable(names)
                .map(name -> name.toUpperCase())
                .log();

        StepVerifier.create(namesFlux)
                .expectNext("ADAM", "ANNA", "JACK", "JENNIE")
                .verifyComplete();
    }

    @Test
    public void transformUsingMapLength() {
        Flux<Integer> namesFlux = Flux.fromIterable(names)
                .map(name -> name.length())
                .log();

        StepVerifier.create(namesFlux)
                .expectNext(4, 4, 4, 6)
                .verifyComplete();
    }

    @Test
    public void transformUsingMapRepeat() {
        Flux<String> namesFlux = Flux.fromIterable(names)
                .map(name -> name.toUpperCase())
                .repeat(1)
                .log();

        StepVerifier.create(namesFlux)
                .expectNext("ADAM", "ANNA", "JACK", "JENNIE", "ADAM", "ANNA", "JACK", "JENNIE")
                .verifyComplete();
    }

    @Test
    public void transformUsingMapFilter() {
        Flux<String> namesFlux = Flux.fromIterable(names)
                .filter(name -> name.length() > 4)
                .map(name -> name.toUpperCase())
                .repeat(1)
                .log();

        StepVerifier.create(namesFlux)
                .expectNext("JENNIE", "JENNIE")
                .verifyComplete();
    }

    @Test
    public void transformUsingFlatMap() {

        Flux<String> namesFlux = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F"))
                .flatMap(s -> {
                    return Flux.fromIterable(convertToList(s)); // A -> List[A, new value]
                }).log();  // DB or external service call that returns a flux -> s -> Flux<String>

        StepVerifier.create(namesFlux)
                .expectNextCount(12)
                .verifyComplete();
    }

    @Test
    public void transformUsingFlatMapUsingParallel() {

        Flux<String> namesFlux = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F"))
                .window(2)
                .flatMap((s) ->
                        s.map(this::convertToList).subscribeOn(parallel())) // Flux<List<String>>
                        .flatMap(s -> Flux.fromIterable(s))
                .log();

        StepVerifier.create(namesFlux)
                .expectNextCount(12)
                .verifyComplete();
    }

    @Test
    public void transformUsingFlatMapUsingParallelMaintainOrder() {

        Flux<String> namesFlux = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F"))
                .window(2)
//                .concatMap((s) ->
//                        s.map(this::convertToList).subscribeOn(parallel())) // Flux<List<String>>
                .flatMapSequential((s) ->
                        s.map(this::convertToList).subscribeOn(parallel())) // Flux<List<String>>
                .flatMap(s -> Flux.fromIterable(s))
                .log();

        StepVerifier.create(namesFlux)
                .expectNextCount(12)
                .verifyComplete();
    }

    private List<String> convertToList(String s) {

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Arrays.asList(s, "new value");
    }

}
