package com.webFlux.SpringWebFlux.service;

public interface MetricsService {

    void initializeCustomMetrics();

    void incrementCounter(String name, String tagKey, String tagValue);

    void incrementMessageCount();


}
