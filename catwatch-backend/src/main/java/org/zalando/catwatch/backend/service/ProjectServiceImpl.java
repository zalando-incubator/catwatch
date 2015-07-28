package org.zalando.catwatch.backend.service;

import java.util.*;
import java.util.stream.Collectors;

import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.expr.ComparableExpressionBase;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import org.zalando.catwatch.backend.model.Project;
import org.zalando.catwatch.backend.model.QProject;
import org.zalando.catwatch.backend.model.Statistics;
import org.zalando.catwatch.backend.model.sort.ProjectSortColumn;
import org.zalando.catwatch.backend.repo.ProjectRepository;
import org.zalando.catwatch.backend.service.comparator.*;
import org.zalando.catwatch.backend.util.Constants;
import org.zalando.catwatch.backend.util.StringParser;

import com.google.common.base.Splitter;

/**
 * Created by mkunz on 7/22/15.
 */
@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    private Environment env;

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
                List<Project> projects = projectRepository.findProjects(organization, endDate.get(), query);
                resultList.addAll(projects);
            } else {
                List<Project> projects = projectRepository.findProjects(organization, query);
                resultList.addAll(projects);
            }
        }

        resultList = getSortedResultList(resultList,sortBy);

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

    private List<Project> getSortedResultList(List<Project> projectList,Optional<String>  sortBy){

        if (isSortOrderAscending(sortBy)) {
            Collections.sort(projectList, getProjectSortComparator(sortBy, true));
        } else {
            Collections.sort(projectList,
                    Collections.reverseOrder(getProjectSortComparator(sortBy,false)));
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

        if(organizations==null){
            organizations = getOrganizationConfig();
        }

        return Splitter.on(',').trimResults().omitEmptyStrings().split(organizations);
    }

    /**
     * This method returns the list of organizations string
     * @return
     */
    private String getOrganizationConfig() {

        if(!env.containsProperty(Constants.CONFIG_ORGANIZATION_LIST)){
            //TODO throw new exception
            return "";
        }

        return env.getProperty(Constants.CONFIG_ORGANIZATION_LIST);
    }

    /**
     * This returns sorting order
     * @param sortBy
     * @return
     */
    private boolean isSortOrderAscending(Optional<String>  sortBy) {

        if (sortBy.isPresent()) {
            String sortColumn = sortBy.get();
            if (sortColumn.startsWith(SORT_ORDER_DESC)) {
                return false;
            }else {
               return true;
            }
        } else {
            return false;
        }
    }

    /**
     * This returns the required sorting comparator class
     * @param sortBy
     * @param sortOrderAscending
     * @return
     */
    private Comparator getProjectSortComparator(Optional<String> sortBy, boolean sortOrderAscending) {

        String sortColumn = null;

        if (sortBy.isPresent()) {

            if (!sortOrderAscending) {
                sortColumn = sortBy.get().substring(1);
            } else {
                sortColumn = sortBy.get();
            }

        } else {
            sortColumn = ProjectSortColumn.COMMITS_COUNT;
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
                return new ProjectCommitComparator();
        }


    }

}
