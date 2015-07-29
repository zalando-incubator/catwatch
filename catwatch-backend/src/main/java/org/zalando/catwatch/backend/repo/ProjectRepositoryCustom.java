package org.zalando.catwatch.backend.repo;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.zalando.catwatch.backend.model.Project;

/**
 * Created by mkunz on 7/22/15.
 */
interface ProjectRepositoryCustom {

    List<Project> findProjects(String organization, Optional<String> query, Optional<String> language);

    List<Project> findProjects(String organization, Date snapshotDate, Optional<String> query, Optional<String> language);
}
