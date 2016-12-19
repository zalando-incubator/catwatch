package org.zalando.catwatch.backend.web;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.zalando.catwatch.backend.model.Statistics;
import org.zalando.catwatch.backend.repo.StatisticsRepository;
import org.zalando.catwatch.backend.repo.builder.StatisticsBuilder;
import org.zalando.catwatch.backend.util.StringParser;
import org.zalando.catwatch.backend.util.TestUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.DAYS;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.junit.Assert.assertThat;
import static org.zalando.catwatch.backend.util.TestUtils.createAbsoluteStatisticsUrl;

public class StatisticsApiIT extends AbstractCatwatchIT {

	@Autowired
	private StatisticsRepository repository;

	private static final String ORGANIZATION1 = "organization1", ORGANIZATION2 = "organization2";

	private Statistics s1, s2, s3, s5;

	private static int statisticIdCounter = 0;
	@Test
	public void testGetLatestStatisticsFromOrganizations() throws Exception {

		// given
		createStatistics();

		// check if data has been initialized correctly
		Assert.assertNotNull(repository);
		Assert.assertNotNull(s1);
		Assert.assertNotNull(s2);

		repository.save(s1);
		repository.save(s2);

		Assert.assertNotNull("Statistics not found in repository", repository.findByOrganizationName(s1.getOrganizationName()));
		Assert.assertNotNull("Statistics not found in repository", repository.findByOrganizationName(s2.getOrganizationName()));

		// when
		String organisations = s1.getOrganizationName() + "," + s2.getOrganizationName();
		String url = TestUtils.createAbsoluteStatisticsUrl(this.base.toString(), organisations, null, null);

		ResponseEntity<Statistics[]> response = template.getForEntity(url, Statistics[].class);

		Statistics[] statsResponse = response.getBody();

		// then
		Assert.assertNotNull(statsResponse);

		Assert.assertEquals(1, statsResponse.length);

		TestUtils.checkAggregatedStatistics(statsResponse[0], Arrays.asList(s1, s2));
	}

	@Test
	public void testGetStatisticsFromOrganizationWithStartAndEnddate() throws Exception {
		// given
		Date oneDayAgo = Date.from(now().minus(1, DAYS));
		Date twoDaysAgo = Date.from(now().minus(2, DAYS));
		Date threeDaysAgo = Date.from(now().minus(3, DAYS));
		Date fourDaysAgo = Date.from(now().minus(4, DAYS));
		Date fiveDaysAgo = Date.from(now().minus(5, DAYS));

		repository.deleteAll();

		// // snapshot date is before requested period of time
		s1 = createAndSaveStatistics(ORGANIZATION1, oneDayAgo);
		// snapshot date is at the exact start of the requested period of time
		s2 = createAndSaveStatistics(ORGANIZATION1, twoDaysAgo);
		s3 = createAndSaveStatistics(ORGANIZATION1, threeDaysAgo);
		/**/ createAndSaveStatistics(ORGANIZATION2, threeDaysAgo);
		s5 = createAndSaveStatistics(ORGANIZATION1, fourDaysAgo);
		/**/ createAndSaveStatistics(ORGANIZATION1, fiveDaysAgo);

		assertThat(repository.findAll(), iterableWithSize(6));
		// check if data has been initialized correctly
		Assert.assertNotNull(repository);
		Assert.assertNotNull(s1);
		Assert.assertNotNull(s2);
		Assert.assertNotNull("Statistics not found in repository",
				repository.findByOrganizationName(s1.getOrganizationName()));
		Assert.assertNotNull("Statistics not found in repository",
				repository.findByOrganizationName(s2.getOrganizationName()));

		// initialize url parameter
		String start = StringParser.getISO8601StringForDate(new Date(fourDaysAgo.getTime()+60000));
		String end = StringParser.getISO8601StringForDate(twoDaysAgo);

		// create url
		String url = createAbsoluteStatisticsUrl(this.base.toString(), ORGANIZATION1, start, end);
		
		// when
		ResponseEntity<Statistics[]> response = template.getForEntity(url, Statistics[].class);

		List<Statistics> statsResponse = Arrays.asList(response.getBody());
		
		
		// then
		Assert.assertNotNull(statsResponse);

		Assert.assertEquals(2, statsResponse.size());

		TestUtils.checkEquals(s3, statsResponse.get(0));
		TestUtils.checkEquals(s5, statsResponse.get(1));
		
		Assert.assertThat(statsResponse.get(0).toString(), Matchers.stringContainsInOrder(Arrays.asList(s3.getId()+"")));
		Assert.assertThat(statsResponse.get(1).toString(), Matchers.stringContainsInOrder(Arrays.asList(s5.getId()+"")));
		
	}


	private Statistics createAndSaveStatistics(String organizationName, Date snapshotDate) {
		Statistics s = new Statistics(statisticIdCounter++, snapshotDate);
		s.setOrganizationName(organizationName);
		s.setSnapshotDate(snapshotDate);
		return repository.save(s);
	}

	private void createStatistics() {
		this.repository.deleteAll();

		s1 = new Statistics(statisticIdCounter++, new Date());
		s1.setAllContributorsCount(10);
		s1.setExternalContributorsCount(5);
		s1.setAllForksCount(12);
		s1.setAllSizeCount(100);
		s1.setAllStarsCount(23);
		s1.setMembersCount(7);
		s1.setPrivateProjectCount(11);
		s1.setProgramLanguagesCount(3);
		s1.setPublicProjectCount(2);
		s1.setTagsCount(6);
		s1.setTeamsCount(0);
		s1.setOrganizationName("organization1");

		repository.save(s1);

		s2 = new Statistics(statisticIdCounter++,
				new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24));
		s2.setAllContributorsCount(9);
		s2.setExternalContributorsCount(3);
		s2.setAllForksCount(11);
		s2.setAllSizeCount(99);
		s2.setAllStarsCount(22);
		s2.setMembersCount(6);
		s2.setPrivateProjectCount(10);
		s2.setProgramLanguagesCount(2);
		s2.setPublicProjectCount(1);
		s2.setTagsCount(5);
		s2.setTeamsCount(1);
		s2.setOrganizationName("organization2");

		s3 = new StatisticsBuilder(null)
				.organizationName("organization3")
				.create();

		/**/ new StatisticsBuilder(null)
				.organizationName("organization4")
				.create();
		
		s5 = new StatisticsBuilder(null)
				.organizationName("organization5")
				.create();
	}
}
