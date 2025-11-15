package com.webFlux.webFluxCassandra.service.serviceImpl;


import com.webFlux.webFluxCassandra.domain.Message;
import com.webFlux.webFluxCassandra.repository.MessageRepository;
import com.webFlux.webFluxCassandra.service.MessageService;
import com.webFlux.webFluxCassandra.utility.ReactiveErrorHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final ReactiveErrorHandler reactiveErrorHandler;

    @Override
    public Mono<Message> getById(UUID Id) {
        return messageRepository.findById(Id)
                .switchIfEmpty(Mono.empty())
                .onErrorResume(reactiveErrorHandler::handleMonoError);
    }

    @Override
    public Mono<Message> postMessage(Message message) {
        if (message == null || message.getData() == null || message.getData().isBlank()) {
            return Mono.error(new IllegalArgumentException("Uncorrected message"));
        }
        return messageRepository.save(message)
                .switchIfEmpty(Mono.empty())
                .onErrorResume(reactiveErrorHandler::handleMonoError);
    }

    @Override
    public Mono<Void> deleteById(UUID id) {
        if (id == null) {
            return Mono.error(new IllegalArgumentException("Uncorrected id"));
        }
        return messageRepository.deleteById(id)
                .onErrorResume(reactiveErrorHandler::handleMonoError);
    }

    public Flux<Message> postMessages(List<Message> messages) {
        return Flux.fromIterable(messages)
                .filter(message -> message.getData() != null)
                .collectList()
                .flatMapMany(validMessages -> {
                    if (validMessages.isEmpty()) {
                        return Flux.error(new IllegalArgumentException("No valid messages with non-null data"));
                    }
                    return messageRepository.saveAll(validMessages);
                })
                .onErrorResume(reactiveErrorHandler::handleFluxError);
    }

}
