package org.zalando.catwatch.backend.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class TaskScheduler {

    private final RetryableFetcher fetcher;

    @Autowired
    public TaskScheduler(RetryableFetcher fetcher) {
        this.fetcher = fetcher;
    }

    /**
     * This is used to fetch every Organization statistics from GitHub.
     */
    @Scheduled(cron = "${schedule}")
    public void fetchData() {
        fetcher.tryFetchData();
    }

}
