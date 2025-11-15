package com.webFlux.SpringWebFlux.service.serviceImpl;

import com.webFlux.SpringWebFlux.service.MetricsService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class MetricsServiceImpl implements MetricsService {

    @Getter
    private final MeterRegistry meterRegistry;
    private final AtomicInteger activeRequestsCounter;
    private final AtomicInteger totalMessagesCounter;

    private final ConcurrentHashMap<String, Counter> counters = new ConcurrentHashMap<>();

    public void initializeCustomMetrics() {
        Gauge.builder("app.requests.active", activeRequestsCounter, AtomicInteger::get)
                .description("Number of active requests")
                .register(meterRegistry);

        Gauge.builder("app.messages.total", totalMessagesCounter, AtomicInteger::get)
                .description("Total number of messages processed")
                .register(meterRegistry);

        registerOperationCounter("get_list");
        registerOperationCounter("get_one");
        registerOperationCounter("create");
        registerOperationCounter("search");
    }

    private void registerOperationCounter(String type) {
        counters.putIfAbsent(type,
                Counter.builder("app.operations.total")
                        .description("Total operations by type")
                        .tag("type", type)
                        .register(meterRegistry)
        );
    }

    public void incrementCounter(String name, String tagKey, String tagValue) {
        String key = name + ":" + tagKey + ":" + tagValue;
        counters.computeIfAbsent(key, k ->
                Counter.builder(name)
                        .tag(tagKey, tagValue)
                        .register(meterRegistry)
        ).increment();
    }

    public void incrementMessageCount() {
        totalMessagesCounter.incrementAndGet();
    }

}
