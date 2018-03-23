package com.myke.hello.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class MyHelloHealth implements HealthIndicator {
    @Override
    public Health health() {
        return Health.up().withDetail("zhang", "keke").build();
    }
}
