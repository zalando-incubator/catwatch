package org.zalando.catwatch.backend.repo;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.zalando.catwatch.backend.model.Project;
import org.zalando.catwatch.backend.repo.builder.ProjectBuilder;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=RANDOM_PORT)
public class ProjectStatsTests {

    @Autowired
    private ProjectRepository projects;

    @Test
    public void basicStatisticsTest() {
        projects.deleteAll();

        Date now = new Date();

        List<String> language_list = new ArrayList<>();
        language_list.add("Java");
        language_list.add("Scala");

        new ProjectBuilder(projects)
                .name("project-1")
                .commitsCount(100)
                .snapshotDate(now)
                .languages(language_list)
                .save();

        new ProjectBuilder(projects)
                .name("project-2")
                .commitsCount(50)
                .snapshotDate(Date.from(now.toInstant().minusSeconds(120)))
                .languages(language_list)
                .save();


        Date startDate = Date.from(now.toInstant().minusSeconds(60));

        for (Project p: projects.findProjectsByDateRange(startDate, now)) {
            System.out.printf("Project %s, commit %d, id %d %n", p.getName(), p.getCommitsCount(), p.getId());
        }
    }

}
