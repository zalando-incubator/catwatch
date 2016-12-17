package org.zalando.catwatch.backend.service;

import org.junit.Assert;
import org.junit.Test;
import org.zalando.catwatch.backend.model.Statistics;
import org.zalando.catwatch.backend.repo.builder.StatisticsBuilder;
import org.zalando.catwatch.backend.util.TestUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.DAYS;

public class StatisticsServiceTest {

	private final String ORGANIZATION1 = "org1", ORGANIZATION2 = "org2", ORGANIZATION3 = "org3";

	@Test(expected = IllegalArgumentException.class)
	public void testAggregateNullStatistics() {
		StatisticsService.aggregateStatistics(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAggregateEmptyStatistics() {
		StatisticsService.aggregateStatistics(new ArrayList<>());
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
				.externalContributorsCount(5)
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
				.externalContributorsCount(4)
				.allSize(16)
				.membersCount(42)
                .privateProjectCount(0)
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
				.externalContributorsCount(1)
				.allSize(15)
				.membersCount(41)
				.privateProjectCount(2)
				.tagsCount(26)
				.teamsCount(3)
				.create();

		List<Statistics> actual = new ArrayList<>();
		actual.add(s1);

		// when
		Statistics result = StatisticsService.aggregateStatistics(actual);

		// then
		TestUtils.checkEquals(s1, result);

		// when
		actual.add(s2);
		result = StatisticsService.aggregateStatistics(actual);

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
		Assert.assertEquals(5 + 4, result.getExternalContributorsCount().intValue());
		Assert.assertNotNull(result.getOrganizationName());
		Assert.assertNotNull(result.getSnapshotDate());
		Assert.assertNotNull(result.getId());
		Assert.assertEquals(ORGANIZATION1, result.getOrganizationName());

		// when
		actual.add(s3);
		result = StatisticsService.aggregateStatistics(actual);

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
		Assert.assertEquals(5 + 4 + 1, result.getExternalContributorsCount().intValue());
		Assert.assertNotNull(result.getOrganizationName());
		Assert.assertNotNull(result.getSnapshotDate());
		Assert.assertNotNull(result.getId());
		Assert.assertTrue(result.getOrganizationName().contains(ORGANIZATION1));
		Assert.assertTrue(result.getOrganizationName().contains(ORGANIZATION2));
	}
	
	@Test
	public void testAggregateHistoricalStatistics(){
		
		//given
		List<List<Statistics>> history = generateStatisticHistory();
		
		//when
		Collection<Statistics> aggregatedHistory = StatisticsService.aggregateHistoricalStatistics(history);
		
		//then
		Assert.assertNotNull(aggregatedHistory);
		Assert.assertEquals(3, aggregatedHistory.size());
		
		Iterator<Statistics> iter = aggregatedHistory.iterator();
		
		//check the first aggregated record
		checkStatisticsRecord(history, 0, iter.next());
		
		//check the second aggregated record
		checkStatisticsRecord(history, 1, iter.next());
		
		//check the second aggregated record
		checkStatisticsRecord(history, 2, iter.next());
	}
	
	
	private void checkStatisticsRecord(List<List<Statistics>> statLists, int recordNr, Statistics actual){
		
		List<Statistics> organizationsStats = new ArrayList<>();
		
		for(List<Statistics> stats : statLists){
			
			Assert.assertTrue(stats.size()>=recordNr);
			
			organizationsStats.add(stats.get(recordNr));
		}
		
		TestUtils.checkEquals(StatisticsService.aggregateStatistics(organizationsStats), actual, false);
	}
	
	
	private List<List<Statistics>> generateStatisticHistory(){
		//given 
		Date oneDayAgo = Date.from(now().minus(1, DAYS));
		Date twoDaysAgo = Date.from(now().minus(2, DAYS));
		Date threeDaysAgo = Date.from(now().minus(3, DAYS));
		
		Statistics org1Day1 = new StatisticsBuilder(null)
				.allContributersCount(100)
				.allForksCount(200)
				.allSize(300)
				.allStarsCount(400)
				.membersCount(500)
				.organizationName(ORGANIZATION1)
				.privateProjectCount(600)
				.programLanguagesCount(700)
				.tagsCount(800)
				.teamsCount(900)
				.snapshotDate(oneDayAgo)
				.create();
		
		Statistics org1Day2 = new StatisticsBuilder(null)
				.allContributersCount(100)
				.allForksCount(200)
				.allSize(300)
				.allStarsCount(400)
				.membersCount(500)
				.organizationName(ORGANIZATION1)
				.privateProjectCount(600)
				.programLanguagesCount(700)
				.tagsCount(800)
				.teamsCount(900)
				.snapshotDate(twoDaysAgo)
				.create();
		
		Statistics org1Day3 = new StatisticsBuilder(null)
				.allContributersCount(100)
				.allForksCount(200)
				.allSize(300)
				.allStarsCount(400)
				.membersCount(500)
				.organizationName(ORGANIZATION1)
				.privateProjectCount(600)
				.programLanguagesCount(700)
				.tagsCount(800)
				.teamsCount(900)
				.snapshotDate(threeDaysAgo)
				.create();
		
		
		Statistics org2Day1 = new StatisticsBuilder(null)
				.allContributersCount(100)
				.allForksCount(200)
				.allSize(300)
				.allStarsCount(400)
				.membersCount(500)
				.organizationName(ORGANIZATION2)
				.privateProjectCount(600)
				.programLanguagesCount(700)
				.tagsCount(800)
				.teamsCount(900)
				.snapshotDate(oneDayAgo)
				.create();
		
		Statistics org2Day2 = new StatisticsBuilder(null)
				.allContributersCount(100)
				.allForksCount(200)
				.allSize(300)
				.allStarsCount(400)
				.membersCount(500)
				.organizationName(ORGANIZATION2)
				.privateProjectCount(600)
				.programLanguagesCount(700)
				.tagsCount(800)
				.teamsCount(900)
				.snapshotDate(twoDaysAgo)
				.create();
		
		Statistics org2Day3 = new StatisticsBuilder(null)
				.allContributersCount(100)
				.allForksCount(200)
				.allSize(300)
				.allStarsCount(400)
				.membersCount(500)
				.organizationName(ORGANIZATION2)
				.privateProjectCount(600)
				.programLanguagesCount(700)
				.tagsCount(800)
				.teamsCount(900)
				.snapshotDate(threeDaysAgo)
				.create();
		
		Statistics org3Day1 = new StatisticsBuilder(null)
				.allContributersCount(100)
				.allForksCount(200)
				.allSize(300)
				.allStarsCount(400)
				.membersCount(500)
				.organizationName(ORGANIZATION3)
				.privateProjectCount(600)
				.programLanguagesCount(700)
				.tagsCount(800)
				.teamsCount(900)
				.snapshotDate(oneDayAgo)
				.create();
		
		Statistics org3Day2 = new StatisticsBuilder(null)
				.allContributersCount(100)
				.allForksCount(200)
				.allSize(300)
				.allStarsCount(400)
				.membersCount(500)
				.organizationName(ORGANIZATION3)
				.privateProjectCount(600)
				.programLanguagesCount(700)
				.tagsCount(800)
				.teamsCount(900)
				.snapshotDate(twoDaysAgo)
				.create();
		
		Statistics org3Day3 = new StatisticsBuilder(null)
				.allContributersCount(100)
				.allForksCount(200)
				.allSize(300)
				.allStarsCount(400)
				.membersCount(500)
				.organizationName(ORGANIZATION3)
				.privateProjectCount(600)
				.programLanguagesCount(700)
				.tagsCount(800)
				.teamsCount(900)
				.snapshotDate(threeDaysAgo)
				.create();
		
		
		List<Statistics> org1Records = new ArrayList<>();
		org1Records.add(org1Day1);
		org1Records.add(org1Day2);
		org1Records.add(org1Day3);
		
		List<Statistics> org2Records = new ArrayList<>();
		org2Records.add(org2Day1);
		org2Records.add(org2Day2);
		org2Records.add(org2Day3);
		
		List<Statistics> org3Records = new ArrayList<>();
		org3Records.add(org3Day1);
		org3Records.add(org3Day2);
		org3Records.add(org3Day3);
		
		List<List<Statistics>> history = new ArrayList<>();
		history.add(org1Records);
		history.add(org2Records);
		history.add(org3Records);
		
		return history;
	}
}
