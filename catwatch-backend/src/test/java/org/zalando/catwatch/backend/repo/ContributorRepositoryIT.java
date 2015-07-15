package org.zalando.catwatch.backend.repo;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.zalando.catwatch.backend.CatWatchBackendApplication;
import org.zalando.catwatch.backend.model.Contributor;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CatWatchBackendApplication.class)
public class ContributorRepositoryIT {
	
    @Autowired
    ContributorRepository repository;
    
	@Test
	public void learningtestSaveAndLoad() throws Exception {
		
        // given
        repository.save(new Contributor("Jack"));
        repository.save(new Contributor("Chloe"));
        Contributor kim = new Contributor("Kim");
		repository.save(kim);

        // when
        Contributor loadedContributor = repository.findOne(kim.getId());

        // then
        assertThat(loadedContributor.getName(), equalTo("Kim"));

        // when
        loadedContributor = repository.findByName("Kim").get(0);
        
        // then
        assertThat(loadedContributor.getName(), equalTo("Kim"));    }

}
