package com.reactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FluxAndMonoBPTest {

    @Test
    public void backPressureTest() {
        Flux<Integer> integerFlux = Flux.range(1,10);

        StepVerifier.create(integerFlux.log())
                .expectSubscription()
                .thenRequest(2) // Request flux to give only 2 elements
                .expectNext(1,2) // Expected value
                .thenRequest(2)
                .expectNext(3,4)
                .thenCancel()
                .verify();
    }

    @Test
    public void backPressure() {
        Flux<Integer> integerFlux = Flux.range(1,10).log();

        integerFlux.subscribe((element) -> System.out.println("Element is : " +  element)
                , (e) -> System.out.println("Exception is: " + e)
                , () -> System.out.println("Done")
                , (subscription) -> subscription.request(2));
    }

    @Test
    public void backPressureCancel() {
        Flux<Integer> integerFlux = Flux.range(1,10).log();

        integerFlux.subscribe((element) -> System.out.println("Element is : " +  element)
                , (e) -> System.out.println("Exception is: " + e)
                , () -> System.out.println("Done")
                , (subscription) -> subscription.cancel());
    }

    @Test
    public void backPressureRequestCancel() {
        Flux<Integer> integerFlux = Flux.range(1,10).log();

        integerFlux.subscribe(new BaseSubscriber<Integer>() {
            @Override
            protected void hookOnNext(Integer value) {
                request(1);
                System.out.println("Value is : " + value);
                if(value == 4) {
                    cancel();
                }
            }
        });
    }
}
