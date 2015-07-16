package org.zalando.catwatch.backend.repo;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.zalando.catwatch.backend.CatWatchBackendApplication;
import org.zalando.catwatch.backend.model.Project;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CatWatchBackendApplication.class)
public class ProjectRepositoryIT {
	
    @Autowired
    ProjectRepository repository;
    
	@Test
	public void learningtestSaveAndLoad() throws Exception {
		
        // given
		Project project = new Project(123, new Date());
		project.setName("p1");
        repository.save(project);
        
        // when
        Project loadedProject = repository.findOne(project.getKey());

        // then
        assertThat(loadedProject.getName(), equalTo("p1"));
    }

}
