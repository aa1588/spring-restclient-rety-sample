package com.example.domain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;


@Service
@Slf4j
public class WeatherService {

    private final RestClient weatherRestClient;

    public WeatherService(RestClient weatherRestClient) {
        this.weatherRestClient = weatherRestClient;
    }

    @Retryable(
            retryFor = { ResourceAccessException.class }, // includes timeouts
            maxAttempts = 3,
            backoff = @Backoff(delay = 1_000, multiplier = 2.0) // 1s, then 2s
    )
    public WeatherInfo getWeatherWithRetry(String city) {
        log.info("Calling slow external weather API for city={}", city);

        // read timeout of 2 seconds
        WeatherInfo weather = weatherRestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/slow")
                        .queryParam("city", city)
                        .build())
                .retrieve()
                .body(WeatherInfo.class);

        log.info("Received weather response for city={}: {}", city, weather);
        return weather;
    }

    @Recover
    public WeatherInfo recover(ResourceAccessException ex, String city) {
        log.error("All retry attempts failed for city={}. Triggering fallback. Reason: {}",
                city, ex.getMessage());

        WeatherInfo fallback = new WeatherInfo(
                city,
                0.0,
                "C",
                "Weather service not available, this is a fallback response",
                true
        );

        log.info("Returning fallback response for city={}: {}", city, fallback);
        return fallback;
    }


    // For comparison: no retry and fast endpoint
    public WeatherInfo getFastWeather(String city) {

        log.info("Calling fast external weather API for city={}", city);

        WeatherInfo weather = weatherRestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/fast")
                        .queryParam("city", city)
                        .build())
                .retrieve()
                .body(WeatherInfo.class);

        log.info("Received fast weather response for city={}: {}", city, weather);
        return weather;

    }

}
