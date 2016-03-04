package org.zalando.catwatch.backend.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.zalando.catwatch.backend.model.Project;
import org.zalando.catwatch.backend.repo.ProjectRepository;
import org.zalando.catwatch.backend.repo.builder.ProjectBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static java.util.Optional.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class ProjectServiceImplTest {

	@InjectMocks
	ProjectServiceImpl projectService;

	@Mock
	ProjectRepository projectRepository;

	private final String ORGANIZATION1 = "organization1";

	private final String LANGUAGE1 = "JAVA";
	
	@Test
	public void findProjectsByOrganizationNameTest() throws Exception {

		// given
		Project p = new ProjectBuilder().create();
		when(projectRepository.findProjects(ORGANIZATION1, empty(), empty())).thenReturn(singletonList(p));

		// when
		List<Project> projectList = (List<Project>) projectService.findProjects(ORGANIZATION1, empty(), empty(),
				empty(), empty(), empty(), empty(), empty());

		// then
		assertThat(projectList, hasSize(1));
		assertThat(projectList.get(0), equalTo(p));

	}

	@Test
	public void findMostRecentProjectsTest() throws Exception {

		// given
		Date snapshotDate = new Date(System.currentTimeMillis());
		Project p = new ProjectBuilder().snapshotDate(snapshotDate).gitHubProjectId(1).organizationName(ORGANIZATION1)
				.starsCount(1).commitsCount(2).contributorsCount(1).score(20).forksCount(0).create();
		when(projectRepository.findProjects(ORGANIZATION1, snapshotDate, empty(), empty()))
				.thenReturn(singletonList(p));
		when(projectRepository.findProjects(ORGANIZATION1, empty(), empty())).thenReturn(singletonList(p));
		// when
		List<Project> projectList = (List<Project>) projectService.findProjects(ORGANIZATION1, empty(), empty(),
				Optional.of(snapshotDate), empty(), empty(), empty(), empty());

		// then
		assertThat(projectList, hasSize(1));
		assertThat(projectList.get(0), equalTo(p));

	}

	@Test
	public void findProjectsByProjectNameAndSnapshotDateTest() throws Exception {

		// given
		Date snapshotDate = new Date(System.currentTimeMillis());
		Project p = new ProjectBuilder().snapshotDate(snapshotDate).gitHubProjectId(1).organizationName(ORGANIZATION1)
				.starsCount(1).commitsCount(2).contributorsCount(1).score(20).forksCount(0).create();
		when(projectRepository.findProjects(ORGANIZATION1, snapshotDate, Optional.of("PROJECT Z"), empty()))
				.thenReturn(singletonList(p));
		when(projectRepository.findProjects(ORGANIZATION1, Optional.of("PROJECT Z"), empty()))
				.thenReturn(singletonList(p));

		// when
		List<Project> projectList = (List<Project>) projectService.findProjects(ORGANIZATION1, empty(), empty(),
				Optional.of(snapshotDate), empty(), empty(), Optional.of("PROJECT Z"), empty());

		// then
		assertThat(projectList, hasSize(1));
		assertThat(projectList.get(0), equalTo(p));
	}

	@Test
	public void findProjectsByProjectNameAndLanguageTest() throws Exception {

		// given
		Project p = new ProjectBuilder().gitHubProjectId(1).organizationName(ORGANIZATION1).starsCount(1)
				.commitsCount(2).contributorsCount(1).score(20).forksCount(0).create();
		when(projectRepository.findProjects(ORGANIZATION1, Optional.of("PROJECT Z"),
				Optional.of(LANGUAGE1))).thenReturn(singletonList(p));

		// when
		List<Project> projectList = (List<Project>) projectService.findProjects(ORGANIZATION1, empty(), empty(),
				empty(), empty(), empty(), Optional.of("PROJECT Z"), Optional.of(LANGUAGE1));

		// then
		assertThat(projectList, hasSize(1));
		assertThat(projectList.get(0), equalTo(p));
	}

	@Test
	public void findProjectPerformanceByStartDateAndEndDateTest() throws Exception {

		// given
		Date snapshotDate = new Date(System.currentTimeMillis());
		Project p = new ProjectBuilder().gitHubProjectId(1).organizationName(ORGANIZATION1).snapshotDate(snapshotDate)
				.starsCount(4).commitsCount(6).contributorsCount(2).score(20).forksCount(0).create();
		Project p1 = new ProjectBuilder().gitHubProjectId(1).organizationName(ORGANIZATION1).days(1).starsCount(1)
				.commitsCount(2).contributorsCount(1).score(20).forksCount(0).create();

		when(projectRepository.findProjects(ORGANIZATION1, snapshotDate, empty(), empty()))
				.thenReturn(singletonList(p));
		when(projectRepository.findProjects(ORGANIZATION1, p1.getSnapshotDate(), empty(), empty()))
				.thenReturn(singletonList(p1));

		// when
		List<Project> projectList = (List<Project>) projectService.findProjects(ORGANIZATION1, empty(), empty(),
				Optional.ofNullable(p1.getSnapshotDate()), Optional.ofNullable(p.getSnapshotDate()), empty(), empty(),
				empty());

		// then
		p.setStarsCount(p.getStarsCount() - p1.getStarsCount());
		p.setCommitsCount(p.getCommitsCount() - p1.getCommitsCount());
		p.setForksCount(p.getForksCount() - p1.getForksCount());
		p.setContributorsCount(p.getContributorsCount() - p1.getContributorsCount());
		p.setScore(p.getScore() - p1.getScore());
		assertThat(projectList, hasSize(1));
		assertThat(projectList.get(0), equalTo(p));
	}

	@Test
	public void findProjectsByStartDateAndEndDateTest() throws Exception {

		// given
		Date snapshotDate = new Date(System.currentTimeMillis());
		Project p = new ProjectBuilder().gitHubProjectId(1).organizationName(ORGANIZATION1).snapshotDate(snapshotDate)
				.starsCount(4).commitsCount(6).contributorsCount(2).score(20).forksCount(0).create();
		Project p1 = new ProjectBuilder().gitHubProjectId(2).organizationName(ORGANIZATION1).days(1).starsCount(1)
				.commitsCount(2).contributorsCount(1).score(20).forksCount(0).create();

		when(projectRepository.findProjects(ORGANIZATION1, snapshotDate, empty(), empty()))
				.thenReturn(singletonList(p));
		when(projectRepository.findProjects(ORGANIZATION1, p1.getSnapshotDate(), empty(), empty()))
				.thenReturn(singletonList(p1));

		// when
		List<Project> projectList = (List<Project>) projectService.findProjects(ORGANIZATION1, empty(), empty(),
				Optional.ofNullable(p1.getSnapshotDate()), Optional.ofNullable(p.getSnapshotDate()), empty(), empty(),
				empty());

		// then

		assertThat(projectList, hasSize(1));
		assertThat(projectList.get(0), equalTo(p));
	}

	@Test
	public void findProjectsBySnapshotDateSortByTest() throws Exception {

		// given
		Project p = new ProjectBuilder().gitHubProjectId(1).organizationName(ORGANIZATION1).starsCount(4)
				.commitsCount(6).contributorsCount(2).score(20).forksCount(0).create();
		Project p1 = new ProjectBuilder().gitHubProjectId(2).organizationName(ORGANIZATION1).starsCount(3)
				.commitsCount(2).contributorsCount(1).score(20).forksCount(0).create();
		Project p2 = new ProjectBuilder().gitHubProjectId(3).organizationName(ORGANIZATION1).starsCount(1)
				.commitsCount(3).contributorsCount(1).score(20).forksCount(0).create();
		List<Project> projects = new ArrayList<>();
		projects.add(p);
		projects.add(p1);
		projects.add(p2);
		when(projectRepository.findProjects(ORGANIZATION1, empty(), empty())).thenReturn(projects);

		// when
		List<Project> projectList = (List<Project>) projectService.findProjects(ORGANIZATION1, empty(), empty(),
				empty(), empty(), Optional.of("-commits"), empty(), empty());

		// then
		projects = new ArrayList<>();
		projects.add(p);
		projects.add(p2);
		projects.add(p1);
		assertThat(projectList, hasSize(3));
		assertThat(projectList, equalTo(projects));
		
		// when
		projectList = (List<Project>) projectService.findProjects(ORGANIZATION1, empty(), empty(),
				empty(), empty(), Optional.of("stars"), empty(), empty());

		// then
		projects = new ArrayList<>();
		projects.add(p2);
		projects.add(p1);
		projects.add(p);
		assertThat(projectList, hasSize(3));
		assertThat(projectList, equalTo(projects));
	}

	@Test
	public void findProjectsBySnapshotDatePagination() throws Exception {

		// given
		Project p = new ProjectBuilder().gitHubProjectId(1).organizationName(ORGANIZATION1).starsCount(4)
				.commitsCount(6).contributorsCount(2).score(20).forksCount(0).create();
		Project p1 = new ProjectBuilder().gitHubProjectId(2).organizationName(ORGANIZATION1).starsCount(1)
				.commitsCount(2).contributorsCount(1).score(50).forksCount(0).create();
		Project p2 = new ProjectBuilder().gitHubProjectId(3).organizationName(ORGANIZATION1).starsCount(1)
				.commitsCount(3).contributorsCount(1).score(15).forksCount(0).create();
		Project p3 = new ProjectBuilder().gitHubProjectId(4).organizationName(ORGANIZATION1).starsCount(1)
				.commitsCount(3).contributorsCount(1).score(30).forksCount(0).create();
		Project p4 = new ProjectBuilder().gitHubProjectId(5).organizationName(ORGANIZATION1).starsCount(1)
				.commitsCount(3).contributorsCount(1).score(39).forksCount(0).create();
		List<Project> projects = new ArrayList<>();
		projects.add(p);
		projects.add(p1);
		projects.add(p2);
		projects.add(p3);
		projects.add(p4);
		when(projectRepository.findProjects(ORGANIZATION1, empty(), empty())).thenReturn(projects);

		// when
		List<Project> projectList = (List<Project>) projectService.findProjects(ORGANIZATION1, Optional.of(2),
				empty(), empty(), empty(), empty(), empty(), empty());

		// then
		projects = new ArrayList<>();
		projects.add(p1);
		projects.add(p4);

		assertThat(projectList, hasSize(2));
		assertThat(projectList, equalTo(projects));

		// when
		projectList = (List<Project>) projectService.findProjects(ORGANIZATION1, Optional.of(3),
				Optional.of(1), empty(), empty(), empty(), empty(), empty());

		// then
		projects = new ArrayList<>();
		projects.add(p4);
		projects.add(p3);
		projects.add(p);
		assertThat(projectList, hasSize(3));
		assertThat(projectList, equalTo(projects));
	}

}
