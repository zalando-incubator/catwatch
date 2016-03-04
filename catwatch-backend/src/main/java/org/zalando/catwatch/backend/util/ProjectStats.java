package org.zalando.catwatch.backend.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.zalando.catwatch.backend.model.Project;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Class to collect Projects stats
 *
 * This class collects the statistics from a List of Project objects in a more compact manner
 * so that it can then be returned from the /statistics/projects REST endpoint.
 *
 * Essentially, the fields which are always the same (such as "name"...) are stored only once
 * and the counts (such as "commitCounts") are put into arrays of their own.
 *
 */
public class ProjectStats {

    private String name;
    private String organizationName;
    private String url;
    private String description;
    private String primaryLanguage;
    private List<Integer> commitCounts;
    private List<Integer> forkCounts;
    private List<Integer> contributorsCounts;
    private List<Integer> scores;
    private List<Date> snapshotDates;

    public ProjectStats(List<Project> projects) {
        Project first = projects.get(0);

        name = first.getName();
        organizationName = first.getOrganizationName();
        url = first.getUrl();
        description = first.getDescription();
        primaryLanguage = first.getPrimaryLanguage();

        projects.sort((o1, o2) -> o1.getSnapshotDate().compareTo(o2.getSnapshotDate()));

        int size = projects.size();

        commitCounts = new ArrayList<>(size);
        forkCounts = new ArrayList<>(size);
        snapshotDates = new ArrayList<>(size);
        contributorsCounts = new ArrayList<>(size);
        scores = new ArrayList<>(size);

        int i = 0;

        Date lastSnapshotDate = null;

        for (Project p: projects) {
            if (!p.getName().equals(name)) {
                throw new IllegalArgumentException("All projects in the list must refer to the same project!");
            }

            if (lastSnapshotDate == null || !p.getSnapshotDate().equals(lastSnapshotDate)) {
                commitCounts.add(i, p.getCommitsCount());
                forkCounts.add(i, p.getForksCount());
                contributorsCounts.add(i, p.getContributorsCount());
                scores.add(i, p.getScore());
                snapshotDates.add(i, p.getSnapshotDate());
                i++;
            }

            lastSnapshotDate = p.getSnapshotDate();
        }
    }

    @JsonProperty(value="name")
    public String getName() { return name; }

    @JsonProperty(value="organization_name")
    public String getOrganizationName() { return organizationName; }

    @JsonProperty(value="url")
    public String getUrl() { return url; }

    @JsonProperty(value="primary_language")
    public String getPrimaryLanguage() { return primaryLanguage; }

    @JsonProperty(value="description")
    public String getDescription() { return description; }

    @JsonProperty(value="commit_counts")
    public List<Integer> getCommitCounts() { return commitCounts; }

    @JsonProperty(value="fork_counts")
    public List<Integer> getForkCounts() { return forkCounts; }

    @JsonProperty(value="contributors_counts")
    public List<Integer> getContributorsCounts() { return contributorsCounts; }

    @JsonProperty(value="scores")
    public List<Integer> getScores() { return scores; }

    @JsonProperty(value="snapshot_dates")
    @JsonSerialize(using = JsonDateListSerializer.class)
    public List<Date> getSnapshotDates() { return snapshotDates; }

    public static List<ProjectStats> buildStats(List<Project> projects) {
        Map<String,List<Project>> projectsByName = getDistinctProjects(projects);

        return projectsByName.values().stream()
                .map(ProjectStats::new)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Take a list of projects and return a map of lists where projects have been partitioned
     * by names.
     *
     * @param projects list consisting of potentially different projects
     * @return a map which contains the list separated by project, using the name as index.
     */
    public static Map<String,List<Project>> getDistinctProjects(List<Project> projects)  {
        Map<String,List<Project>> result = new HashMap<>();

        for (Project project: projects) {
            String name = project.getName();

            List<Project> list = result.get(name);
            if (list == null) {
                list = new LinkedList<>();
                result.put(name, list);
            }

            list.add(project);
        }

        return result;
    }

    @Override
    public String toString() {
        return String.format("ProjectStats(%s, commitCounts=%s, forkCounts=%s, snapshotDates=%s)", name, commitCounts, forkCounts, snapshotDates);
    }
}
