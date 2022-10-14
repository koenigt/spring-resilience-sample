package group.msg.playground.resilience;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class BackendCaller {

    @Autowired
    private WebClient targetServiceClientPlain;

    @TimeLimiter(name = "backendA", fallbackMethod = "healthTimeout")
    public Mono<Health> checkBackendA(int delay) {
        return targetServiceClientPlain.get().uri(uriBuilder -> uriBuilder.queryParam("duration", delay).build())
                .exchangeToMono((response) -> {
                    Health.Builder builder;
                    if (response.statusCode() == HttpStatus.OK) {
                        builder = Health.up();
                    } else {
                        builder = Health.down();
                    }
                    builder.withDetail("responseCode", response.statusCode().value());
                    return Mono.just(builder.build());
                }).doOnError(t-> t.printStackTrace()).onErrorReturn(Health.down().withDetail("reason", "onErrorReturn").build());
    }

    public Mono<Health> healthTimeout(int delay, Throwable t) {
        log.debug("delay:" + delay);
        t.printStackTrace();
        return Mono.just(Health.down().withDetail("reason", "healthTimeout").build());
    }

}
