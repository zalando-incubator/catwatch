package org.zalando.catwatch.backend.github;

import com.google.common.collect.Lists;
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
import org.springframework.test.util.ReflectionTestUtils;
import org.zalando.catwatch.backend.model.Language;
import org.zalando.catwatch.backend.model.Project;
import org.zalando.catwatch.backend.model.Statistics;
import org.zalando.catwatch.backend.model.util.Scorer;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.time.Instant.now;
import static java.util.Arrays.asList;
import static java.util.Collections.*;
import static java.util.Date.from;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TakeSnapshotTaskTest {

    @InjectMocks
    TakeSnapshotTask task = new TakeSnapshotTask(null, null, null, from(now()));

    @Mock
    Scorer scorer;

    @Mock
    Date date = new Date();

    static final String ORGANIZATION_LOGIN = "myLogin";
    static final int ORGANIZATION_ID = 77;

    @Test
    public void testCollectContributors() throws Exception {

        // given
        RepositoryWrapper repo1 = mock(RepositoryWrapper.class);
        Contributor c1 = newContributor(11, 22, "http://a.com", 33);
        when(repo1.listContributors()).thenReturn(asList(c1));

        RepositoryWrapper repo2 = mock(RepositoryWrapper.class);
        Contributor c2 = newContributor(44, 55, "http://b.com", 66);
        when(repo2.listContributors()).thenReturn(asList(c2));

        List<org.zalando.catwatch.backend.model.Contributor> contributors =
                new ArrayList<>(
                        task.collectContributors(org(asList(repo1, repo2))));

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

    private Contributor newContributor(int id, int contributions, String htmlUrl, int publicRepoCount) throws Exception {
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
        when(org.listMembers()).thenReturn(mockList(GHUser.class, 5));
        when(org.listTeams()).thenReturn(mockList(GHTeam.class, 4));
        when(org.listRepositories()).thenReturn(emptyList());
        // TODO add more behavior and more assertions
        when(org.getLogin()).thenReturn("myLogin");

        // when
        Statistics statistics = task.collectStatistics(org);

        // then
        assertThat(statistics.getPublicProjectCount(), equalTo(0));
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
        when(repo.getFileContent("MAINTAINERS")).thenReturn(new ByteArrayInputStream("foo\nbar".getBytes()));
        when(repo.getFileContent(".catwatch.yaml")).thenReturn(new ByteArrayInputStream("image: test\n".getBytes()));
        when(scorer.score(any(Project.class))).thenReturn(55);

        // when
        List<Project> projects = new ArrayList<>(task.collectProjects(org(singletonList(repo))));

        // then
        assertThat(projects, hasSize(1));
        Project project = projects.get(0);

        assertThat(project.getGitHubProjectId(), equalTo(123L));

        assertThat(project.getSnapshotDate().getTime(), equalTo(((Date)ReflectionTestUtils.getField(task, "snapshotDate")).getTime()));
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
        assertThat(project.getMaintainers(), containsInAnyOrder("foo", "bar"));
        assertThat(project.getImage(), equalTo("test"));
    }

    @Test
    public void testCollectLanguages() throws Exception {

        // given
        List<RepositoryWrapper> repos = asList( //
                repo("C", 30, "Go", 15, "Java", 4), //
                repo("C", 30, "Go", 15, "Java", 4), //
                repo("Java", 2));
        // when
        List<Language> langs = new ArrayList<>(task.collectLanguages(org(repos)));

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

    class TestGHContributor extends GHRepository.Contributor {
        public TestGHContributor(String login, int id) {
            super();
            this.login = login;
            this.id = id;
        }
    }

    @Test
    /**
     * Tests the counting of external contributors
     */
    public void testCountExternalContributors() throws Exception {
        GHRepository.Contributor internal1 = new TestGHContributor("internal1", 1);
        GHRepository.Contributor internal2 = new TestGHContributor("internal2", 2);
        List<GHUser> members = Lists.newArrayList(internal1, internal2);
        List<GHRepository.Contributor> contributors = Lists.newArrayList(internal1, internal2, new TestGHContributor("external", 3));

        RepositoryWrapper repo = mock(RepositoryWrapper.class);
        when(repo.listContributors()).thenReturn(contributors);

        // given
        OrganizationWrapper org = mock(OrganizationWrapper.class);
        when(org.listMembers()).thenReturn(members);
        when(org.listTeams()).thenReturn(mockList(GHTeam.class, 4));
        when(org.listRepositories()).thenReturn(Lists.newArrayList(repo));
        when(org.contributorIsMember(any(Contributor.class))).thenCallRealMethod();

        // when
        Statistics statistics = task.collectStatistics(org);

        assertThat(statistics.getExternalContributorsCount(), equalTo(1));
        assertThat(statistics.getAllContributorsCount(), equalTo(3));
    }

    private OrganizationWrapper org(List<RepositoryWrapper> repos) {
        OrganizationWrapper org = mock(OrganizationWrapper.class);
        when(org.listRepositories()).thenReturn(repos);
        when(org.getLogin()).thenReturn(ORGANIZATION_LOGIN);
        when(org.getId()).thenReturn(ORGANIZATION_ID);
        return org;
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
        Map<String, T> map = new HashMap<>();
        for (int index = 0; index < keyAndValuePairs.length; index = index + 2) {
            String key = (String) keyAndValuePairs[index];
            Object value = keyAndValuePairs[index + 1];
            map.put(key, (T) value);
        }
        return map;
    }

    private <T> List<T> mockList(Class<? extends T> clazz, int size) {
        // does not work on Travis CI (compile error)
        // return IntStream.generate(() -> 1).limit(size).mapToObj(i -> mock(clazz)).collect(toList());
        List<T> result = new ArrayList<>();
        for (int index = 0; index < size; index++) {
            result.add(mock(clazz));
        }
        return result;
    }
}
