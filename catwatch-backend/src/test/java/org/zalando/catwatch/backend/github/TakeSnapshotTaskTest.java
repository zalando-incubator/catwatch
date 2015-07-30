package org.zalando.catwatch.backend.github;

import static java.util.Arrays.asList;
import static java.util.Collections.sort;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.mockito.Mockito;
import org.zalando.catwatch.backend.model.Language;

public class TakeSnapshotTaskTest {
	
	TakeSnapshotTask task = new TakeSnapshotTask(null, null, null);
	
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
