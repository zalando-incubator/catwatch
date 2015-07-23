package org.zalando.catwatch.backend.service;

import java.util.Date;
import java.util.Optional;

import org.zalando.catwatch.backend.model.Project;

/**
 * Created by mkunz on 7/22/15.
 */
public interface ProjectService {

    Iterable<Project> findProjects(String organizations, Optional<Integer> limit, Optional<Integer> offset,
            Optional<Date> startDate, Optional<Date> endDate, Optional<String> sortBy, Optional<String> query);
}
