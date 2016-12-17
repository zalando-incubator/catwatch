package org.zalando.catwatch.backend.web;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.zalando.catwatch.backend.model.Language;
import org.zalando.catwatch.backend.model.Project;
import org.zalando.catwatch.backend.repo.ProjectRepository;
import org.zalando.catwatch.backend.repo.builder.BuilderUtil;
import org.zalando.catwatch.backend.repo.builder.ProjectBuilder;
import org.zalando.catwatch.backend.util.TestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class LanguagesControllerIT extends AbstractCatwatchIT {

	private final String LANG_CS = "C#", LANG_JAVA = "Java", LANG_CPP = "C++",  LANG_CSS = "CSS", //
			LANG_HTML = "HTML5", LANG_JS = "JavaScript", LANG_PSCL = "Pascal", LANG_GO = "Go", LANG_SCALA = "Scala",
			LANG_PY = "Python", LANG_GRV = "Groovy", LANG_VB = "VB";

	private final String ORG1 = "organization1", ORG2 = "organization2";
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
		String url = TestUtils.createAbsoluteLanguagesUrl(this.base.toString(), ORG1, null, null, null);

		ResponseEntity<Language[]> response = template.getForEntity(url, Language[].class);

		Language[] langResponse = response.getBody();

		// then
		Assert.assertThat(langResponse.length, Matchers.equalTo(5));

		checkLanguages(langResponse, sortedLanguages, 0);
	}

	@Test
	public void testGetLanguagesWithOffsetAndLimit() {

		// given
		this.repository.deleteAll();
		fillRepositoryRandom(6, ORG2);
		
		List<String> languageNames = fillRepository(ORG1);

		List<Language> languageList = generateLanguageList(languageNames);
		
		int limit = 6;
		int offset = 0;

		// when
		String url = TestUtils.createAbsoluteLanguagesUrl(this.base.toString(), ORG1, limit, offset, null);

		ResponseEntity<Language[]> response = template.getForEntity(url, Language[].class);

		Language[] langResponse = response.getBody();

		Assert.assertThat(langResponse.length, Matchers.equalTo(limit));

		checkLanguages(langResponse, languageList, offset);

		offset += limit;

		limit = 3;

		// when
		url = TestUtils.createAbsoluteLanguagesUrl(this.base.toString(), ORG1, limit, offset, null);

		response = template.getForEntity(url, Language[].class);

		langResponse = response.getBody();

		checkLanguages(langResponse, languageList, offset);

	}

	
	@SuppressWarnings("unused")
    @Test
	public void testGetLanguagesWithQueryFilter() {

		// given
		this.repository.deleteAll();
		fillRepositoryRandom(6, ORG2);
		
		List<String> languageNames = fillRepository(ORG1);

		// when
		String url = TestUtils.createAbsoluteLanguagesUrl(this.base.toString(), ORG1, null, null, LANG_CPP);

		ResponseEntity<Language[]> response = template.getForEntity(url, Language[].class);

		Language[] langResponse = response.getBody();

		//FIXME Assert.assertThat(langResponse.length, Matchers.equalTo(Collections.frequency(languageNames, LANG_CPP)));

	}
	
	private List<String> fillRepositoryRandom(final int nrOfProjects, String organization) {

		List<String> languages = new ArrayList<>();

		Date now = new Date();
		for (int i = 0; i < nrOfProjects; i++) {
			Project p = new ProjectBuilder(repository, new Date(), 0L, null, null, 0, 0, 0, 0, 0, 0)
					.primaryLanguage(BuilderUtil.randomLanguage()).organizationName(organization).snapshotDate(now)
					.name("p" + i).save();

			languages.add(p.getPrimaryLanguage());
		}

		Assert.assertEquals(nrOfProjects,
				repository.findProjects(organization, Optional.empty(), Optional.empty()).size());

		return languages;
	}

	private List<String> fillRepository(String organization) {

		List<String> langs = Arrays.asList(
				LANG_JAVA, LANG_JAVA, LANG_JAVA, LANG_JAVA, LANG_JAVA, LANG_JAVA, LANG_JAVA,
				LANG_HTML, LANG_HTML, LANG_HTML, LANG_HTML, LANG_HTML, 
				LANG_SCALA, LANG_SCALA, LANG_SCALA, LANG_SCALA, 
				LANG_CPP, LANG_CPP, LANG_CPP, 
				LANG_CSS, LANG_CSS, LANG_CSS, LANG_CSS, LANG_CSS, 
				LANG_JS, LANG_JS, LANG_JS, LANG_JS, LANG_JS, LANG_JS, LANG_JS, 
				LANG_GO,
				LANG_VB, LANG_VB,
				LANG_CS, LANG_CS,
				LANG_GRV,
				LANG_PY, LANG_PY, LANG_PY,
				LANG_PSCL
				);
		
		Collections.shuffle(langs);
		
		List<String> result = new ArrayList<>();
		
		Date now = new Date();
		for (int i = 0; i < langs.size(); i++) {
			Project p = new ProjectBuilder(repository, new Date(), 0L, null, null, 0, 0, 0, 0, 0, 0)
					.primaryLanguage(BuilderUtil.randomLanguage()).organizationName(organization).snapshotDate(now)
					.name("p" + i).description("Test project "+i).gitHubProjectId(1234456).score((int) (Math.random() * 100))
					.languages(Arrays.asList(BuilderUtil.randomLanguage(), BuilderUtil.randomLanguage(), BuilderUtil.randomLanguage()))
					.lastPushed(null).save();

			result.add(p.getPrimaryLanguage());
		}

		Assert.assertEquals(langs.size(),
				repository.findProjects(organization, Optional.empty(), Optional.empty()).size());

		return result;
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

		String langName;
		
		while (iter.hasNext()) {

			langName = iter.next();

			if (!languageCount.keySet().contains(langName)) {
				languageCount.put(langName, 1);
			} else {
				int old = languageCount.get(langName);
				languageCount.put(langName, ++old);
			}
		}

		languages = new ArrayList<>();

		for (String name : languageCount.keySet()) {
			Language lang = new Language(name);

			lang.setProjectsCount(languageCount.get(name));

			lang.setPercentage((int) Math.round((double) lang.getProjectsCount() / languageNames.size() * 100));

			languages.add(lang);

		}

		Collections.sort(languages, new LanguageComparator(languageNames));

		return languages;
	}

	private void checkLanguages(Language[] actualLangs, List<Language> expectedLangs, int offset) {
		int tmpProjectCount = Integer.MAX_VALUE, tmpPercentage = Integer.MAX_VALUE;

		Language actual, expected;

		for (int i = 0; i < actualLangs.length; i++) {

			actual = actualLangs[i];
			expected = expectedLangs.get(offset + i);

			Assert.assertThat(actual.getPercentage(), Matchers.lessThanOrEqualTo(tmpPercentage));

			Assert.assertThat(actual.getProjectsCount(), Matchers.lessThanOrEqualTo(tmpProjectCount));

			Assert.assertThat(actual.getPercentage(), Matchers.equalTo(expected.getPercentage()));

			Assert.assertThat(actual.getProjectsCount(), Matchers.equalTo(expected.getProjectsCount()));
			
			Assert.assertThat(actual.toString(), Matchers.stringContainsInOrder(Arrays.asList("name: ", "projectsCount: ", "percentage: ")));
			
			
			tmpPercentage = actual.getPercentage();
			tmpProjectCount = actual.getProjectsCount();
		}
	}
}
