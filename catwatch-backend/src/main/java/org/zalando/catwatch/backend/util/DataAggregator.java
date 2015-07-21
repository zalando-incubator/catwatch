package org.zalando.catwatch.backend.util;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.zalando.catwatch.backend.model.Statistics;

public class DataAggregator {

	public static Statistics aggregateStatistics(Collection<Statistics> statistics) throws IllegalArgumentException{
		
		if(statistics==null || statistics.size()==0){
			throw new IllegalArgumentException("Illegal number of statistics to aggregate");
		}
		
		String delimeter = ", ";

		if(statistics.size()==1) return statistics.iterator().next();
		
		Integer contributers = 0, forks = 0, size = 0, stars = 0, members = 0, privateProjects = 0, languages = 0,
				publicProjects = 0, tags = 0, teams = 0;

		
		Date snapshotDate = null;

		Set<String> organizationList = new HashSet<>();
		
		//aggregate data
		for (Statistics s : statistics) {
			contributers = add(contributers, s.getAllContributorsCount());
			forks = add(forks, s.getAllForksCount());
			size = add(size, s.getAllSizeCount());
			stars = add(stars, s.getAllStarsCount());
			members = add(members, s.getMembersCount());
			privateProjects = add(privateProjects, s.getPrivateProjectCount());
			languages = add(languages, s.getProgramLanguagesCount());
			publicProjects = add(publicProjects, s.getPublicProjectCount());
			tags = add(tags, s.getTagsCount());
			teams = add(teams, s.getTeamsCount());

			organizationList.add(s.getOrganizationName());
			
			
			if(snapshotDate==null) snapshotDate = s.getSnapshotDate();
			
			else if(snapshotDate.before(s.getSnapshotDate())) snapshotDate = s.getSnapshotDate();
		}
		
		String organizations = null;
		
		for(String org : organizationList){
			if (organizations == null)
			organizations = org;
		else
			organizations += delimeter + org;
		}
		
		//save aggegated values in new statistics object
		Statistics s = new Statistics(new Double(Math.random()*10000).intValue() , snapshotDate);
		s.setAllContributorsCount(contributers);
		s.setAllForksCount(forks);
		s.setAllSizeCount(size);
		s.setAllStarsCount(stars);
		s.setMembersCount(members);
		s.setOrganizationName(organizations);
		s.setPrivateProjectCount(privateProjects);
		s.setProgramLanguagesCount(languages);
		s.setPublicProjectCount(publicProjects);
		s.setTagsCount(tags);
		s.setTeamsCount(teams);

		return s;
	}
	
	
	private static Integer add(Integer sum, Integer value){
		
		int tempSum = sum == null ? 0 : sum.intValue();
		
		if(value!=null) tempSum += value;
		
		return tempSum;
	}
	
	
}
