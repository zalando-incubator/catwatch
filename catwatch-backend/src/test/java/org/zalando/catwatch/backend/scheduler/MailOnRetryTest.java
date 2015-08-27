package org.zalando.catwatch.backend.scheduler;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.zalando.catwatch.backend.CatWatchBackendApplication;
import org.zalando.catwatch.backend.mail.MailSender;

import static org.mockito.Mockito.*;

/**
 * Created by jgolebiowski on 8/18/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(properties = {"fetcher.initialInterval=100", "fetcher.multiplier=2", "fetcher.maxAttempts=1"})
@SpringApplicationConfiguration(classes = CatWatchBackendApplication.class)
public class MailOnRetryTest {

    @InjectMocks
    @Autowired
    private RetryableFetcher retryableFetcher;

    @Mock
    private Fetcher fetcher;

    @Mock
    private MailSender mailSender;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

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