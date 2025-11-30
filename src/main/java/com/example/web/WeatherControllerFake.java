package com.example.web;

import com.example.domain.WeatherInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WeatherControllerFake {

    @GetMapping("/external/weather/slow")
    public WeatherInfo slowWeather(@RequestParam(defaultValue = "Dallas") String city) throws InterruptedException {

        // simulate a very slow external service (10 seconds)
        Thread.sleep(10_000);
        return new WeatherInfo(
                city,
                32.5,
                "C",
                "Hot and slow response",
                false
        );
    }

    @GetMapping("/external/weather/fast")
    public WeatherInfo fastWeather(@RequestParam(defaultValue = "Dallas") String city) {

        return new WeatherInfo(
                city,
                30.0,
                "C",
                "Sunny with a light breeze",
                false
        );
    }
}
