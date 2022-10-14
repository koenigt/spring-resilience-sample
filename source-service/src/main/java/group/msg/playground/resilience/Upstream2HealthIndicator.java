package group.msg.playground.resilience;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

@Component
public class Upstream2HealthIndicator implements ReactiveHealthIndicator {

    @Autowired
    private BackendCaller backendCaller;

    Random rand = new Random(635527399738L);

    @Override
    public Mono<Health> health() {
        return backendCaller.checkBackendA(rand.nextInt(7500));
    }

}
