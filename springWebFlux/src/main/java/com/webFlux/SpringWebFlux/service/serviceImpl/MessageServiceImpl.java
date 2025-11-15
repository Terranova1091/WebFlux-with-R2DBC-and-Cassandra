package com.webFlux.SpringWebFlux.service.serviceImpl;

import com.webFlux.SpringWebFlux.domain.Message;
import com.webFlux.SpringWebFlux.repository.MessageRepository;
import com.webFlux.SpringWebFlux.service.MessageService;
import com.webFlux.SpringWebFlux.utility.ReactiveErrorHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;



@Service
@AllArgsConstructor
@Slf4j
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final ReactiveErrorHandler reactiveErrorHandler;

    public Flux<Message> list() {
        return messageRepository.findAll()
                .switchIfEmpty(Flux.empty())
                .onErrorResume(reactiveErrorHandler::handleFluxError);
    }

    public Mono<Message> getOne(Long id) {
        if (id == 0 || id < 0){
            return Mono.error(new IllegalArgumentException("Uncorrected id"));
        }
        return messageRepository.findById(id)
                .switchIfEmpty(Mono.empty())
                .onErrorResume(reactiveErrorHandler::handleMonoError);
    }

    public Mono<Message> addOne(Message message) {
        if (message == null || message.getData() == null || message.getData().isBlank()) {
            return Mono.error(new IllegalArgumentException("Uncorrected message"));
        }
        return messageRepository.save(message)
                .switchIfEmpty(Mono.empty())
                .onErrorResume(reactiveErrorHandler::handleMonoError);
    }

    public Flux<Message> searchByContent(String keyword) {
        if (keyword.isBlank()) {
            return Flux.error(new IllegalArgumentException("Uncorrected keyword"));
        }
        return messageRepository.searchMessageBy(keyword.trim())
                .switchIfEmpty(Flux.empty())
                .onErrorResume(reactiveErrorHandler::handleFluxError);
    }

    public Mono<Void> deleteById(Long id){
        if (id == null || id < 0) {
            return Mono.error(new IllegalArgumentException("Uncorrected argument"));
        }
        return messageRepository.deleteById(id)
                .onErrorResume(reactiveErrorHandler::handleMonoError);
    }
}
