```java
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
```

```
Attempt 1  ← initial call

Attempt 2  ← retry #1

Attempt 3  ← retry #2

---------------------
@Recover   ← called after 3 failures 
```

Spring retries the method only when a `ResourceAccessException` occurs.
This exception happens during:
- read timeouts

- connection timeouts

- low level network I/O issues

In short, whenever the external service is slow or unreachable.

---

`maxAttempts = 3`

Spring will try the method three times in total:

- first attempt

- retry attempt 1

- retry attempt 2

After that, it stops and uses the fallback.

`backoff = @Backoff(delay = 1_000, multiplier = 2.0)`

Spring waits a little longer before each retry:

- wait 1 second before retry 1

- wait 2 seconds before retry 2

- (if more retries existed: 4 seconds, then 8 seconds…)

This is called exponential backoff, and it prevents overloading a slow service.

---

`requestFactory.setReadTimeout(Duration.ofSeconds(2))`

A read timeout happens when:

- the client connects successfully,

- ends the request,

- but the server takes more than 2 seconds to respond

The client cancels the request and throws an exception.

---

`connectTimeout(Duration.ofSeconds(2))`

A connection timeout happens when:

- the client tries to make a TCP connection,

- but the server does not accept the connection within 2 seconds

It is like dialing a number and it never connects — so the client gives up.
