package org.zalando.catwatch.backend.util;

import org.junit.Assert;
import org.springframework.web.util.UriComponentsBuilder;
import org.zalando.catwatch.backend.model.Statistics;

import java.util.List;

public class TestUtils {
	
	public static void checkEquals(Statistics expected, Statistics actual) {
		
		checkEquals(expected, actual, true);
	}
	
	public static void checkEquals(Statistics expected, Statistics actual, boolean checkIds) {
		Assert.assertEquals("Number of contributors is different", expected.getAllContributorsCount(),
				actual.getAllContributorsCount());

		Assert.assertEquals("Snapshot date is different", expected.getSnapshotDate().getTime(), actual.getSnapshotDate().getTime());

		if(checkIds) Assert.assertEquals("ID is different", expected.getId(), actual.getId());

		Assert.assertEquals("Number of contributors is different", expected.getAllForksCount(),
				actual.getAllForksCount());

		Assert.assertEquals("Number of size is different", expected.getAllSizeCount(), actual.getAllSizeCount());

		Assert.assertEquals("Number of stars is different", expected.getAllStarsCount(), actual.getAllStarsCount());

		Assert.assertEquals("Number of members is different", expected.getMembersCount(), actual.getMembersCount());

		Assert.assertEquals("Oranization names are different", expected.getOrganizationName(),
				actual.getOrganizationName());

		Assert.assertEquals("Number of projects is different", expected.getPrivateProjectCount(),
				actual.getPrivateProjectCount());

		Assert.assertEquals("Number of programming languages is different", expected.getProgramLanguagesCount(),
				actual.getProgramLanguagesCount());

		Assert.assertEquals("Number of public projects is different", expected.getPublicProjectCount(),
				actual.getPublicProjectCount());

		Assert.assertEquals("Number of tags is different", expected.getTagsCount(), actual.getTagsCount());

		Assert.assertEquals("Number of teams is different", expected.getTeamsCount(), actual.getTeamsCount());
	}
	
	public static void checkAggregatedStatistics(Statistics actual, List<Statistics> expectedstatisticsToBeAggregated) {
		Integer 
			contributers = 0,
			externalContributors = 0,
			forks = 0, 
			size = 0, 
			stars = 0, 
			members = 0, 
			privateProjects = 0, 
			languages = 0,
			publicProjects = 0, 
			tags = 0, 
			teams = 0;

		String organizations = null;

		for (Statistics s : expectedstatisticsToBeAggregated) {
			contributers += s.getAllContributorsCount();
			externalContributors += s.getExternalContributorsCount();
			forks += s.getAllForksCount();
			size += s.getAllSizeCount();
			stars += s.getAllStarsCount();
			members += s.getMembersCount();
			privateProjects += s.getPrivateProjectCount();
			languages += s.getProgramLanguagesCount();
			publicProjects += s.getPublicProjectCount();
			tags += s.getTagsCount();
			teams += s.getTeamsCount();

			if (organizations == null)
				organizations = s.getOrganizationName();
			else
				organizations += ", " + s.getOrganizationName();
		}

		Assert.assertEquals(contributers, actual.getAllContributorsCount());
		Assert.assertEquals(externalContributors, actual.getExternalContributorsCount());
		Assert.assertEquals(forks, actual.getAllForksCount());
		Assert.assertEquals(size, actual.getAllSizeCount());
		Assert.assertEquals(stars, actual.getAllStarsCount());
		Assert.assertEquals(members, actual.getMembersCount());
		Assert.assertEquals(privateProjects, actual.getPrivateProjectCount());
		Assert.assertEquals(languages, actual.getProgramLanguagesCount());
		Assert.assertEquals(publicProjects, actual.getPublicProjectCount());
		Assert.assertEquals(tags, actual.getTagsCount());
		Assert.assertEquals(teams, actual.getTeamsCount());

		for (Statistics s : expectedstatisticsToBeAggregated) {
			if (s.getOrganizationName() == null)
				continue;

			Assert.assertTrue("Organization " + s.getOrganizationName() + " is missing",
					organizations.contains(s.getOrganizationName()));
		}
	}

	public static String createAbsoluteStatisticsUrl (String base, String organizations, String startDate, String endDate){
		String url = base + Constants.API_RESOURCE_STATISTICS;
		
		if(organizations!=null){
			url+="?"+Constants.API_REQUEST_PARAM_ORGANIZATIONS+"="+organizations;
		}
		
		
		if(startDate!=null){
			if(url.contains("?")){
				url += "&" + Constants.API_REQUEST_PARAM_STARTDATE+"="+startDate;
			}
			else{
				url += "?" + Constants.API_REQUEST_PARAM_STARTDATE+"="+startDate;
			}
		}
		
		
		if(endDate!=null){
			if(url.contains("?")){
				url += "&" + Constants.API_REQUEST_PARAM_ENDDATE+"="+endDate;
			}
			else{
				url += "?" + Constants.API_REQUEST_PARAM_ENDDATE+"="+endDate;
			}
		}
		
		return url;
	}
	
	public static String createAbsoluteLanguagesUrl (String base, String organizations, Integer limit, Integer offset, String query){
		String url = base + Constants.API_RESOURCE_LANGUAGES;
		
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
		
		if(organizations!=null) builder.queryParam(Constants.API_REQUEST_PARAM_ORGANIZATIONS, organizations);
		
		if(limit!=null) builder.queryParam(Constants.API_REQUEST_PARAM_LIMIT, limit);
		
		if(offset!=null) builder.queryParam(Constants.API_REQUEST_PARAM_OFFSET, offset);
		
		if(query!=null) builder.queryParam(Constants.API_REQUEST_PARAM_Q, query);
		
		return builder.toUriString();
	}
	
	
	public static String createRelativeStatisticsUrl (String organizations, String startDate, String endDate){
		return createAbsoluteStatisticsUrl("", organizations, startDate, endDate);
	}
}
