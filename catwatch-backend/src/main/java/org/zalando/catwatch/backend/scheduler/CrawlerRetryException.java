package org.zalando.catwatch.backend.scheduler;

/**
 * Created by jgolebiowski on 8/19/15.
 */
public class CrawlerRetryException extends RuntimeException {
    public CrawlerRetryException(Exception e) {
        super(e);
    }
}
