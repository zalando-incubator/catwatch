package org.zalando.catwatch.backend.repo.util;

import static org.zalando.catwatch.backend.repo.builder.BuilderUtil.freshId;
import static org.zalando.catwatch.backend.repo.builder.BuilderUtil.random;
import static org.zalando.catwatch.backend.repo.builder.BuilderUtil.randomDate;
import static org.zalando.catwatch.backend.repo.builder.BuilderUtil.randomLanguage;
import static org.zalando.catwatch.backend.repo.builder.BuilderUtil.randomProjectName;
import static org.zalando.catwatch.backend.repo.util.DatabasePing.isDatabaseAvailable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.stereotype.Component;

import org.zalando.catwatch.backend.model.Project;
import org.zalando.catwatch.backend.repo.ContributorRepository;
import org.zalando.catwatch.backend.repo.ProjectRepository;
import org.zalando.catwatch.backend.repo.StatisticsRepository;
import org.zalando.catwatch.backend.repo.builder.ContributorBuilder;
import org.zalando.catwatch.backend.repo.builder.ProjectBuilder;
import org.zalando.catwatch.backend.repo.builder.StatisticsBuilder;

@Component
public class DatabasePopulator {

    @Autowired
    private StatisticsRepository statisticsRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ContributorRepository contributorRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public StatisticsBuilder newStat() {
        return new StatisticsBuilder(statisticsRepository);
    }

    public ProjectBuilder newProject(final Date date, final Long gitHubProjectId, final String name,
            final String language, final Integer forksCount, final Integer starsCount, final Integer commitsCount,
            final Integer contributionCount, final Integer score) {

        return new ProjectBuilder(projectRepository, date, gitHubProjectId, name, language, forksCount, starsCount,
                commitsCount, contributionCount, score);
    }

    public ContributorBuilder newContributor() {
        return new ContributorBuilder(contributorRepository);
    }

    @PostConstruct
    public void postConstruct() {

        if (!isDatabaseAvailable(jdbcTemplate)) {
            return;                             // return so that the application context can start at least
        }

        // create statistics for two companies (latest)
        newStat()                           //
        .organizationName("galanto")        //
                    .publicProjectCount(34) //
                    .allStarsCount(54)      //
                    .allForksCount(110)     //
                    .days(1).save();
        newStat()                           //
        .organizationName("galanto-italic") //
                    .publicProjectCount(56) //
                    .allStarsCount(93)      //
                    .allForksCount(249)     //
                    .days(1).save();

        // create projects for galanto

        int amountSnapshots = 100;
        int projectsCount = 10;
        List<Date> snapshotList = getSnapshotDateList(amountSnapshots);

        for (int i = 0; i < projectsCount; i++) {

            Long gitHubProjectId = freshId();
            String name = randomProjectName();
            String language = randomLanguage();
            Integer forksCount = random(1, 4);
            Integer starsCount = random(1, 10);
            Integer commitsCount = random(1, 1000);
            Integer contributionCount = random(1, 1000);
            Integer score = random(1, 100);

            for (Date snapshot : snapshotList) {
                Project project = getProject(snapshot, gitHubProjectId, name, language, forksCount, starsCount,
                        commitsCount, contributionCount, score);
                projectRepository.save(project);
            }
        }

        // create contributors for galanto
        newContributor().organizationName("galanto").save();
        newContributor().organizationName("galanto").save();
    }

    private List<Date> getSnapshotDateList(final int amountSnapshots) {
        List<Date> snapshotList = new ArrayList<>();
        for (int i = 0; i < amountSnapshots; i++) {
            snapshotList.add(randomDate());
        }

        return snapshotList;
    }

    private Project getProject(final Date date, final Long gitHubProjectId, final String name, final String language,
            final Integer forksCount, final Integer starsCount, final Integer commitsCount,
            final Integer contributionCount, final Integer score) {

        return newProject(date, gitHubProjectId, name, language, forksCount, starsCount, commitsCount,
                contributionCount, score).organizationName("galanto").getProject();

    }

}
