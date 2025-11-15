package com.webFlux.webFluxCassandra.repository;

import com.webFlux.webFluxCassandra.domain.Message;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;

import java.util.UUID;

public interface MessageRepository extends ReactiveCassandraRepository<Message, UUID> {
}
