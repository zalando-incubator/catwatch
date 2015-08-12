package org.zalando.catwatch.backend.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.zalando.catwatch.backend.model.Project;

import java.util.*;

/**
 * Created by mibraun on 12/08/15.
 */
public class ProjectStats {

    private String name;
    private List<Integer> commitCounts;
    private List<Integer> forkCounts;
    private List<Date> snapshotDates;

    public ProjectStats(List<Project> projects) {
        Project first = projects.get(0);

        name = first.getName();

        projects.sort(new Comparator<Project>() {
            @Override
            public int compare(Project o1, Project o2) {
                return o1.getSnapshotDate().compareTo(o2.getSnapshotDate());
            }
        });

        int size = projects.size();

        commitCounts = new ArrayList<>(size);
        forkCounts = new ArrayList<>(size);
        snapshotDates = new ArrayList<>(size);

        int i = 0;

        for (Project p: projects) {
            if (!p.getName().equals(name)) {
                throw new IllegalArgumentException("All projects in the list must refer to the same project!");
            }

            commitCounts.add(i, p.getCommitsCount());
            forkCounts.add(i, p.getForksCount());
            snapshotDates.add(i, p.getSnapshotDate());

            i++;
        }
    }

    @JsonProperty(value="name")
    public String getName() { return name; }

    @JsonProperty(value="commit_counts")
    public List<Integer> getCommitCounts() { return commitCounts; }

    @JsonProperty(value="fork_counts")
    public List<Integer> getForkCounts() { return forkCounts; }

    @JsonProperty(value="snaphost_dates")
    public List<Date> getSnapshotDates() { return snapshotDates; }

    public static List<ProjectStats> buildStats(List<Project> projects) {
        Map<String,List<Project>> projectsByName = getDistinctProjects(projects);

        List<ProjectStats> result = new LinkedList<>();

        for(List<Project> ps: projectsByName.values()) {
            result.add(new ProjectStats(ps));
        }

        return result;
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
