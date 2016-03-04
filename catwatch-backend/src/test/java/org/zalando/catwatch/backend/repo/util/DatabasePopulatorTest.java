package org.zalando.catwatch.backend.repo.util;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.zalando.catwatch.backend.model.Contributor;
import org.zalando.catwatch.backend.model.Project;
import org.zalando.catwatch.backend.model.Statistics;
import org.zalando.catwatch.backend.repo.AbstractRepositoryIT;
import org.zalando.catwatch.backend.repo.ContributorRepository;
import org.zalando.catwatch.backend.repo.ProjectRepository;
import org.zalando.catwatch.backend.repo.StatisticsRepository;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 *
 * @author ayastrebov
 */
public class DatabasePopulatorTest extends AbstractRepositoryIT {

    @Autowired
    private DatabasePopulator populator;

    @Autowired
    private StatisticsRepository statisticsRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ContributorRepository contributorRepository;

    @Test
    public void testPopulateTestData() {
        populator.populateTestData();

        List<Statistics> galantos = statisticsRepository.findByOrganizationName("galanto");
        assertEquals(1, galantos.size());

        List<Project> projects = projectRepository.findProjects("galanto", Optional.empty(), Optional.empty());
        assertFalse(projects.isEmpty());

        List<Contributor> contributors = contributorRepository.findContributorsByOrganizationAndDate(Collections.singleton("galanto"), new Date(0), new Date());
        assertFalse(contributors.isEmpty());
    }
}
