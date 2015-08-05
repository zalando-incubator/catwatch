package org.zalando.catwatch.backend.web.fetch;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.core.env.Environment;
import org.zalando.catwatch.backend.model.Contributor;
import org.zalando.catwatch.backend.model.Project;
import org.zalando.catwatch.backend.model.Statistics;
import org.zalando.catwatch.backend.repo.ContributorRepository;
import org.zalando.catwatch.backend.repo.ProjectRepository;
import org.zalando.catwatch.backend.repo.StatisticsRepository;
import org.zalando.catwatch.backend.web.AbstractCatwatchIT;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.springframework.web.util.UriComponentsBuilder.fromHttpUrl;

@IntegrationTest({ "github.login=", "organization.list=rwitzeltestorg", "server.port=0" })
public class FetchControllerIT extends AbstractCatwatchIT {

	@Autowired
	private Environment env;

	@Autowired
	private ContributorRepository contributorRepository;

	@Autowired
	private StatisticsRepository statisticsRepository;

	@Autowired
	private ProjectRepository projectRepository;

	@Test
	public void testFetch() throws Exception {

		// given
		contributorRepository.deleteAll();
		statisticsRepository.deleteAll();
		projectRepository.deleteAll();

		// when
		String result = template.getForEntity(fetchUrl(), String.class).getBody();

		// then
		List<Statistics> statisticses = newArrayList(statisticsRepository.findAll());
		List<Contributor> contributors = newArrayList(contributorRepository.findAll());
		List<Project> projects = newArrayList(projectRepository.findAll());

		assertThat(result, equalTo("OK"));
		assertThat(statisticses.size(), equalTo(1));
		assertThat(projects.size(), equalTo(1));
		assertThat(contributors.size(), equalTo(1));

		assertThat(statisticses.get(0).getOrganizationName(), equalTo("rwitzeltestorg"));
		assertThat(projects.get(0).getName(), equalTo("testrepo1"));
		assertThat(contributors.get(0).getName(), equalTo("Rodrigo Witzel"));
	}

	private String fetchUrl() {
		return fromHttpUrl(base.toString() + "fetch").toUriString();
	}

}
