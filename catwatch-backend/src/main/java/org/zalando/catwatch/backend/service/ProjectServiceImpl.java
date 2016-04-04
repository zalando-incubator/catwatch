package org.zalando.catwatch.backend.service;

import com.google.common.base.Splitter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.zalando.catwatch.backend.model.Project;
import org.zalando.catwatch.backend.repo.ProjectRepository;
import org.zalando.catwatch.backend.service.comparator.ProjectCommitComparator;
import org.zalando.catwatch.backend.service.comparator.ProjectContributorComparator;
import org.zalando.catwatch.backend.service.comparator.ProjectForkComparator;
import org.zalando.catwatch.backend.service.comparator.ProjectScoreComparator;
import org.zalando.catwatch.backend.service.comparator.ProjectStarComparator;
import org.zalando.catwatch.backend.util.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl implements ProjectService {

    private static final String SORT_ORDER_DESC = "-";
    private static final Integer DEFAULT_LIMIT = 5;
    private static final Integer DEFAULT_OFFSET = 0;

    private final ProjectRepository projectRepository;
    private final Environment env;

    @Autowired
    public ProjectServiceImpl(ProjectRepository projectRepository, Environment env) {
        this.projectRepository = projectRepository;
        this.env = env;
    }

    @Override
    public Iterable<Project> findProjects(final String organizations, final Optional<Integer> limit,
                                          final Optional<Integer> offset, final Optional<Date> startDate, final Optional<Date> endDate,
                                          final Optional<String> sortBy, final Optional<String> query, final Optional<String> language) {

        List<Project> resultList = new ArrayList<>();
        for (String organization : getOrganizations(organizations)) {
            if (startDate.isPresent() && endDate.isPresent()) {
                List<Project> startProjects = projectRepository.findProjects(organization, startDate.get(), query,language);
                List<Project> endProjects = projectRepository.findProjects(organization, endDate.get(), query, language);
                resultList.addAll(getMergedProjectList(startProjects, endProjects));
            } else if (startDate.isPresent() && !endDate.isPresent()) {
                List<Project> startProjects = projectRepository.findProjects(organization, startDate.get(), query,language);
                List<Project> endProjects = projectRepository.findProjects(organization, query,language);
                resultList.addAll(getMergedProjectList(startProjects, endProjects));
            } else if (!startDate.isPresent() && endDate.isPresent()) {
                List<Project> projects = projectRepository.findProjects(organization, endDate.get(), query, language);
                resultList.addAll(projects);
            } else {
                List<Project> projects = projectRepository.findProjects(organization, query, language);
                resultList.addAll(projects);
            }
        }

        resultList = getSortedResultList(resultList, sortBy);

        Integer limitVal = limit.orElse(DEFAULT_LIMIT);
        Integer offsetVal = offset.orElse(DEFAULT_OFFSET);

        return resultList.stream().skip(offsetVal).limit(limitVal).collect(Collectors.toList());
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

    private List<Project> getSortedResultList(final List<Project> projectList, final Optional<String> sortBy) {

        if (isSortOrderAscending(sortBy)) {
            Collections.sort(projectList, getProjectSortComparator(sortBy, true));
        } else {
            Collections.sort(projectList, Collections.reverseOrder(getProjectSortComparator(sortBy, false)));
        }

        return projectList;
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

    private Iterable<String> getOrganizations(String organizations) {

        if (organizations == null) {
            organizations = getOrganizationConfig();
        }

        return Splitter.on(',').trimResults().omitEmptyStrings().split(organizations);
    }

    /**
     * This method returns the list of organizations string.
     *
     * @return
     */
    private String getOrganizationConfig() {

        if (!env.containsProperty(Constants.CONFIG_ORGANIZATION_LIST)) {

            // TODO throw new exception
            return "";
        }

        return env.getProperty(Constants.CONFIG_ORGANIZATION_LIST);
    }

    /**
     * This returns sorting order.
     *
     * @param sortBy
     * @return
     */
    private boolean isSortOrderAscending(final Optional<String> sortBy) {

        if (sortBy.isPresent()) {
            String sortColumn = sortBy.get();
            return !sortColumn.startsWith(SORT_ORDER_DESC);
        } else {
            return false;
        }
    }

    /**
     * This returns the required sorting comparator class.
     *
     * @param sortBy
     * @param sortOrderAscending
     * @return
     */
    private Comparator<Project> getProjectSortComparator(final Optional<String> sortBy, final boolean sortOrderAscending) {

        String sortColumn;

        if (sortBy.isPresent()) {

            if (!sortOrderAscending) {
                sortColumn = sortBy.get().substring(1);
            } else {
                sortColumn = sortBy.get();
            }

        } else {
            sortColumn = ProjectSortColumn.SCORE;
        }

        switch (sortColumn) {

            case ProjectSortColumn.STARS_COUNT:
                return new ProjectStarComparator();

            case ProjectSortColumn.SCORE:
                return new ProjectScoreComparator();

            case ProjectSortColumn.COMMITS_COUNT:
                return new ProjectCommitComparator();

            case ProjectSortColumn.FORKS_COUNT:
                return new ProjectForkComparator();

            case ProjectSortColumn.CONTRIBUTION_COUNT:
                return new ProjectContributorComparator();

            default:
                return new ProjectScoreComparator();
        }

    }

}
