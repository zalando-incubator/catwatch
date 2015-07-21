package org.zalando.catwatch.backend.repo.populate;

import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.DAYS;
import static org.zalando.catwatch.backend.repo.populate.BuilderUtil.freshId;
import static org.zalando.catwatch.backend.repo.populate.BuilderUtil.random;
import static org.zalando.catwatch.backend.repo.populate.BuilderUtil.randomDate;

import java.util.Date;

import org.zalando.catwatch.backend.model.Statistics;
import org.zalando.catwatch.backend.repo.StatisticsRepository;

public class StatisticsBuilder {

	private Statistics statistics;

	private StatisticsRepository statisticsRepository;

	public StatisticsBuilder(StatisticsRepository statisticsRepository) {
		this.statisticsRepository = statisticsRepository;
		statistics = new Statistics(freshId(), randomDate());
		statistics.setPublicProjectCount(random(50, 120));
		statistics.setAllStarsCount(random(1, 5));
		statistics.setAllForksCount(random(1, 10));
		statistics.setProgramLanguagesCount(random(1, 20));
		statistics.setMembersCount(random(1, 20));
		statistics.setAllForksCount(random(1, 20));
	}

	public StatisticsBuilder organizationName(String organizationName) {
		statistics.setOrganizationName(organizationName);
		return this;
	}

	public StatisticsBuilder publicProjectCount(int publicProjectCount) {
		statistics.setPublicProjectCount(publicProjectCount);
		return this;
	}

	public StatisticsBuilder allStarsCount(int allStarsCount) {
		statistics.setAllStarsCount(allStarsCount);
		return this;
	}

	public StatisticsBuilder allForksCount(int allForksCount) {
		statistics.setAllForksCount(allForksCount);
		return this;
	}

	public StatisticsBuilder programLanguagesCount(int programLanguagesCount) {
		statistics.setProgramLanguagesCount(programLanguagesCount);
		return this;
	}
	
	public StatisticsBuilder allContributersCount(int contributersCount) {
		statistics.setAllContributorsCount(contributersCount);
		return this;
	}
	
	public StatisticsBuilder allSize(int sizeCount) {
		statistics.setAllSizeCount(sizeCount);
		return this;
	}
	
	public StatisticsBuilder membersCount(int membersCount) {
		statistics.setMembersCount(membersCount);
		return this;
	}
	
	public StatisticsBuilder privateProjectCount(int privateProjectCount) {
		statistics.setPrivateProjectCount(privateProjectCount);
		return this;
	}
	
	public StatisticsBuilder tagsCount(int tagsCount) {
		statistics.setTagsCount(tagsCount);
		return this;
	}
	
	public StatisticsBuilder teamsCount(int teamsCount) {
		statistics.setTeamsCount(teamsCount);
		return this;
	}
	
	public StatisticsBuilder snapshotDate(Date snapshotDate) {
		statistics.setSnapshotDate(snapshotDate);
		return this;
	}

	public StatisticsBuilder days(int numDaysBeforeNow) {
		statistics.getKey().setSnapshotDate(Date.from(now().minus(numDaysBeforeNow, DAYS)));
		return this;
	}

	public Statistics create() {
		Statistics s = new Statistics(statistics.getId(), statistics.getSnapshotDate());
		s.setPublicProjectCount(statistics.getPublicProjectCount());
		s.setAllStarsCount(statistics.getAllStarsCount());
		s.setAllForksCount(statistics.getAllForksCount());
		s.setProgramLanguagesCount(statistics.getProgramLanguagesCount());
		s.setAllContributorsCount(statistics.getAllContributorsCount());
		s.setMembersCount(statistics.getMembersCount());
		s.setAllForksCount(statistics.getAllForksCount());
		s.setTeamsCount(statistics.getTeamsCount());
		s.setTagsCount(statistics.getTagsCount());
		s.setPrivateProjectCount(statistics.getPrivateProjectCount());
		s.setMembersCount(statistics.getMembersCount());
		s.setAllSizeCount(statistics.getAllSizeCount());
		s.setOrganizationName(statistics.getOrganizationName());
		return s;
	}

	public Statistics save() {
		return statisticsRepository.save(create());
	}

}
