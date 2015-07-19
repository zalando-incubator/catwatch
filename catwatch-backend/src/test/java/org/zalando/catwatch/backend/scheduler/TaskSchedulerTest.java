package org.zalando.catwatch.backend.scheduler;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.zalando.catwatch.backend.github.GithubConnector;
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
	GithubConnector connector;
	
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
	public void testFetchOrganizationStatistics_getAndSave() throws Exception {
		
		// given
		when(connector.findStatistics("zal")).thenReturn(stat1);
		when(connector.findStatistics("ando")).thenReturn(stat2);
		
		// when
		scheduler.fetchOrganizationStatistics(asList("zal", "ando"));
		
		// then
		verify(statisticsRepository).save(stat1);
		verify(statisticsRepository).save(stat2);
	}
	
	@Test
	public void testFetchOrganizationProjects_getAndSave() throws Exception {
		
		
		// given
		when(connector.findProjects("zal")).thenReturn(projectList1);
		when(connector.findProjects("ando")).thenReturn(projectList2);
		
		// when
		scheduler.fetchOrganizationProjects(asList("zal", "ando"));
		
		// then
		verify(projectRepository).save(projectList1);
		verify(projectRepository).save(projectList2);
		
	}
	
	@Test
	public void testFetchOrganizationContributors_getAndSave() throws Exception {
	
		// given
		when(connector.findContributors("zal")).thenReturn(contributorList1);
		when(connector.findContributors("ando")).thenReturn(contributorList2);
		
		// when
		scheduler.fetchOrganizationContributors(asList("zal", "ando"));
		
		// then
		verify(contributorRepository).save(contributorList1);
		verify(contributorRepository).save(contributorList2);
		
		
	}
}
