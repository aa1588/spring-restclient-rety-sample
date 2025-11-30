package com.example;

import com.example.domain.WeatherInfo;
import com.example.domain.WeatherService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class Application {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
        WeatherService bean = context.getBean(WeatherService.class);
        WeatherInfo fast = bean.getFastWeather("Dallas");
        System.out.println(fast);

        System.out.println("\n + Testing RETRY with slow external API" + "=".repeat(10));
        WeatherInfo slow = bean.getWeatherWithRetry("Dallas");
        System.out.println(slow);

    }

}
