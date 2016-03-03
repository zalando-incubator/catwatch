package org.zalando.catwatch.backend.scheduler;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.zalando.catwatch.backend.CatWatchBackendApplication;
import org.zalando.catwatch.backend.mail.MailSender;

import static org.mockito.Mockito.*;

public class MailOnRetryTest {

    private final static int MAX_ATTEMPTS = 1;

    private final Fetcher fetcher = mock(Fetcher.class);
    private final MailSender mailSender = mock(MailSender.class);
    private final RetryableFetcher retryableFetcher = new RetryableFetcher(fetcher, 1, 0, 0, 0, mailSender);

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