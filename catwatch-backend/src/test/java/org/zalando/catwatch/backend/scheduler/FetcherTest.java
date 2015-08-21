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

import static org.mockito.Mockito.*;

/**
 * Created by jgolebiowski on 8/18/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(properties = {"fetcher.initialInterval=100", "fetcher.multiplier=2", "fetcher.maxAttempts=5"})
@SpringApplicationConfiguration(classes = CatWatchBackendApplication.class)
public class FetcherTest {

    @InjectMocks
    @Autowired
    private RetryableFetcher retryableFetcher;

    @Mock
    private Fetcher fetcher;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

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