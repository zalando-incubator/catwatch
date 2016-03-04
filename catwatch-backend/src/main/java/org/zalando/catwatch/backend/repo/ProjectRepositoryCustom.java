package org.zalando.catwatch.backend.repo;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.zalando.catwatch.backend.model.Project;

interface ProjectRepositoryCustom {

    List<Project> findProjects(String organization, Optional<String> query, Optional<String> language);

    List<Project> findProjects(String organization, Date snapshotDate, Optional<String> query, Optional<String> language);
}
