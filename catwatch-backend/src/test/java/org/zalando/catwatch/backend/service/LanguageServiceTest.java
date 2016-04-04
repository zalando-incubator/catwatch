package org.zalando.catwatch.backend.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zalando.catwatch.backend.model.Language;
import org.zalando.catwatch.backend.model.Project;
import org.zalando.catwatch.backend.repo.ProjectRepository;
import org.zalando.catwatch.backend.repo.builder.ProjectBuilder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.util.Optional.empty;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LanguageServiceTest {

    public static final Logger logger = LoggerFactory.getLogger(LanguageServiceTest.class);

    @Mock
    ProjectRepository projectRepository;

    @InjectMocks
    LanguageService languageService;

    /**
     *  Checking if the language with name null is ignored
     */
    @Test
    public void checkProgrammingLanguage() {

        logger.info("Setting up the projects...");
        Project p1 = new ProjectBuilder().commitsCount(10)
                .contributorsCount(5)
                .forksCount(1)
                .gitHubProjectId(12345)
                .description("bogus project 1")
                .name("bogus project 1")
                .primaryLanguage("Java")
                .organizationName("zalando-stups")
                .getProject();

        Project p2 = new ProjectBuilder().commitsCount(10)
                .contributorsCount(5)
                .forksCount(1)
                .gitHubProjectId(12345)
                .description("bogus project 2")
                .name("bogus project 2")
                .primaryLanguage("Scala")
                .organizationName("zalando-stups")
                .getProject();


        Project p3 = new ProjectBuilder().commitsCount(10)
                .contributorsCount(5)
                .forksCount(1)
                .gitHubProjectId(12345)
                .description("bogus project 3")
                .name("bogus project 3")
                .primaryLanguage("C++")
                .organizationName("zalando")
                .getProject();

        Project p4 = new ProjectBuilder().commitsCount(10)
                .contributorsCount(5)
                .forksCount(1)
                .gitHubProjectId(12345)
                .description("bogus project 4")
                .name("bogus project 4")
                .primaryLanguage(null)
                .organizationName("zalando")
                .getProject();

        projectRepository.save(p1);
        projectRepository.save(p2);
        projectRepository.save(p3);
        projectRepository.save(p4);


        String organizations = "zalando,zalando-stups";

        logger.info("Calling language service...");
        List<Project> projectsZalando = new ArrayList<>();
        List<Project> projectsZalandoStups = new ArrayList<>();

        projectsZalandoStups.add(p1);
        projectsZalandoStups.add(p2);

        projectsZalando.add(p3);
        projectsZalando.add(p4);

        // given
        when(projectRepository.findProjects("zalando", empty(), empty())).thenReturn(projectsZalando);
        when(projectRepository.findProjects("zalando-stups", empty(), empty())).thenReturn(projectsZalandoStups);

        // when
        List<Language> result = languageService.getMainLanguages(organizations, new LanguagePercentComparator(), empty());
        Assert.assertEquals(3, result.size());
    }

    private class LanguagePercentComparator implements Comparator<Language> {

        @Override
        public int compare(final Language l1, final Language l2) {

            if(l1.getProjectsCount()<l2.getProjectsCount()) return 1;

            return -1;

        }
    }
}
