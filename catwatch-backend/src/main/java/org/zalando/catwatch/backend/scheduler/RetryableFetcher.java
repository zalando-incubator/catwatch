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

    private final Fetcher fetcher;
    private final int maxAttempts;
    private final int initialInterval;
    private final int maxInterval;
    private final double multiplier;
    private final MailSender mailSender;

    @Autowired
    public RetryableFetcher(Fetcher fetcher,
                            @Value("${fetcher.maxAttempts}") int maxAttempts,
                            @Value("${fetcher.initialInterval}") int initialInterval,
                            @Value("${fetcher.maxInterval}") int maxInterval,
                            @Value("${fetcher.multiplier}") double multiplier,
                            MailSender mailSender) {
        this.fetcher = fetcher;
        this.maxAttempts = maxAttempts;
        this.initialInterval = initialInterval;
        this.maxInterval = maxInterval;
        this.multiplier = multiplier;
        this.mailSender = mailSender;
    }

    public void tryFetchData() {
        RetryCallback<Boolean, RuntimeException> retryCallback = context -> fetcher.fetchData();
        RecoveryCallback<Boolean> recoveryCallback = retryContext -> mailSender.send(retryContext.getLastThrowable());
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
