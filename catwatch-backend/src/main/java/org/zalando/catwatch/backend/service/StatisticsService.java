package org.zalando.catwatch.backend.service;

import org.springframework.data.domain.PageRequest;
import org.zalando.catwatch.backend.model.Statistics;
import org.zalando.catwatch.backend.repo.StatisticsRepository;
import org.zalando.catwatch.backend.util.Constants;
import org.zalando.catwatch.backend.util.StringParser;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class StatisticsService {

	
	public static Collection<Statistics> getStatistics(StatisticsRepository repository, Collection<String> organizations, String startDate, String endDate){
		
		Collection<Statistics> statistics = new ArrayList<>(organizations.size());

		List<Statistics> unaggregatedStatistics = new ArrayList<>();

		if (startDate == null && endDate == null) {
			for (String orgName : organizations) {

				List<Statistics> s = repository.findByOrganizationNameOrderByKeySnapshotDateDesc(orgName,
						new PageRequest(0, 1));

				unaggregatedStatistics.addAll(s);
			}

			if (unaggregatedStatistics.size() > 0) {
				Statistics aggregatedStatistics = aggregateStatistics(unaggregatedStatistics);

				statistics.add(aggregatedStatistics);
			}

		} else {
			// filter by start and end date
			statistics = getStatisticsByDate(repository, organizations, startDate, endDate);
		}
		
		return statistics;
	}
	
	
	private static Collection<Statistics> getStatisticsByDate(StatisticsRepository repository, Collection<String> orgs, String startDate, String endDate) {

		Date start = null;
		Date end;
		try {
			if (startDate != null) {
				start = StringParser.parseIso8601Date(startDate);
			}
		} catch (ParseException e) {
			throw new IllegalArgumentException(Constants.ERR_MSG_WRONG_DATE_FORMAT + " for stardDate");
		}

		try {
			end = endDate == null ? new Date() : StringParser.parseIso8601Date(endDate);
		} catch (ParseException e) {
			throw new IllegalArgumentException(Constants.ERR_MSG_WRONG_DATE_FORMAT + " for endDate");
		}

		List<List<Statistics>> statisticsLists = collectStatistics(repository, orgs, start, end);

		return aggregateHistoricalStatistics(statisticsLists);
	}

	
	private static List<List<Statistics>> collectStatistics(StatisticsRepository repository, Collection<String> organizations, Date start, Date end) {
		
		List<List<Statistics>> statisticsLists = new ArrayList<>();
		
		// get statistics for each organization
		for (String orgName : organizations) {

			if (start == null) {
				Optional<Date> earliestSnapshot = repository.getEarliestSnaphotDate(orgName);
				if (earliestSnapshot.isPresent()) {
					start = earliestSnapshot.get();
				} else {
					continue;
				}
			} else {
				Optional<Date> earlierSnapshot = repository.getLatestSnaphotDateBefore(orgName, start);

				if (earlierSnapshot.isPresent()) {
					start = earlierSnapshot.get();
				} 
			}

			if (start.after(end)) {
				continue;
				// throw new IllegalArgumentException("Start date is after end
				// date");
			}

			List<Statistics> s = repository.findStatisticsByOrganizationAndDate(orgName, start, end);
			statisticsLists.add(s);
		}
		
		return statisticsLists;
	}
	
	
	
	/**
	 * Aggregates a collection of {@link Statistics} objects by adding up their field values
	 * 
	 * @param statistics The {@link Collection} of {@link Statistics} objects to be merged
	 * @return A {@link Statistics} object whole field values are the sum of the given field values of the input Statistics objects.
	 * @throws IllegalArgumentException If an invariant has been violated
	 */
	public static Statistics aggregateStatistics(Collection<Statistics> statistics) throws IllegalArgumentException{
		
		if(statistics==null || statistics.size()==0){
			throw new IllegalArgumentException("Illegal number of statistics to aggregate");
		}
		
		String delimeter = ", ";

		if(statistics.size()==1) return statistics.iterator().next();
		
		Integer contributers = 0, externalContributors = 0, forks = 0, size = 0, stars = 0, members = 0, privateProjects = 0, languages = 0,
				publicProjects = 0, tags = 0, teams = 0;

		
		Date snapshotDate = null;

		Set<String> organizationList = new HashSet<>();
		
		//aggregate data
		for (Statistics s : statistics) {
			contributers = add(contributers, s.getAllContributorsCount());
			externalContributors = add(externalContributors, s.getExternalContributorsCount());
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
		
		//save aggregated values in new statistics object
		Statistics s = new Statistics(new Double(Math.random()*10000).intValue() , snapshotDate);
		s.setAllContributorsCount(contributers);
		s.setExternalContributorsCount(externalContributors);
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
	
	
	public  static Collection<Statistics> aggregateHistoricalStatistics(List<List<Statistics>> statisticsLists){
		
		if(statisticsLists.isEmpty()) return Collections.emptyList();
		
		if(statisticsLists.size()==1){
			return statisticsLists.iterator().next();
		}
		
		//TODO check if the lists have the same size
		
		//map statistics of the different organizations
		List<Statistics> aggregatedStatistics = new ArrayList<>();
		int numberOfRecords = statisticsLists.get(0).size(); //assuming that all organizations have the same amount of records
		
		List<Statistics> unaggregatedStatistics;
		
		for (int i=0; i<numberOfRecords; i++){
			unaggregatedStatistics  = new ArrayList<>();
			for (List<Statistics> orgStats : statisticsLists){
				
				if(orgStats.isEmpty()) continue;
				
				//FIXME figure out how to map the records
				//for now just use the order
				unaggregatedStatistics.add(orgStats.get(i));
			}
			
			Statistics aggregatedRecord = aggregateStatistics(unaggregatedStatistics);
			
			aggregatedStatistics.add(aggregatedRecord);
		}

		return aggregatedStatistics;
	}
	
	
	private static Integer add(Integer sum, Integer value){
		
		int tempSum = sum == null ? 0 : sum;
		
		if(value!=null) tempSum += value;
		
		return tempSum;
	}
}