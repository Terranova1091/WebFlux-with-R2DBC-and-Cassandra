package com.webFlux.SpringWebFlux.config;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.core.instrument.binder.system.UptimeMetrics;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class MetricsConfig {

    /**
     * Регистрирует системные и JVM метрики + собственные Gauge для потоков.
     */
    @Bean
    public MeterBinder systemMetrics() {
        return registry -> {
            // Встроенные метрики JVM и системы
            new ClassLoaderMetrics().bindTo(registry);
            new JvmMemoryMetrics().bindTo(registry);
            new JvmGcMetrics().bindTo(registry);
            new JvmThreadMetrics().bindTo(registry);
            new ProcessorMetrics().bindTo(registry);
            new UptimeMetrics().bindTo(registry);

            // Дополнительные метрики потоков
            ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();

            Gauge.builder("app.threads.live", threadBean, ThreadMXBean::getThreadCount)
                    .description("Current live threads")
                    .tag("type", "total")
                    .register(registry);

            Gauge.builder("app.threads.live", threadBean, ThreadMXBean::getDaemonThreadCount)
                    .description("Current daemon threads")
                    .tag("type", "daemon")
                    .register(registry);

            Gauge.builder("app.threads.peak", threadBean, ThreadMXBean::getPeakThreadCount)
                    .description("Peak thread count")
                    .register(registry);
        };
    }

    /**
     * Пользовательский счётчик для активных запросов.
     */
    @Bean
    public AtomicInteger activeRequestsCounter(MeterRegistry registry) {
        AtomicInteger counter = new AtomicInteger(0);
        Gauge.builder("app.requests.active", counter, AtomicInteger::get)
                .description("Number of active requests")
                .register(registry);
        return counter;
    }

    /**
     * Пользовательский счётчик общего количества обработанных сообщений.
     */
    @Bean
    public AtomicInteger totalMessagesCounter(MeterRegistry registry) {
        AtomicInteger counter = new AtomicInteger(0);
        Gauge.builder("app.messages.total", counter, AtomicInteger::get)
                .description("Total processed messages")
                .register(registry);
        return counter;
    }
}
