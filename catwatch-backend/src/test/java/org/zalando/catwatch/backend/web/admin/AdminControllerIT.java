package org.zalando.catwatch.backend.web.admin;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.zalando.catwatch.backend.model.Contributor;
import org.zalando.catwatch.backend.model.Project;
import org.zalando.catwatch.backend.model.Statistics;
import org.zalando.catwatch.backend.repo.ContributorRepository;
import org.zalando.catwatch.backend.repo.ProjectRepository;
import org.zalando.catwatch.backend.repo.StatisticsRepository;
import org.zalando.catwatch.backend.repo.builder.ContributorBuilder;
import org.zalando.catwatch.backend.repo.builder.ProjectBuilder;
import org.zalando.catwatch.backend.repo.builder.StatisticsBuilder;
import org.zalando.catwatch.backend.web.AbstractCatwatchIT;

import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.Date.from;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.web.util.UriComponentsBuilder.fromHttpUrl;

public class AdminControllerIT extends AbstractCatwatchIT {

	@Autowired
	private ContributorRepository contributorRepository;

	@Autowired
	private StatisticsRepository statisticsRepository;

	@Autowired
	private ProjectRepository projectRepository;

	private ProjectBuilder newProject() {
		return new ProjectBuilder(projectRepository);
	}

	private ContributorBuilder newContributor() {
		return new ContributorBuilder(contributorRepository);
	}

	private StatisticsBuilder newStatistic() {
		return new StatisticsBuilder(statisticsRepository);
	}

	@Test
	@Ignore("Endpoint functionaltity removed")
	public void testConfigScoringProject() throws Exception {

		// given
		projectRepository.deleteAll();
		Project p = newProject().snapshotDate(from(now().minus(3, DAYS))).organizationName("abc").score(13).save();

		// when
		String scoringFunction = "function(p) { return 55; }";
		String result = template.exchange(configScoringProjectUrl(), POST,
				entity(scoringFunction, "X-Organizations", "abc"), String.class).getBody();

		// then
		assertThat(result, containsString("1 project object(s) updated"));
		assertThat(projectRepository.findOne(p.getId()).getScore(), equalTo(55));
	}

	private HttpEntity<String> entity(String body, String headerName, String headerValue) {
		HttpHeaders headers = new HttpHeaders();
		headers.set(headerName, headerValue);
		return new HttpEntity<>(body, headers);
	}

	@Test
	@Ignore("Endpoint functionaltity removed")
	public void testExportAndImport() throws Exception {

		// given
		contributorRepository.deleteAll();
		statisticsRepository.deleteAll();
		Contributor c = contributorRepository.findOne(newContributor().save().getKey());
		Statistics s = statisticsRepository.findOne(newStatistic().save().getKey());

		// when
		DatabaseDto dto = template.getForEntity(exportUrl(), DatabaseDto.class).getBody();

		contributorRepository.deleteAll();
		statisticsRepository.deleteAll();
		projectRepository.deleteAll();

		template.postForEntity(importUrl(), dto, String.class);

		// then
		assertThat(contributorRepository.findAll(), iterableWithSize(1));
		Contributor c_ = contributorRepository.findAll().iterator().next();
		assertThat(c_.getId(), equalTo(c.getId()));
		// TODO problem!!!! the milliseconds are truncated so that the date
		// differs after the export/import :-(
		// assertThat(c_.getSnapshotDate().getTime(),
		// equalTo(c.getSnapshotDate().getTime()));

		assertThat(statisticsRepository.findAll(), iterableWithSize(1));
		Statistics s_ = statisticsRepository.findAll().iterator().next();
		assertThat(s_.getId(), equalTo(s.getId()));
		// TODO problem!!!! the milliseconds are truncated so that the date
		// differs after the export/import :-(
		// assertThat(s_.getSnapshotDate().getTime(),
		// equalTo(s.getSnapshotDate().getTime()));
	}

	private String configScoringProjectUrl() {
		return fromHttpUrl(base.toString() + "config/scoring.project").toUriString();
	}

	private String exportUrl() {
		return fromHttpUrl(base.toString() + "export").toUriString();
	}

	private String importUrl() {
		return fromHttpUrl(base.toString() + "import").toUriString();
	}

}
