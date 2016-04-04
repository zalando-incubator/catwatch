package org.zalando.catwatch.backend.scheduler;

import org.junit.Test;
import org.zalando.catwatch.backend.mail.MailSender;

import static org.mockito.Mockito.*;

public class FetcherTest {

    private final static int MAX_ATTEMPTS = 3;

    private final Fetcher fetcher = mock(Fetcher.class);
    private final MailSender mailSender = mock(MailSender.class);
    private final RetryableFetcher retryableFetcher = new RetryableFetcher(fetcher, MAX_ATTEMPTS, 0, 0, 0, mailSender);

    @SuppressWarnings("unchecked")
    @Test
    public void shouldRetryThreeTimes() throws Exception {
        when(fetcher.fetchData())
                .thenThrow(CrawlerRetryException.class)
                .thenThrow(CrawlerRetryException.class)
                .thenReturn(true);

        retryableFetcher.tryFetchData();

        verify(fetcher, times(3)).fetchData();
    }

}