package com.webFlux.SpringWebFlux.controller;

import com.webFlux.SpringWebFlux.domain.Message;
import com.webFlux.SpringWebFlux.service.MessageService;
import com.webFlux.SpringWebFlux.service.MetricsService;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/message")
public class MessageController {

    private final MessageService messageService;
    private final MetricsService metricsService;

    private static final String LOG_START_OPERATION = "Starting {} operation";
    private static final String LOG_COMPLETE_OPERATION = "Completed {} operation";

    @PostConstruct
    public void init() {
        metricsService.initializeCustomMetrics();
    }

    private Flux<Message> trackFlux(Flux<Message> flux, String operationType) {
        logOperationStart(operationType);
        metricsService.incrementCounter("app.operations.total", "type", operationType);

        return flux
                .doOnNext(message -> metricsService.incrementMessageCount())
                .doOnComplete(() -> logOperationComplete(operationType))
                .doOnError(error -> {
                    metricsService.incrementCounter("app.operations.errors", "type", operationType);
                    log.error("Error in {} operation", operationType, error);
                });
    }

    private <T> Mono<T> trackMono(Mono<T> mono, String operationType) {
        logOperationStart(operationType);
        metricsService.incrementCounter("app.operations.total", "type", operationType);

        return mono
                .doOnSuccess(result -> logOperationComplete(operationType))
                .doOnError(error -> {
                    metricsService.incrementCounter("app.operations.errors", "type", operationType);
                    log.error("Error in {} operation", operationType, error);
                });
    }

    @GetMapping("/getList")
    @Timed(value = "app.operation.duration", extraTags = {"type", "get_list"})
    @Counted(value = "app.operation.count", extraTags = {"type", "get_list"})
    public Flux<Message> list() {
        return trackFlux(messageService.list(), "get_list");
    }

    @GetMapping("/getOneMessage")
    @Timed(value = "app.operation.duration", extraTags = {"type", "get_one"})
    @Counted(value = "app.operation.count", extraTags = {"type", "get_one"})
    public Mono<ResponseEntity<Message>> getOne(@RequestParam Long id) {
        return trackMono(
                messageService.getOne(id)
                        .map(ResponseEntity::ok)
                        .defaultIfEmpty(ResponseEntity.notFound().build()),
                "get_one"
        );
    }

    @PostMapping("/postMessage")
    @Timed(value = "app.operation.duration", extraTags = {"type", "create"})
    @Counted(value = "app.operation.count", extraTags = {"type", "create"})
    public Mono<ResponseEntity<Message>> addOne(@RequestBody Message message) {
        return trackMono(
                messageService.addOne(message)
                        .doOnNext(msg -> metricsService.incrementMessageCount())
                        .map(ResponseEntity::ok),
                "create"
        );
    }

    @GetMapping("/getMessageByContaining")
    @Timed(value = "app.operation.duration", extraTags = {"type", "search"})
    @Counted(value = "app.operation.count", extraTags = {"type", "search"})
    public Flux<Message> findByContentContaining(@RequestParam String keyword) {
        return trackFlux(messageService.searchByContent(keyword), "search");
    }

    @DeleteMapping("/delete/{id}")
    @Timed(value = "app.operation.duration", extraTags = {"type", "delete"})
    @Counted(value = "app.operation.count", extraTags = {"type", "delete"})
    public Mono<Void> delete(@PathVariable Long id) {
        return messageService.deleteById(id);
    }
    private void logOperationStart(String operationType) {
        log.info(LOG_START_OPERATION, operationType);
    }

    private void logOperationComplete(String operationType) {
        log.info(LOG_COMPLETE_OPERATION, operationType);
    }
}
