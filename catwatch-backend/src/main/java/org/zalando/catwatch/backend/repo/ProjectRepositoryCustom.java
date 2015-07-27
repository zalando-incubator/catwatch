package org.zalando.catwatch.backend.repo;

import java.util.Date;
import java.util.Optional;

import org.zalando.catwatch.backend.model.Project;

/**
 * Created by mkunz on 7/22/15.
 */
interface ProjectRepositoryCustom {
    Iterable<Project> findProjects(String organizations, Optional<Integer> limit, Optional<Integer> offset,
            Optional<Date> snapshotDate, Optional<String> sortBy, Optional<String> query);
}
