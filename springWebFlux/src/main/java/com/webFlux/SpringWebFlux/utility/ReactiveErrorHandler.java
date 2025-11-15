package com.webFlux.SpringWebFlux.utility;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class ReactiveErrorHandler {

    private static final String DATABASE_ERROR_OCCURRED = "Database error occurred: {}";

    private static final String UNEXPECTED_ERROR_OCCURRED = "Database error occurred: {}";

    /** Обработка ошибок для Mono */
    public <T> Mono<T> handleMonoError(Throwable e) {
        if (e instanceof DataAccessException) {
            log_Database_Error(e);
            return Mono.error(new RuntimeException("Database access error"));
        } else {
            log_Unexpected_Error(e);
            return Mono.error(e);
        }
    }

    /** Обработка ошибок для Flux */
    public <T> Flux<T> handleFluxError(Throwable e) {
        if (e instanceof DataAccessException) {
            log_Database_Error(e);
            return Flux.error(new RuntimeException("Database access error"));
        } else {
            log_Unexpected_Error(e);
            return Flux.error(e);
        }
    }

    private void log_Database_Error(Throwable e) {
        log.info(DATABASE_ERROR_OCCURRED, e.getMessage());
    }

    private void log_Unexpected_Error(Throwable e) {
        log.info(UNEXPECTED_ERROR_OCCURRED, e.getMessage());
    }
}
