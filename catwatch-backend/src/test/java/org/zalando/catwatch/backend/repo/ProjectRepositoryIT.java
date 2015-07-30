package org.zalando.catwatch.backend.repo;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.zalando.catwatch.backend.repo.builder.BuilderUtil.freshId;
import static org.zalando.catwatch.backend.repo.builder.BuilderUtil.random;
import static org.zalando.catwatch.backend.repo.builder.BuilderUtil.randomLanguage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.zalando.catwatch.backend.model.Project;
import org.zalando.catwatch.backend.repo.builder.ProjectBuilder;

public class ProjectRepositoryIT extends AbstractRepositoryIT {

    @Autowired
    private ProjectRepository repository;

    @Test
    public void learningtestSaveAndLoad() throws Exception {

        // given
        Long gitHubProjectId = freshId();
        String name = "testProject";
        String language = randomLanguage();
        Integer forksCount = random(1, 4);
        Integer starsCount = random(1, 10);
        Integer commitsCount = random(1, 1000);
        Integer contributionCount = random(1, 1000);
        Integer score = random(1, 100);
        List<String> language_list = new ArrayList<>();
        language_list.add("Java");
        language_list.add("Scala");


        Project project =new ProjectBuilder(repository, new Date(), gitHubProjectId, name, language, forksCount, starsCount,
                commitsCount, contributionCount, score).organizationName("galanto").getProject();
        project.setLanguageList(language_list);
        repository.save(project);

        // when
        Project loadedProject = repository.findOne(project.getId());

        // then
        assertThat(loadedProject.getName(), equalTo("testProject"));
    }

}
