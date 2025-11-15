package com.webFlux.webFluxCassandra.controller;


import com.webFlux.webFluxCassandra.domain.Message;
import com.webFlux.webFluxCassandra.service.MessageService;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cassandra")
public class MessageController {

    private final MessageService messageService;

    @GetMapping("/findById/{Id}")
    @Timed(value = "app.operation.duration", extraTags = {"type", "get_one"})
    @Counted(value = "app.operation.count", extraTags = {"type", "get_one"})
    Mono<Message> getById(@PathVariable UUID Id) {
        return messageService.getById(Id);
    }

    @PostMapping("/postMessage")
    @Timed(value = "app.operation.duration", extraTags = {"type", "post_one"})
    @Counted(value = "app.operation.count", extraTags = {"type", "post_one"})
    Mono<Message> postMessage(@RequestBody Message message) {
        return messageService.postMessage(message);
    }

    @DeleteMapping("/delete/{id}")
    @Timed(value = "app.operation.duration", extraTags = {"type", "delete_one"})
    @Counted(value = "app.operation.count", extraTags = {"type", "delete_one"})
    Mono<Void> delete(@PathVariable UUID id) {
        return messageService.deleteById(id);
    }

    @PostMapping("/postMessages")
    @Timed(value = "app.operation.duration", extraTags = {"type", "post_many"})
    @Counted(value = "app.operation.count", extraTags = {"type", "post_many"})
    Flux<Message> postMessages(@RequestBody List<Message> messages) {
        return messageService.postMessages(messages);
    }

}
