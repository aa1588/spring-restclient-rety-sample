package com.example.domain;

public record WeatherInfo(
        String city,
        double temperature,
        String unit,
        String description,
        boolean fromFallback
) {}

