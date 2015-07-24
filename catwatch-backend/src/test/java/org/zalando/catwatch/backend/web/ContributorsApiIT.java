package org.zalando.catwatch.backend.web;

import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.util.Date.from;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.springframework.web.util.UriComponentsBuilder.fromHttpUrl;
import static org.zalando.catwatch.backend.util.Constants.API_REQUEST_PARAM_ENDDATE;
import static org.zalando.catwatch.backend.util.Constants.API_REQUEST_PARAM_ORGANIZATIONS;
import static org.zalando.catwatch.backend.util.Constants.API_REQUEST_PARAM_STARTDATE;
import static org.zalando.catwatch.backend.util.Constants.API_RESOURCE_CONTRIBUTORS;
import static org.zalando.catwatch.backend.web.config.DateUtil.iso8601;

import java.time.Instant;
import java.util.Date;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;
import org.zalando.catwatch.backend.model.Contributor;
import org.zalando.catwatch.backend.repo.ContributorRepository;
import org.zalando.catwatch.backend.repo.populate.ContributorBuilder;

public class ContributorsApiIT extends AbstractCatwatchIT {

	@Autowired
	private ContributorRepository repository;

	public ContributorBuilder newContributor() {
		return new ContributorBuilder(repository);
	}

	@Test
	public void testContributorsGet_FindInPeriodOfTime() throws Exception {

		// given
		repository.deleteAll();

		newContributor().id(11).days(2).organizationId(1).organizationName("a").orgCommits(28).save();
		newContributor().id(11).days(3).organizationId(1).organizationName("a").orgCommits(23).save();
		newContributor().id(11).days(4).organizationId(1).organizationName("a").orgCommits(20).save();

		newContributor().id(12).days(2).organizationId(2).organizationName("b").orgCommits(18).save();
		newContributor().id(12).days(4).organizationId(2).organizationName("b").orgCommits(16).save();

		newContributor().id(13).days(2).organizationId(1).organizationName("a").orgCommits(7).save();
		newContributor().id(13).days(4).organizationId(1).organizationName("a").orgCommits(4).save();

		Date endDate = from(now());
		Date startDate = from(Instant.now().minus(3, DAYS).minus(12, HOURS));

		// when
		// (TODO does not work with " a, b " yet -> "%20a,b")
		String url = contributorUrl() //
				.queryParam(API_REQUEST_PARAM_ORGANIZATIONS, "a,b")
				.queryParam(API_REQUEST_PARAM_STARTDATE, iso8601(startDate)) //
				.queryParam(API_REQUEST_PARAM_ENDDATE, iso8601(endDate)) //
				.toUriString();

		// ResponseEntity<String> response = template.getForEntity(url,
		// String.class);
		ResponseEntity<Contributor[]> response = template.getForEntity(url, Contributor[].class);

		// then
		Contributor[] contributors = response.getBody();
		//System.out.println(Arrays.asList(contributors));

		assertThat(contributors[0].getId(), equalTo(11L));
		assertThat(contributors[0].getOrganizationalCommitsCount(), equalTo(8));

		assertThat(contributors[1].getId(), equalTo(13L));
		assertThat(contributors[1].getOrganizationalCommitsCount(), equalTo(3));

		assertThat(contributors[2].getId(), equalTo(12L));
		assertThat(contributors[2].getOrganizationalCommitsCount(), equalTo(2));

		assertThat(contributors.length, equalTo(3));
	}

	private UriComponentsBuilder contributorUrl() {
		return fromHttpUrl(base.toString() + API_RESOURCE_CONTRIBUTORS);
	}
}
