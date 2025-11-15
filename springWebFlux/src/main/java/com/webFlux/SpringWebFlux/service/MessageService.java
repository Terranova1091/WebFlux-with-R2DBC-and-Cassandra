package com.webFlux.SpringWebFlux.service;

import com.webFlux.SpringWebFlux.domain.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface MessageService {

    Flux<Message> list();

    Mono<Message> getOne(Long id);

    Mono<Message> addOne(Message message);

    Flux<Message> searchByContent(String keyword);

    Mono<Void> deleteById(Long id);

}
