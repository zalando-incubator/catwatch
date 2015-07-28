package org.zalando.catwatch.backend.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.zalando.catwatch.backend.model.Language;
import org.zalando.catwatch.backend.model.Project;
import org.zalando.catwatch.backend.model.Statistics;
import org.zalando.catwatch.backend.repo.ProjectRepository;

public class DataAggregator {

	
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
		
		int tempSum = sum == null ? 0 : sum.intValue();
		
		if(value!=null) tempSum += value;
		
		return tempSum;
	}
	
	
    public static List<Language> filterLanguages(List<Language> languages, int limit,  int offset){
    	

         return  languages.stream().skip(offset).limit(limit).collect(Collectors.toList());

    }
    
    
    public static List<Language> getMainLanguages(final String organizations, final Comparator<Language> c, ProjectRepository repository, Optional<String> filterLanguage) {

        Collection<String> organizationList = StringParser.parseStringList(organizations, ",");
        List<Project> projectList = new ArrayList<>();

        // get the projects
        for (String org : organizationList) {

            Iterable<Project> projects = repository.findProjects(org, Optional.ofNullable(null), filterLanguage);

            Iterator<Project> iter = projects.iterator();
            while (iter.hasNext()) {
                projectList.add(iter.next());
            }
        }

        // count the languages

        List<String> languageList = new ArrayList<>();

        for (Project p : projectList) {
            languageList.add(p.getPrimaryLanguage());
        }

        List<Language> languages = new ArrayList<>();

        Set<String> languageSet = new HashSet<>(languageList);

        int frequency = 0;

        for (String language : languageSet) {
            Language l = new Language(language);
            frequency = Collections.frequency(languageList, language);

            l.setPercentage((int) Math.round(((double) frequency) / languageList.size() * 100));
            l.setProjectsCount(frequency);

            languages.add(l);
        }

        // sort
        if (languages.size() > 1) {
            Collections.sort(languages, c);
        }

        return languages;
    }
	
}
