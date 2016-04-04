package org.zalando.catwatch.backend.repo.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.zalando.catwatch.backend.repo.ContributorRepository;
import org.zalando.catwatch.backend.repo.ProjectRepository;
import org.zalando.catwatch.backend.repo.StatisticsRepository;
import org.zalando.catwatch.backend.repo.builder.BuilderUtil;
import org.zalando.catwatch.backend.repo.builder.ContributorBuilder;
import org.zalando.catwatch.backend.repo.builder.ProjectBuilder;
import org.zalando.catwatch.backend.repo.builder.StatisticsBuilder;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.zalando.catwatch.backend.repo.builder.BuilderUtil.*;
import static org.zalando.catwatch.backend.repo.util.DatabasePing.isDatabaseAvailable;

@Component
public class DatabasePopulator {

    private final StatisticsRepository statisticsRepository;
    private final ProjectRepository projectRepository;
    private final ContributorRepository contributorRepository;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DatabasePopulator(StatisticsRepository statisticsRepository,
                             ProjectRepository projectRepository,
                             ContributorRepository contributorRepository,
                             JdbcTemplate jdbcTemplate) {
        this.statisticsRepository = statisticsRepository;
        this.projectRepository = projectRepository;
        this.contributorRepository = contributorRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    public void populateTestData() {

        if (!isDatabaseAvailable(jdbcTemplate)) {
            return;                             // return so that the application context can start at least
        }

        // create statistics for two companies (latest)
        new StatisticsBuilder(statisticsRepository) //
            .organizationName("galanto") //
            .publicProjectCount(34) //
            .allStarsCount(54) //
            .allForksCount(110) //
            .days(1)
            .save();

        new StatisticsBuilder(statisticsRepository) //
            .organizationName("galanto-italic") //
            .publicProjectCount(56) //
            .allStarsCount(93) //
            .allForksCount(249) //
            .days(1)
            .save();

        // create projects for galanto
        List<Date> snapshots = Stream
            .generate(BuilderUtil::randomDate)
            .limit(100)
            .collect(Collectors.toList());

        for (int i = 0; i < 10; i++) {
            Long gitHubProjectId = freshId();
            String name = randomProjectName();
            String language = randomLanguage();

            for (Date snapshot : snapshots) {
                new ProjectBuilder(projectRepository)
                    .organizationName("galanto")
                    .name(name)
                    .gitHubProjectId(gitHubProjectId)
                    .primaryLanguage(language)
                    .snapshotDate(snapshot)
                    .forksCount(random(1, 4))
                    .starsCount(random(1, 10))
                    .commitsCount(random(1, 1000))
                    .contributorsCount(random(1, 1000))
                    .score(random(1, 100))
                    .save();
            }
        }

        // create contributors for galanto
        for (int i = 0; i < 2; i++) {
            new ContributorBuilder(contributorRepository)
                .organizationName("galanto")
                .save();
        }
    }
}
