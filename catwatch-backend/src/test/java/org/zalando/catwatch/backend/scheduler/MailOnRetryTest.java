package org.zalando.catwatch.backend.scheduler;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.zalando.catwatch.backend.mail.MailSender;

public class MailOnRetryTest {

    private final static int MAX_ATTEMPTS = 1;

    private final Fetcher fetcher = mock(Fetcher.class);
    private final MailSender mailSender = mock(MailSender.class);
    private final RetryableFetcher retryableFetcher = new RetryableFetcher(fetcher, MAX_ATTEMPTS, 0, 0, 0, mailSender);

    @Test
    public void shouldSendMailOnCrawlerFailure() throws Exception {
        CrawlerRetryException crawlerRetryException = new CrawlerRetryException(new RuntimeException());
        when(fetcher.fetchData())
                .thenThrow(crawlerRetryException);
        when(mailSender.send(crawlerRetryException)).thenReturn(true);

        retryableFetcher.tryFetchData();

        verify(mailSender, times(1)).send(crawlerRetryException);
    }

}