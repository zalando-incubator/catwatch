package org.zalando.catwatch.backend.repo;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.zalando.catwatch.backend.CatWatchBackendApplication;
import org.zalando.catwatch.backend.model.Statistics;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CatWatchBackendApplication.class)
public class StatisticsRepositoryIT {
	
    @Autowired
    StatisticsRepository repository;
    
	@Test
	public void learningtestSaveAndLoad() throws Exception {
		
        // given
		Statistics statistics = new Statistics(123, new Date());
		statistics.setMembersCount(1234);
        repository.save(statistics);
        
        // when
        Statistics loadedStatistics = repository.findOne(statistics.getKey());

        // then
        assertThat(loadedStatistics.getMembersCount(), equalTo(1234));    }

}
