package org.zalando.catwatch.backend.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.RecoveryCallback;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.zalando.catwatch.backend.mail.MailSender;

import java.util.HashMap;
import java.util.Map;

/**
 * Tries to fetch organizations data from GitHub and saves it to the database.
 */
@Component
public class RetryableFetcher {

    @Autowired
    private Fetcher fetcher;
    @Value("${fetcher.maxAttempts}")
    private int maxAttempts;
    @Value("${fetcher.initialInterval}")
    private int initialInterval;
    @Value("${fetcher.maxInterval}")
    private int maxInterval;
    @Value("${fetcher.multiplier}")
    private double multiplier;

    @Autowired
    private MailSender mailSender;

    public void tryFetchData() {
        RetryCallback<Boolean, RuntimeException> retryCallback = context -> {
            return fetcher.fetchData();
        };
        RecoveryCallback<Boolean> recoveryCallback = retryContext -> {
            return mailSender.send(retryContext.getLastThrowable());
        };
        retryTemplate().execute(retryCallback, recoveryCallback);
    }

    private RetryTemplate retryTemplate() {
        RetryTemplate template = new RetryTemplate();
        template.setBackOffPolicy(exponentialBackOffPolicy());
        template.setRetryPolicy(retryPolicy());
        return template;
    }

    private SimpleRetryPolicy retryPolicy() {
        return new SimpleRetryPolicy(maxAttempts, transientExceptions());
    }

    private ExponentialBackOffPolicy exponentialBackOffPolicy() {
        ExponentialBackOffPolicy exponentialBackOffPolicy = new ExponentialBackOffPolicy();

        exponentialBackOffPolicy.setInitialInterval(initialInterval);
        exponentialBackOffPolicy.setMaxInterval(maxInterval);
        exponentialBackOffPolicy.setMultiplier(multiplier);
        return exponentialBackOffPolicy;
    }

    private Map<Class<? extends Throwable>, Boolean> transientExceptions() {
        Map<Class<? extends Throwable>, Boolean> exceptions = new HashMap<>();
        exceptions.put(CrawlerRetryException.class, true);
        return exceptions;
    }


}
