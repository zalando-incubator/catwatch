package org.zalando.catwatch.backend.service;

import org.zalando.catwatch.backend.model.Project;

import java.util.Date;
import java.util.Optional;

public interface ProjectService {

    Iterable<Project> findProjects(String organizations, Optional<Integer> limit, Optional<Integer> offset,
            Optional<Date> startDate, Optional<Date> endDate, Optional<String> sortBy, Optional<String> query,Optional<String> language);
}
