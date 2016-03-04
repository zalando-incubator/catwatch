package org.zalando.catwatch.backend.web;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;
import org.zalando.catwatch.backend.model.Contributor;
import org.zalando.catwatch.backend.repo.ContributorRepository;
import org.zalando.catwatch.backend.repo.builder.ContributorBuilder;

import java.net.URI;
import java.util.Date;
import java.util.Map;

import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.util.Date.from;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.web.util.UriComponentsBuilder.fromHttpUrl;
import static org.zalando.catwatch.backend.util.Constants.*;
import static org.zalando.catwatch.backend.web.config.DateUtil.iso8601;

public class ContributorsApiIT extends AbstractCatwatchIT {

	@Autowired
	private ContributorRepository repository;

	public ContributorBuilder newContributor() {
		return new ContributorBuilder(repository);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testContributorsGet_Serialization() throws Exception {

		// given
		repository.deleteAll();
		Contributor c = newContributor().organizationName("IBM").save();

		// when
		String url = contributorUrl().queryParam("organizations", "IBM").toUriString();
		Map<String, Object>[] contributors = exchange(GET, url, Map[].class).getBody();

		// then
		assertEquals(contributors.length, 1);
		assertThat(contributors[0].get("id"), equalTo((int) c.getId()));
		assertThat(contributors[0].get("snapshotDate"), equalTo(c.getSnapshotDate().getTime()));
	}

	@Test
	public void testContributorsGet_FindInPeriodOfTime() throws Exception {

		// given
		repository.deleteAll();

		newContributor().id(11).days(2).organizationId(1).organizationName("IBM").orgCommits(28).save();
		newContributor().id(11).days(3).organizationId(1).organizationName("IBM").orgCommits(23).save();
		newContributor().id(11).days(4).organizationId(1).organizationName("IBM").orgCommits(20).save();

		newContributor().id(12).days(2).organizationId(2).organizationName("Sun").orgCommits(18).save();
		newContributor().id(12).days(4).organizationId(2).organizationName("Sun").orgCommits(16).save();

		newContributor().id(13).days(2).organizationId(1).organizationName("IBM").orgCommits(7).save();
		newContributor().id(13).days(4).organizationId(1).organizationName("IBM").orgCommits(4).save();

		Date endDate = from(now());
		Date startDate = from(now().minus(3, DAYS).minus(12, HOURS));

		// when
		String url = contributorUrl()
				//
				.queryParam(API_REQUEST_PARAM_ORGANIZATIONS, " IBM,Sun")
				.queryParam(API_REQUEST_PARAM_STARTDATE, iso8601(startDate)) //
				.queryParam(API_REQUEST_PARAM_ENDDATE, iso8601(endDate)) //
				.toUriString();

		ResponseEntity<Contributor[]> response = exchange(GET, url, Contributor[].class);

		// then
		Contributor[] contributors = response.getBody();

		assertThat(contributors[0].getId(), equalTo(11L));
		assertThat(contributors[0].getOrganizationalCommitsCount(), equalTo(8));

		assertThat(contributors[1].getId(), equalTo(13L));
		assertThat(contributors[1].getOrganizationalCommitsCount(), equalTo(3));

		assertThat(contributors[2].getId(), equalTo(12L));
		assertThat(contributors[2].getOrganizationalCommitsCount(), equalTo(2));

		assertThat(contributors.length, equalTo(3));
	}

	@Test
	public void testContributorsGet_SortBy() throws Exception {

		// given
		repository.deleteAll();

		newContributor().days(1).orgCommits(28).orgProjects(33).persProjects(10).name("Alf").organizationName("IBM")
				.save();
		newContributor().days(1).orgCommits(29).orgProjects(32).persProjects(11).name("Ben").organizationName("Sun")
				.save();

		// when
		Contributor[] contributors = exchange(GET, builder().queryParam("sortBy", "-name").toUriString(),
				Contributor[].class).getBody();

		// then
		assertThat(contributors.length, equalTo(2));
		assertThat(contributors[0].getName(), equalTo("Ben"));
		assertThat(contributors[1].getName(), equalTo("Alf"));

		// when
		contributors = exchange(GET, builder().queryParam("sortBy", "name").toUriString(),
				Contributor[].class).getBody();

		// then
		assertThat(contributors.length, equalTo(2));
		assertThat(contributors[0].getName(), equalTo("Alf"));
		assertThat(contributors[1].getName(), equalTo("Ben"));

		// when
		contributors = exchange(GET, builder().queryParam("sortBy", "-personalProjectsCount").toUriString(),
				Contributor[].class).getBody();

		// then
		assertThat(contributors.length, equalTo(2));
		assertThat(contributors[0].getName(), equalTo("Ben"));
		assertThat(contributors[1].getName(), equalTo("Alf"));

		// when
		contributors = exchange(GET, builder().queryParam("sortBy", "personalProjectsCount").toUriString(),
				Contributor[].class).getBody();

		// then
		assertThat(contributors.length, equalTo(2));
		assertThat(contributors[0].getName(), equalTo("Alf"));
		assertThat(contributors[1].getName(), equalTo("Ben"));

		// when
		contributors = exchange(GET, builder().queryParam("sortBy", "-organizationalProjectsCount").toUriString(),
				Contributor[].class).getBody();

		// then
		assertThat(contributors.length, equalTo(2));
		assertThat(contributors[0].getName(), equalTo("Alf"));
		assertThat(contributors[1].getName(), equalTo("Ben"));

		// when
		contributors = exchange(GET, builder().queryParam("sortBy", "-organizationalCommitsCount").toUriString(),
				Contributor[].class).getBody();

		// then
		assertThat(contributors.length, equalTo(2));
		assertThat(contributors[0].getName(), equalTo("Ben"));
		assertThat(contributors[1].getName(), equalTo("Alf"));

		// TODO does not work yet!!!
//		// when
//		contributors = exchange(GET, builder().queryParam("sortBy", "-organizationName").toUriString(),
//				Contributor[].class).getBody();
//
//		// then
//		assertThat(contributors.length, equalTo(2));
//		assertThat(contributors[0].getName(), equalTo("Ben"));
//		assertThat(contributors[1].getName(), equalTo("Alf"));
	}

	private UriComponentsBuilder builder() {
		return contributorUrl().queryParam(API_REQUEST_PARAM_ORGANIZATIONS, "IBM,Sun");
	}

	private <T> ResponseEntity<T> exchange(HttpMethod method, String url, Class<T> clazz) throws Exception {
		RequestEntity<String> requestEntity = new RequestEntity<>(method, new URI(url));
		return template.exchange(requestEntity, clazz);
	}

	private UriComponentsBuilder contributorUrl() {
		return fromHttpUrl(base.toString() + API_RESOURCE_CONTRIBUTORS);
	}
}
