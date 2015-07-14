package org.zalando.catwatch.backend.repo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.zalando.catwatch.backend.CatWatchBackendApplication;
import org.zalando.catwatch.backend.model.Contributor;
import org.zalando.catwatch.backend.repo.ContributorRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CatWatchBackendApplication.class)
public class ContributorRepositoryIT {
	
    @Autowired
    ContributorRepository repository;
    
	@Test
	public void learningtestSaveAndLoad() throws Exception {
		
        // save a couple of contributors
        repository.save(new Contributor("Jack"));
        repository.save(new Contributor("Chloe"));
        repository.save(new Contributor("Kim"));
        repository.save(new Contributor("David"));
        repository.save(new Contributor("Michelle"));

        // fetch all contributors
        System.out.println("Contributors found with findAll():");
        System.out.println("-------------------------------");
        for (Contributor contributor : repository.findAll()) {
            System.out.println(contributor);
        }
        System.out.println();

        // fetch an individual contributor by ID
        Contributor contributor = repository.findOne(1L);
        System.out.println("Contributor found with findOne(1L):");
        System.out.println("--------------------------------");
        System.out.println(contributor);
        System.out.println();

        // fetch contributors by name
        System.out.println("Contributor found with findByName('Kim'):");
        System.out.println("--------------------------------------------");
        for (Contributor bauer : repository.findByName("Kim")) {
            System.out.println(bauer);
        }
    }

}
