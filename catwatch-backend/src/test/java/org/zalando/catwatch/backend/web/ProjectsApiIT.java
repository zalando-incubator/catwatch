package org.zalando.catwatch.backend.web;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;
import org.zalando.catwatch.backend.model.Project;
import org.zalando.catwatch.backend.repo.ProjectRepository;
import org.zalando.catwatch.backend.repo.builder.ProjectBuilder;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.HOURS;
import static org.junit.Assert.*;
import static org.springframework.web.util.UriComponentsBuilder.fromHttpUrl;
import static org.zalando.catwatch.backend.util.Constants.*;

public class ProjectsApiIT extends AbstractCatwatchIT {

    @Autowired
    private ProjectRepository projectRepository;

    private static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

    @Before
    public void init() { }

    @Test
    public void testProjectsGet() {

        // tear down
        projectRepository.deleteAll();

        Instant instant = Instant.now();

        Date snapshotDate1 = Date.from(instant.minus(3, DAYS));

        Project project1 = new ProjectBuilder(projectRepository).name("catwatch").snapshotDate(snapshotDate1)
                                                                .organizationName("zalando").gitHubProjectId(73561571)
                                                                .starsCount(4000).contributorsCount(300)
                                                                .externalContributorsCount(30).score(79)
                                                                .commitsCount(100).forksCount(70)
                                                                .languages(Arrays.asList("Java", "Python", "Scala"))
                                                                .save();

        Date snapshotDate2 = Date.from(instant.minus(1, DAYS));

        Project project2 = new ProjectBuilder(projectRepository).name("catwatch").snapshotDate(snapshotDate2)
                                                                .organizationName("zalando").gitHubProjectId(73561571)
                                                                .starsCount(5000).score(90).contributorsCount(500)
                                                                .externalContributorsCount(50).commitsCount(130)
                                                                .forksCount(90)
                                                                .languages(Arrays.asList("Java", "Python", "Scala"))
                                                                .save();

        testWithNoDates(project2);
        testWithStartAndEndDate(snapshotDate1, project1, snapshotDate2, project2);

    }

    private void testWithStartAndEndDate(final Date snapshotDate1, final Project project1, final Date snapshotDate2,
            final Project project2) {
        String url = projectUrl().queryParam(API_REQUEST_PARAM_ORGANIZATIONS, "zalando")
                                 .queryParam(API_REQUEST_PARAM_STARTDATE, new SimpleDateFormat(DATE_PATTERN).format(
                                         Date.from(snapshotDate1.toInstant().plus(1, HOURS))))
                                 .queryParam(API_REQUEST_PARAM_ENDDATE, new SimpleDateFormat(DATE_PATTERN).format(
                    Date.from(snapshotDate2.toInstant().plus(1, HOURS)))).toUriString();

        ResponseEntity<Project[]> response = template.getForEntity(url, Project[].class);

        assertNotNull(response);

        Project[] projects = response.getBody();
        assertNotNull(projects);
        assertTrue(projects.length == 1);

        Project responseProject = projects[0];
        assertEquals(responseProject.getStarsCount(),
            Integer.valueOf(project2.getStarsCount() - project1.getStarsCount()));
    }

    private void testWithNoDates(final Project project2) {
        String url = projectUrl().queryParam(API_REQUEST_PARAM_ORGANIZATIONS, "zalando").toUriString();

        ResponseEntity<Project[]> response = template.getForEntity(url, Project[].class);

        assertNotNull(response);

        Project[] projects = response.getBody();
        assertNotNull(projects);
        assertTrue(projects.length == 1);

        Project responseProject = projects[0];
        assertEquals(responseProject.getStarsCount(), project2.getStarsCount());
        assertEquals(responseProject.getScore(), project2.getScore());
        assertEquals(responseProject.getContributorsCount(), project2.getContributorsCount());
        assertEquals(responseProject.getExternalContributorsCount(), project2.getExternalContributorsCount());
        assertEquals(responseProject.getCommitsCount(), project2.getCommitsCount());
        assertEquals(responseProject.getScore(), project2.getScore());
    }

    private UriComponentsBuilder projectUrl() {
        return fromHttpUrl(base.toString() + API_RESOURCE_PROJECTS);
    }

}
