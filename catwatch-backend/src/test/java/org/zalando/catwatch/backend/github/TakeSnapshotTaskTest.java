package org.zalando.catwatch.backend.github;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Collections.sort;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHRepository.Contributor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.zalando.catwatch.backend.model.Language;
import org.zalando.catwatch.backend.model.Project;
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
	public void testCollectPrLanguages() throws Exception {
		
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
		when(repo.listCommits()).thenReturn(asList(mock(GHCommit.class), mock(GHCommit.class)));
		when(repo.listContributors()).thenReturn(asList(mock(Contributor.class), mock(Contributor.class)));
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

}
