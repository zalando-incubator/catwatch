package org.zalando.catwatch.backend.repo.populate;

import static org.zalando.catwatch.backend.repo.populate.BuilderUtil.random;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.catwatch.backend.repo.ContributorRepository;
import org.zalando.catwatch.backend.repo.ProjectRepository;
import org.zalando.catwatch.backend.repo.StatisticsRepository;

@Component
public class DatabasePopulator {

	@Autowired
	private StatisticsRepository statisticsRepository;

	@Autowired
	private ProjectRepository projectRepository;

	@Autowired
	private ContributorRepository contributorRepository;

	public StatisticsBuilder newStat() {
		return new StatisticsBuilder(statisticsRepository);
	}

	public ProjectBuilder newProject() {
		return new ProjectBuilder(projectRepository);
	}

	public ContributorBuilder newContributor() {
		return new ContributorBuilder(contributorRepository);
	}

	@PostConstruct
	public void postConstruct() {

		// create statistics for two companies (latest)
		newStat() //
				.organizationName("galanto") //
				.publicProjectCount(34) //
				.allStarsCount(54) //
				.allForksCount(110) //
				.days(1).save();
		newStat() //
				.organizationName("galanto-italic") //
				.publicProjectCount(56) //
				.allStarsCount(93) //
				.allForksCount(249) //
				.days(1).save();

		// create projects for galanto
		int numProjects = random(50, 150);
		for (int i = 0; i < numProjects; i++) {
			newProject().organizationName("galanto").save();
		}

		// create contributors for galanto
		newContributor().organizationName("galanto").save();
		newContributor().organizationName("galanto").save();
	}

}
