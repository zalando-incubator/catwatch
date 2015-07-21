package org.zalando.catwatch.backend.scheduler;

import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.zalando.catwatch.backend.github.GitHubCrawler;
import org.zalando.catwatch.backend.model.Contributor;
import org.zalando.catwatch.backend.model.Project;
import org.zalando.catwatch.backend.model.Statistics;
import org.zalando.catwatch.backend.repo.ContributorRepository;
import org.zalando.catwatch.backend.repo.ProjectRepository;
import org.zalando.catwatch.backend.repo.StatisticsRepository;

@RunWith(MockitoJUnitRunner.class)
public class TaskSchedulerTest {
	
	@InjectMocks
	TaskScheduler scheduler = new TaskScheduler();
	
	@Mock
	GitHubCrawler connector;
	
	@Mock
	StatisticsRepository statisticsRepository;
	
	@Mock
	ProjectRepository projectRepository;
	
	@Mock
	ContributorRepository contributorRepository;

	@Mock
	Statistics stat1;

	@Mock
	Statistics stat2;
	
	@Mock
	List<Project> projectList1;

	@Mock
	List<Project> projectList2;
	
	@Mock
	List<Contributor> contributorList1;

	@Mock
	List<Contributor> contributorList2;
	
	@Test
	@Ignore
	public void testFetchData() throws Exception {
		// TODO update me
		// given
//		when(connector.findStatistics("zal")).thenReturn(stat1);
//		when(connector.findStatistics("ando")).thenReturn(stat2);
//
//		// when
//		scheduler.fetchData(asList("zal", "ando"));
//
//		// then
//		verify(statisticsRepository).save(stat1);
//		verify(statisticsRepository).save(stat2);
	}
}
