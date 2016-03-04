package org.zalando.catwatch.backend.scheduler;

public class CrawlerRetryException extends RuntimeException {
    public CrawlerRetryException(Exception e) {
        super(e);
    }
}
