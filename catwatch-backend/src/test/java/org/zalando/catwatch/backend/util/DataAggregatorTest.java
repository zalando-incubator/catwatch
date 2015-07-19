package org.zalando.catwatch.backend.util;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.zalando.catwatch.backend.model.Statistics;
import org.zalando.catwatch.backend.repo.populate.StatisticsBuilder;

public class DataAggregatorTest {

	private final String ORGANIZATION1 = "org1", ORGANIZATION2 = "org2", ORGANIZATION3 = "org3";

	@Test(expected = IllegalArgumentException.class)
	public void testAggregateNullStatistics() {
		DataAggregator.aggregateStatistics(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAggregateEmptyStatistics() {
		DataAggregator.aggregateStatistics(new ArrayList<>());
	}

	@Test
	public void testAggregateStatistics() {

		// given
		Statistics s1 = new StatisticsBuilder(null)
				.organizationName(ORGANIZATION1)
				.publicProjectCount(10)
				.allForksCount(12)
				.allStarsCount(11)
				.programLanguagesCount(13)
				.allContributersCount(33)
				.allSize(17)
				.membersCount(43)
				.privateProjectCount(3)
				.tagsCount(28)
				.teamsCount(1)
				.create();

		Statistics s2 = new StatisticsBuilder(null)
				.organizationName(ORGANIZATION1)
				.publicProjectCount(9)
				.allForksCount(11)
				.allStarsCount(10)
				.programLanguagesCount(12)
				.allContributersCount(32)
				.allSize(16)
				.membersCount(42)
				.tagsCount(27)
				.teamsCount(2)
				.create();

		Statistics s3 = new StatisticsBuilder(null)
				.organizationName(ORGANIZATION2)
				.publicProjectCount(8)
				.allForksCount(10)
				.allStarsCount(9)
				.programLanguagesCount(11)
				.allContributersCount(31)
				.allSize(15)
				.membersCount(41)
				.privateProjectCount(2)
				.tagsCount(26)
				.teamsCount(3)
				.create();

		List<Statistics> actual = new ArrayList();
		actual.add(s1);

		// when
		Statistics result = DataAggregator.aggregateStatistics(actual);

		// then
		TestUtils.checkEquals(s1, result);

		// when
		actual.add(s2);
		result = DataAggregator.aggregateStatistics(actual);

		// then
		Assert.assertEquals(33 + 32, result.getAllContributorsCount().intValue());
		Assert.assertEquals(12 + 11, result.getAllForksCount().intValue());
		Assert.assertEquals(17 + 16, result.getAllSizeCount().intValue());
		Assert.assertEquals(11 + 10, result.getAllStarsCount().intValue());
		Assert.assertEquals(43 + 42, result.getMembersCount().intValue());
		Assert.assertEquals(3 + 0, result.getPrivateProjectCount().intValue());
		Assert.assertEquals(13 + 12, result.getProgramLanguagesCount().intValue());
		Assert.assertEquals(10 + 9, result.getPublicProjectCount().intValue());
		Assert.assertEquals(28 + 27, result.getTagsCount().intValue());
		Assert.assertEquals(1 + 2, result.getTeamsCount().intValue());
		Assert.assertNotNull(result.getOrganizationName());
		Assert.assertNotNull(result.getSnapshotDate());
		Assert.assertNotNull(result.getId());
		Assert.assertEquals(ORGANIZATION1, result.getOrganizationName());

		// when
		actual.add(s3);
		result = DataAggregator.aggregateStatistics(actual);

		// then
		Assert.assertEquals(33 + 32 + 31, result.getAllContributorsCount().intValue());
		Assert.assertEquals(12 + 11 + 10, result.getAllForksCount().intValue());
		Assert.assertEquals(17 + 16 + 15, result.getAllSizeCount().intValue());
		Assert.assertEquals(11 + 10 + 9, result.getAllStarsCount().intValue());
		Assert.assertEquals(43 + 42 + 41, result.getMembersCount().intValue());
		Assert.assertEquals(3 + 0 + 2, result.getPrivateProjectCount().intValue());
		Assert.assertEquals(13 + 12 +11, result.getProgramLanguagesCount().intValue());
		Assert.assertEquals(10 + 9 + 8, result.getPublicProjectCount().intValue());
		Assert.assertEquals(28 + 27 +26, result.getTagsCount().intValue());
		Assert.assertEquals(1 + 2 + 3, result.getTeamsCount().intValue());
		Assert.assertNotNull(result.getOrganizationName());
		Assert.assertNotNull(result.getSnapshotDate());
		Assert.assertNotNull(result.getId());
		Assert.assertTrue(result.getOrganizationName().contains(ORGANIZATION1));
		Assert.assertTrue(result.getOrganizationName().contains(ORGANIZATION2));
	}
}
