package com.webFlux.SpringWebFlux.repository;

import com.webFlux.SpringWebFlux.domain.Message;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;


public interface MessageRepository extends ReactiveCrudRepository<Message, Long> {

    @Query("SELECT * FROM message WHERE data ILIKE $1")
    Flux<Message> searchMessageBy(String keyword);

}
