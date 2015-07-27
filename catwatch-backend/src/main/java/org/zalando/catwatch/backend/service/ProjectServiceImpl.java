package org.zalando.catwatch.backend.service;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import org.zalando.catwatch.backend.model.Project;
import org.zalando.catwatch.backend.repo.ProjectRepository;

import com.google.common.base.Splitter;

/**
 * Created by mkunz on 7/22/15.
 */
@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    ProjectRepository projectRepository;

    private static final String SORT_ORDER_DESC = "-";

    private static final Integer DEFAULT_LIMIT = 5;

    private static final Integer DEFAULT_OFFSET = 0;

    @Override
    public Iterable<Project> findProjects(final String organizations, final Optional<Integer> limit,
            final Optional<Integer> offset, final Optional<Date> startDate, final Optional<Date> endDate,
            final Optional<String> sortBy, final Optional<String> query) {

        List<Project> resultList = new ArrayList<>();
        for (String organization : getOrganizations(organizations)) {
            if (startDate.isPresent() && endDate.isPresent()) {
                List<Project> startProjects = projectRepository.findProjects(organization, startDate.get(), query);
                List<Project> endProjects = projectRepository.findProjects(organization, endDate.get(), query);
                resultList.addAll(getMergedProjectList(startProjects, endProjects));
            } else if (startDate.isPresent() && !endDate.isPresent()) {
                List<Project> startProjects = projectRepository.findProjects(organization, startDate.get(), query);
                List<Project> endProjects = projectRepository.findProjects(organization, query);
                resultList.addAll(getMergedProjectList(startProjects, endProjects));
            } else if (!startDate.isPresent() && endDate.isPresent()) {
                // TODO: not sure what to do here
            } else {
                List<Project> projects = projectRepository.findProjects(organization, query);
                resultList.addAll(projects);
            }
        }

        if (sortBy.isPresent()) {
            resultList = getSortedResultList(sortBy.get(), resultList);
        }

        Integer limitVal = limit.orElse(DEFAULT_LIMIT);
        Integer offsetVal = offset.orElse(DEFAULT_OFFSET);

        return resultList.stream().skip(offsetVal).limit(limitVal).collect(Collectors.toList());
    }

    private List<Project> getSortedResultList(final String sortBy, final List<Project> resultList) {

        // TODO
        return resultList;
    }

    private List<Project> getMergedProjectList(final List<Project> projectsStart, final List<Project> projectsEnd) {
        Map<Long, Project> startProjectMap = convertProjectsToMap(projectsStart);
        Map<Long, Project> endProjectMap = convertProjectsToMap(projectsEnd);

        List<Project> resultList = new ArrayList<>();

        for (Map.Entry<Long, Project> entry : endProjectMap.entrySet()) {
            if (startProjectMap.containsKey(entry.getKey())) {
                resultList.add(createMergedProject(startProjectMap.get(entry.getKey()), entry.getValue()));
            } else {
                resultList.add(entry.getValue());
            }
        }

        return resultList;
    }

    private Project createMergedProject(final Project startProject, final Project endProject) {
        endProject.setStarsCount(endProject.getStarsCount() - startProject.getStarsCount());
        endProject.setCommitsCount(endProject.getCommitsCount() - startProject.getCommitsCount());
        endProject.setForksCount(endProject.getForksCount() - startProject.getForksCount());
        endProject.setContributorsCount(endProject.getContributorsCount() - startProject.getContributorsCount());
        endProject.setScore(endProject.getScore() - startProject.getScore());
        return endProject;
    }

    private Map<Long, Project> convertProjectsToMap(final List<Project> projects) {
        Map<Long, Project> projectMap = new HashMap<>();
        for (Project project : projects) {
            projectMap.put(project.getGitHubProjectId(), project);
        }

        return projectMap;
    }

    private Iterable<String> getOrganizations(final String organizations) {
        return Splitter.on(',').trimResults().omitEmptyStrings().split(organizations);
    }
}
