package org.zalando.catwatch.backend.github;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Collections.sort;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.of;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHRepository.Contributor;
import org.kohsuke.github.GHTeam;
import org.kohsuke.github.GHUser;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.zalando.catwatch.backend.model.Language;
import org.zalando.catwatch.backend.model.Project;
import org.zalando.catwatch.backend.model.Statistics;
import org.zalando.catwatch.backend.model.util.Scorer;

@RunWith(MockitoJUnitRunner.class)
public class TakeSnapshotTaskTest {

	@InjectMocks
	TakeSnapshotTask task = new TakeSnapshotTask(null, null, null);

	@Mock
	Scorer scorer;

	@Mock
	Date date = new Date();

	@Test
	public void testCollectContributors() throws Exception {
		
		// given
		RepositoryWrapper repo1 = mock(RepositoryWrapper.class);
		Contributor c1 = newContributor(11, 22, "http://a.com", 33);
		when(repo1.listContributors()).thenReturn(asList(c1));
		
		RepositoryWrapper repo2 = mock(RepositoryWrapper.class);
		Contributor c2 = newContributor(44, 55, "http://b.com", 66);
		when(repo2.listContributors()).thenReturn(asList(c2));

		// when
		List<org.zalando.catwatch.backend.model.Contributor> contributors = //
				new ArrayList<org.zalando.catwatch.backend.model.Contributor>( //
						task.collectContributors(asList(repo1, repo2), 77));

		// then
		assertThat(contributors, hasSize(2));

		org.zalando.catwatch.backend.model.Contributor c1_ = contributors.get(0);
		assertThat(c1_.getId(), equalTo(11L));
		assertThat(c1_.getOrganizationalCommitsCount(), equalTo(22));
		assertThat(c1_.getUrl(), equalTo("http://a.com"));

		org.zalando.catwatch.backend.model.Contributor c2_ = contributors.get(1);
		assertThat(c2_.getId(), equalTo(44L));
		assertThat(c2_.getOrganizationalCommitsCount(), equalTo(55));
		assertThat(c2_.getUrl(), equalTo("http://b.com"));
	}

	private Contributor newContributor(int id, int contributions, String htmlUrl, int publicRepoCount)
			throws Exception {
		Contributor c = mock(Contributor.class);
		when(c.getId()).thenReturn(id);
		when(c.getContributions()).thenReturn(contributions);
		when(c.getHtmlUrl()).thenReturn(new URL(htmlUrl));
		when(c.getPublicRepoCount()).thenReturn(publicRepoCount);
		return c;
	}

	@Test
	public void testCollectStatistics() throws Exception {

		// given
		OrganizationWrapper org = mock(OrganizationWrapper.class);
		when(org.getPublicRepoCount()).thenReturn(44);
		when(org.listPublicMembers()).thenReturn(mockList(GHUser.class, 5));
		when(org.listTeams()).thenReturn(mockList(GHTeam.class, 4));
		when(org.listRepositories()).thenReturn(emptyList());
		// TODO add more behavior and more assertions
		when(org.getLogin()).thenReturn("myLogin");

		// when
		Statistics statistics = task.collectStatistics(org);

		// then
		assertThat(statistics.getPublicProjectCount(), equalTo(44));
		assertThat(statistics.getMembersCount(), equalTo(5));
		assertThat(statistics.getTeamsCount(), equalTo(4));
		assertThat(statistics.getOrganizationName(), equalTo("myLogin"));
	}

	@Test
	public void testCollectProjects() throws Exception {

		// given
		RepositoryWrapper repo = mock(RepositoryWrapper.class);
		when(repo.getId()).thenReturn(123);
		when(repo.getName()).thenReturn("awesome");
		when(repo.getUrl()).thenReturn(new URL("http://a.com/b.html"));
		when(repo.getDescription()).thenReturn("cool");
		when(repo.getStarsCount()).thenReturn(11);
		when(repo.getForksCount()).thenReturn(22);
		when(repo.getLastPushed()).thenReturn(date);
		when(repo.getPrimaryLanguage()).thenReturn("Go");
		when(repo.listLanguages()).thenReturn(toMap("C", 30, "Go", 15, "Java", 4));
		when(repo.listCommits()).thenReturn(mockList(GHCommit.class, 2));
		when(repo.listContributors()).thenReturn(mockList(Contributor.class, 2));
		when(scorer.score(any(Project.class))).thenReturn(55);

		// when
		List<Project> projects = new ArrayList<>(task.collectProjects(singletonList(repo), "mylogin"));

		// then
		assertThat(projects, hasSize(1));
		Project project = projects.get(0);

		assertThat(project.getGitHubProjectId(), equalTo(123L));
		assertThat(project.getSnapshotDate().getTime(), equalTo(task.getSnapshotDate().getTime()));
		assertThat(project.getName(), equalTo("awesome"));
		assertThat(project.getUrl(), equalTo("http://a.com/b.html"));
		assertThat(project.getDescription(), equalTo("cool"));
		assertThat(project.getStarsCount(), equalTo(11));
		assertThat(project.getForksCount(), equalTo(22));
		assertThat(project.getLastPushed(), equalTo(date.toString()));
		assertThat(project.getPrimaryLanguage(), equalTo("Go"));
		assertThat(project.getLanguageList(), containsInAnyOrder("C", "Go", "Java"));
		assertThat(project.getCommitsCount(), equalTo(2));
		assertThat(project.getContributorsCount(), equalTo(2));
		assertThat(project.getScore(), equalTo(55));
	}

	@Test
	public void testCollectLanguages() throws Exception {

		// given
		List<RepositoryWrapper> repos = asList( //
				repo("C", 30, "Go", 15, "Java", 4), //
				repo("C", 30, "Go", 15, "Java", 4), //
				repo("Java", 2));

		// when
		List<Language> langs = new ArrayList<>(task.collectLanguages(repos));

		// then
		assertThat(langs, hasSize(3));
		sort(langs, (p1, p2) -> p1.getName().compareTo(p2.getName()));

		assertThat(langs.get(0).getName(), equalTo("C"));
		assertThat(langs.get(0).getProjectsCount(), equalTo(2));
		assertThat(langs.get(0).getPercentage(), equalTo(60));

		assertThat(langs.get(1).getName(), equalTo("Go"));
		assertThat(langs.get(1).getProjectsCount(), equalTo(2));
		assertThat(langs.get(1).getPercentage(), equalTo(30));

		assertThat(langs.get(2).getName(), equalTo("Java"));
		assertThat(langs.get(2).getProjectsCount(), equalTo(3));
		assertThat(langs.get(2).getPercentage(), equalTo(10));
	}

	private RepositoryWrapper repo(Object... keyAndValuePairs) {
		RepositoryWrapper repo = mock(RepositoryWrapper.class);
		when(repo.listLanguages()).thenReturn(toMap(keyAndValuePairs));
		return repo;
	}

	/**
	 * @param keyAndValuePairs
	 * @return Returns a map with the given keys and values.
	 */
	@SuppressWarnings("unchecked")
	private <T> Map<String, T> toMap(Object... keyAndValuePairs) {
		Map<String, T> map = new HashMap<String, T>();
		for (int index = 0; index < keyAndValuePairs.length; index = index + 2) {
			String key = (String) keyAndValuePairs[index];
			Object value = keyAndValuePairs[index + 1];
			map.put(key, (T) value);
		}
		return map;
	}

	private <T> List<T> mockList(Class<? extends T> clazz, int size) {
		return IntStream.generate(() -> 1).limit(size).mapToObj(i -> mock(clazz)).collect(toList());
	}

}
