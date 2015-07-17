package org.zalando.catwatch.backend.scheduler;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.zalando.catwatch.backend.github.GithubConnector;
import org.zalando.catwatch.backend.repo.ContributorRepository;
import org.zalando.catwatch.backend.repo.ProjectRepository;
import org.zalando.catwatch.backend.repo.StatisticsRepository;

/**
 * 
 * @author pmaji
 *
 */
@Component
public class TaskScheduler {
	
	
	@Autowired
	private Environment env;
	@Autowired
    private ProjectRepository projectRepository;
	@Autowired
    private StatisticsRepository statisticsRepository;
	@Autowired
    private ContributorRepository contributorRepository;
	@Autowired
	private GithubConnector connector;

	/**
	 * This is used to fetch Organization statistics from GitHub
	 *  (This runs at 8 AM everyday)
	 */
    @Scheduled(cron="0 8 * * * ?")
    public void fetchOrganizationStatistics() {
    	fetchOrganizationStatistics(getOrganizationNames());
    }

    void fetchOrganizationStatistics(List<String> organizationList) {
        for(String organization : organizationList){
        	statisticsRepository.save(connector.findStatistics(organization));
        }
    }

    
    /**
	 * This is used to fetch Project List from GitHub
	 *  (This runs at 8 AM everyday)
	 */
    @Scheduled(cron="0 8 * * * ?")
    public void fetchProjects() {
    	fetchOrganizationProjects(getOrganizationNames());
    }
    
    void fetchOrganizationProjects(List<String> organizationList) {
        for(String organization : organizationList){
        	projectRepository.save(connector.findProjects(organization));
        }
    }
    
    /**
	 * This is used to fetch Contributor statistics from GitHub
	 *  (This runs at 8 AM everyday)
	 */
    @Scheduled(cron="0 8 * * * ?")
    public void fetchContributors() {
    	fetchOrganizationContributors(getOrganizationNames());
    }
    
    void fetchOrganizationContributors(List<String> organizationList) {
        for(String organization : organizationList){
        	contributorRepository.save(connector.findContributors(organization));
        }
    }
    
    private List<String> getOrganizationNames() {
    	String organizationNames = env.getProperty("organization.list");
    	List<String> organizationList = Arrays.asList(organizationNames.split("\\s*,\\s*"));
    	return organizationList;
    }
    
}
