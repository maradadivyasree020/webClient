package webClient.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.test.StepVerifier;
import webClient.controller.FluxAndMonoController;

@WebFluxTest(controllers = FluxAndMonoController.class)
@AutoConfigureWebTestClient
public class FluxandMonoControllerTest {
    
    @Autowired
    WebTestClient webTestClient;
 
    @Test
    void flux(){
        webTestClient.get()
                     .uri("/flux")
                     .exchange()
                     .expectStatus()
                     .is2xxSuccessful()
                     .expectBodyList(Integer.class)
                     .hasSize(3);
    }

    @Test
    void flux_approach1(){
        var flux = webTestClient.get()
                                .uri("/flux")
                                .exchange()
                                .expectStatus()
                                .is2xxSuccessful()
                                .returnResult(Integer.class)
                                .getResponseBody();

        StepVerifier.create(flux)
                    .expectNext(1,2,3)
                    .verifyComplete();
    }

    @Test
    void flux_approach3(){
        webTestClient.get()
                     .uri("/flux")
                     .exchange()
                     .expectStatus()
                     .is2xxSuccessful()
                     .expectBodyList(Integer.class)
                     .consumeWith(listEntityExchangeResult ->{
                        var responseBody = listEntityExchangeResult.getResponseBody();
                        assert responseBody != null;
                        assert responseBody.size() == 3;
                     });
    }

    @Test
    void mono(){
        webTestClient.get()
                     .uri("/mono")
                     .exchange()
                     .expectStatus()
                     .is2xxSuccessful()
                     .expectBody(String.class)
                     .consumeWith(body -> {
                        var responseBody = body.getResponseBody();
                        assertEquals("hello", responseBody);
                     });
    }

    @Test
    void stream(){
        var flux = webTestClient.get()
                                .uri("/stream")
                                .exchange()
                                .expectStatus()
                                .is2xxSuccessful()
                                .returnResult(Long.class)
                                .getResponseBody();
        StepVerifier.create(flux)
                    .expectNext(0L,1L)
                    .thenCancel()
                    .verify();
    }
}
