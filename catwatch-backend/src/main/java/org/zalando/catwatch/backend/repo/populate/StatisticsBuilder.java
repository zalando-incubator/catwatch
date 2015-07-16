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

	public StatisticsBuilder days(int numDaysBeforeNow) {
		statistics.getKey().setSnapshotDate(Date.from(now().minus(numDaysBeforeNow, DAYS)));
		return this;
	}

	public Statistics create() {
		Statistics s = new Statistics(statistics.getId(), statistics.getSnapshotDate());
		s.setPublicProjectCount(statistics.getPrivateProjectCount());
		s.setAllStarsCount(statistics.getAllStarsCount());
		s.setAllForksCount(statistics.getAllForksCount());
		s.setProgramLanguagesCount(statistics.getProgramLanguagesCount());
		s.setAllContributorsCount(statistics.getAllContributorsCount());
		s.setMembersCount(statistics.getMembersCount());
		s.setAllForksCount(statistics.getAllForksCount());
		return s;
	}

	public Statistics save() {
		return statisticsRepository.save(create());
	}

}
