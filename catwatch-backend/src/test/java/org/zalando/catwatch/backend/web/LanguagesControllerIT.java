package org.zalando.catwatch.backend.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.zalando.catwatch.backend.model.Language;
import org.zalando.catwatch.backend.model.Project;
import org.zalando.catwatch.backend.model.Statistics;
import org.zalando.catwatch.backend.repo.ProjectRepository;
import org.zalando.catwatch.backend.repo.populate.BuilderUtil;
import org.zalando.catwatch.backend.repo.populate.ProjectBuilder;
import org.zalando.catwatch.backend.util.TestUtils;

import com.google.common.collect.Lists;

public class LanguagesControllerIT extends AbstractCatwatchIT {

	
	private final String 
			ORG1= "organization1",
			ORG2 = "organization2";
	@Autowired
	ProjectRepository repository;

	@Test
	public void testGetLanguages() {

		// given
		this.repository.deleteAll();
		fillRepositoryRandom(6, ORG2);
		List<String> languageNames = fillRepositoryRandom(40, ORG1);
		
		List<Language> sortedLanguages = generateLanguageList(languageNames);

		// when
		String url = TestUtils.createAbsoluteStatisticsUrl(this.base.toString(), ORG1, null, null);

		ResponseEntity<Language[]> response = template.getForEntity(url, Language[].class);

		Language[] statsResponse = response.getBody();

		// then
		//FIXME Assert.assertThat(statsResponse.length, Matchers.equalTo(sortedLanguages.size()));
	}

	
	@Test
	public void testGetLanguagesWithOffsetAndLimit() {
	}

	
	
	private List<String> fillRepositoryRandom(final int nrOfProjects, String organization) {

		
		System.out.println("Creating projects");
		
		List<String> languages = new ArrayList<>();
		
		Date now = new Date();
		for (int i = 0; i < nrOfProjects; i++) {
			Project p = new ProjectBuilder(repository, new Date(), 0L, null, null, 0, 0, 0, 0, 0)
					.primaryLanguage(BuilderUtil.randomLanguage())
					.organizationName(organization)
					.snapshotDate(now)
					.name("p"+i)
					.save();
			
			System.out.println("Stored project with name "+p.getName());
			
			languages.add(p.getPrimaryLanguage());
		}
		
		//Assert.assertEquals(nrOfProjects, Lists.newArrayList(repository.findAll()).size());
		
		Assert.assertEquals(nrOfProjects, repository.findProjects(organization, Optional.ofNullable(null), Optional.ofNullable(null)).size());

		return languages;
	}

	private class LanguageComparator implements Comparator<Language> {

		Collection<String> stringCollection;

		public LanguageComparator(final Collection<String> languageNames) {
			this.stringCollection = languageNames;
		}

		@Override
		public int compare(final Language o1, final Language o2) {

			int f1 = Collections.frequency(stringCollection, o1.getName());

			int f2 = Collections.frequency(stringCollection, o2.getName());

			if (f1 >= f2) {
				return -1;
			}

			return 1;
		}

	}

	private List<Language> generateLanguageList(List<String> languageNames) {

		List<Language> languages;
		Map<String, Integer> languageCount = new HashMap<>();

		Iterator<String> iter = languageNames.iterator();

		while (iter.hasNext()) {

			String langName = iter.next();

			if (!languageCount.keySet().contains(langName)) {
				languageCount.put(langName, 1);
			} else {
				int old = languageCount.get(langName);
				languageCount.put(langName, ++old);
			}
		}

		languages = new ArrayList<>();

		for (String langName : languageCount.keySet()) {
			Language lang = new Language(langName);

			lang.setProjectsCount(languageCount.get(langName));

			lang.setPercentage((int) Math.round((double) lang.getProjectsCount() / languageNames.size() * 100));

			languages.add(lang);

		}

		Collections.sort(languages, new LanguageComparator(languageNames));

		return languages;
	}
}
