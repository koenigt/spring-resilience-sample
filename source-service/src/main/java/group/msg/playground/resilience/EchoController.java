package group.msg.playground.resilience;

import java.util.Random;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * <p>
 * This Controller is simply returning an echo string and should be unsecured so it helps debugging environment or
 * connectivity issues which might be hard when user/pwd are needed everywhere.
 * <p>
 * <ul>
 * <li>see: https://resilience4j.readme.io/docs/circuitbreaker
 * <li>see: https://medium.com/@knoldus/spring-boot-combining-mono-s-358b83b7485a
 * <li>see: https://www.javaadvent.com/2020/12/resilient-applications-spring-resilience4j.html
 * </ul>
 */
@RestController
@Slf4j
@RequiredArgsConstructor
/**
 * ToDo: Check TimeLimiter with Mono ToDo: Check CircuitBreaker on ClassLevel
 */
public class EchoController {

    private final WebClient targetServiceClient;

    Random rand = new Random(635527399738L);

    @GetMapping(produces = "application/json")
    @Retry(name = "backendA")
    @TimeLimiter(name = "backendA", fallbackMethod = "getTimeoutFallback")
    public Mono<String> getTriggerTargetService() {
        log.debug("calling the TriggerTargetService");
        Mono<String> targetResponseBody = targetServiceClient.get()
                .uri(uriBuilder -> uriBuilder.queryParam("duration", rand.nextInt(5000)).build()).retrieve()
                // .bodyToMono(String.class).timeout(Duration.ofSeconds(2)).block();
                .bodyToMono(String.class);
        return Mono.just("{\"echo\":\"source server\"}").zipWith(targetResponseBody).map(tuple2 -> {
            return "[" + tuple2.getT1() + ", " + tuple2.getT2() + "]";
        });
        // return "[{\"echo\":\"source server\"}, " + targetResponseBody + "]";
    }

    @GetMapping(produces = "application/json", path = "/echoError")
    @Retry(name = "backendA", fallbackMethod = "getTriggerTargetServiceErrorFallback")
    @CircuitBreaker(name = "backendA")
    public String getTriggerTargetServiceError() {
        log.debug("calling the TriggerTargetService");
        String targetResponseBody = targetServiceClient.get()
                .uri(uriBuilder -> uriBuilder.path("/echoError").queryParam("resCode", 500).build()).retrieve()
                .bodyToMono(String.class).block();

        return "[{\"echo\":\"source server\"}, " + targetResponseBody + "]";
    }

    public String getTriggerTargetServiceErrorFallback(RuntimeException ex) {
        log.debug("calling the TriggerTargetServiceErrorFallback");

        return "{\"echo\":\"source server fallback\"}";
    }

    public Mono<String> getTimeoutFallback(Throwable t) {
        log.debug("calling the getTimeoutFallback");
        t.printStackTrace();
        return Mono.just("{\"echo\":\"source server fallback\"}");
    }
}
