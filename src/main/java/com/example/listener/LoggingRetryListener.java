package com.example.listener;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.stereotype.Component;

@Component
public class LoggingRetryListener implements RetryListener {

    private static final Logger log = LoggerFactory.getLogger(LoggingRetryListener.class);

    @Override
    public <T, E extends Throwable> boolean open(RetryContext context, RetryCallback<T, E> callback) {

        // Called before first attempt
        if (context.getRetryCount() > 0) {
            log.info("Retry process started…");
        }
        return true;
    }

    @Override
    public <T, E extends Throwable> void close(RetryContext context,
                                               RetryCallback<T, E> callback, Throwable throwable) {
        // Called after retries are done
        if (context.getRetryCount() > 0) {
            log.info("Retry process completed. Total attempts: {}", context.getRetryCount());
        }
    }

    @Override
    public <T, E extends Throwable> void onError(RetryContext context,
                                                 RetryCallback<T, E> callback, Throwable throwable) {
        int attempt = context.getRetryCount(); // 1, 2, 3...

        if (attempt == 1) {
            log.warn("Initial attempt failed due to {}. Retrying...", throwable.toString());
        } else {
            int retryNumber = attempt - 1; // 1 for second execution, 2 for third...
            log.warn("Retry attempt {} failed due to {}. Retrying...", retryNumber, throwable.toString());
        }

    }
}
