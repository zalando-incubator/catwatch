package org.zalando.catwatch.backend.service;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import org.zalando.catwatch.backend.model.Project;
import org.zalando.catwatch.backend.repo.ProjectRepository;

/**
 * Created by mkunz on 7/22/15.
 */
@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    ProjectRepository projectRepository;

    @Override
    public Iterable<Project> findProjects(final String organizations, final Optional<Integer> limit,
            final Optional<Integer> offset, final Optional<Date> startDate, final Optional<Date> endDate,
            final Optional<String> sortBy, final Optional<String> query) {
        return projectRepository.findProjects(organizations, limit, offset, startDate, endDate, sortBy, query);
    }
}
