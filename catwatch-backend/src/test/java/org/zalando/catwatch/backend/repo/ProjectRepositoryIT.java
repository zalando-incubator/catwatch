package org.zalando.catwatch.backend.repo;

import org.junit.Test;

import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.SpringApplicationConfiguration;

import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.zalando.catwatch.backend.CatWatchBackendApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CatWatchBackendApplication.class)
public class ProjectRepositoryIT {

    @Autowired
    ProjectRepository repository;

    @Test
    public void learningtestSaveAndLoad() throws Exception { }

}
