package com.webFlux.webFluxCassandra.service;

import com.webFlux.webFluxCassandra.domain.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    Mono<Message> getById(UUID Id);

    Mono<Message> postMessage(Message message);

    Mono<Void> deleteById(UUID id);

    Flux<Message> postMessages(List<Message> messages);

}
