package org.zalando.catwatch.backend.repo;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.zalando.catwatch.backend.model.Project;
import org.zalando.catwatch.backend.repo.builder.ProjectBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.zalando.catwatch.backend.repo.builder.BuilderUtil.*;

public class ProjectRepositoryIT extends AbstractRepositoryIT {

    private final Logger logger = LoggerFactory.getLogger(ProjectRepositoryIT.class);

    @Autowired
    private ProjectRepository repository;

    @Test
    public void learningtestSaveAndLoad() throws Exception {

        // given
        repository.deleteAll();

        Long gitHubProjectId = freshId();
        String name = "testProject";
        String language = randomLanguage();
        Integer forksCount = random(1, 4);
        Integer starsCount = random(1, 10);
        Integer commitsCount = random(1, 1000);
        Integer contributionCount = random(1, 1000);
        Integer externalContributionCount = random(0, contributionCount);
        Integer score = random(1, 100);
        List<String> language_list = new ArrayList<>();
        language_list.add("Java");
        language_list.add("Scala");

        Project project = new ProjectBuilder(repository, new Date(), gitHubProjectId, name, language, forksCount, starsCount,
            commitsCount, contributionCount, score, externalContributionCount).organizationName("galanto").getProject();
        project.setLanguageList(language_list);
        repository.save(project);

        // when
        Project loadedProject = repository.findOne(project.getId());

        // then
        assertThat(loadedProject.getName(), equalTo("testProject"));
    }

    @Test
    public void testFindProjectsWithDifferentSnapshotDate() {

        // given
        repository.deleteAll();

        new ProjectBuilder(repository)
            .snapshotDate(new Date(1000))
            .organizationName("test1")
            .name("p1")
            .save();

        new ProjectBuilder(repository)
            .snapshotDate(new Date(2000))
            .organizationName("test2")
            .name("p2")
            .save();

        // when then
        List<Project> projects1 = repository.findProjects("test1", Optional.empty(), Optional.empty());
        assertEquals(1, projects1.size());

        // when then
        List<Project> projects2 = repository.findProjects("test2", Optional.empty(), Optional.empty());
        assertEquals(1, projects2.size());
    }

    @Test
    public void testFindProjectsSnapshotDate(){

        // given
        repository.deleteAll();

        new ProjectBuilder(repository)
            .snapshotDate(new Date(1000))
            .organizationName("test_org")
            .name("p1")
            .save();

        new ProjectBuilder(repository)
            .snapshotDate(new Date(2000))
            .organizationName("test_org")
            .name("p1")
            .save();

        // when
        List<Project> projects = repository.findProjects("test_org", new Date(2000), Optional.empty(), Optional.empty());

        // then
        assertEquals(1, projects.size());
    }
}
