package org.zalando.catwatch.backend.util;

import org.zalando.catwatch.backend.model.Contributor;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.*;

/**
 * Class to collect the statistics of the contributors
 *
 * This class collect the statistics from a List of Contributor objects and
 * put them in a more compact way.
 * Specifically, the fields which have always the same value are storaged for
 * only once. The counts and dates are put into an array of their own.
 *
 * Created by nwang on 13/08/15.
 */
public class ContributorStats {
    private String name;
    private String organizationName;
    private String url;
    private List<Integer> organizationalCommitsCounts;
    private List<Integer> personalCommitsCounts;
    private List<Integer> organizationalProjectsCounts;
    private List<Integer> personalProjectsCounts;
    private List<Date> snapshotDates;

    public ContributorStats(List<Contributor> contributions) {
        Contributor first = contributions.get(0);
        name = first.getName();
        organizationName = first.getOrganizationName();
        url = first.getUrl();
        contributions.sort((o1, o2) -> o1.getSnapshotDate().compareTo(o2.getSnapshotDate()));
        int size = contributions.size();
        organizationalCommitsCounts = new ArrayList<>(size);
        personalCommitsCounts = new ArrayList<>(size);
        organizationalProjectsCounts = new ArrayList<>(size);
        personalProjectsCounts = new ArrayList<>(size);
        snapshotDates = new ArrayList<>(size);
        int i = 0;
        Date lastSnapshotDate = null;
        for (Contributor c : contributions) {
            if (c.getId() != first.getId()) {
                throw new IllegalArgumentException("All the contributors in the list must have the same name.");
            }
            if (lastSnapshotDate == null || !c.getSnapshotDate().equals(lastSnapshotDate)) {
                organizationalCommitsCounts.add(i, c.getOrganizationalCommitsCount());
                personalCommitsCounts.add(i, c.getPersonalCommitsCount());
                organizationalProjectsCounts.add(i, c.getOrganizationalProjectsCount());
                personalProjectsCounts.add(i, c.getPersonalProjectsCount());
                snapshotDates.add(i, c.getSnapshotDate());
                i++;
            }
            lastSnapshotDate = c.getSnapshotDate();
        }
    }

    @JsonProperty(value="name")
    public String getName() {return name;}

    @JsonProperty(value="organization_name")
    public String getOrganizationName() {return organizationName;}

    @JsonProperty(value="url")
    public String getUrl() {return url;}

    @JsonProperty(value="organization_commit_counts")
    public List<Integer> getOrganizationalCommitsCounts() {return organizationalCommitsCounts;}

    @JsonProperty(value="personal_commit_counts")
    public List<Integer> getPersonalCommitsCounts() {return personalCommitsCounts;}

    @JsonProperty(value="organization_project_counts")
    public List<Integer> getOrganizationalProjectsCounts() {return organizationalProjectsCounts;}

    @JsonProperty(value="personal_project_counts")
    public List<Integer> getPersonalProjectsCounts() {return personalProjectsCounts;}

    @JsonProperty(value="snapshot_dates")
    @JsonSerialize(using = JsonDateListSerializer.class)
    public List<Date> getSnapshotDates() { return snapshotDates; }

    /**
     * Take a list of contributors and return a list their statistics that are seperated
     * by different contributors.
     *
     * @param contributors  a list consisting of contributors.
     * @return a list, each entry of which consists of statistics of the same contributors
     * at different date.
     */
    public static List<ContributorStats> buildStats(List<Contributor> contributors) {
        Map<String, List<Contributor>> contributorByID = getDistinctContributors(contributors);
        List<ContributorStats> result = new LinkedList<>();
        for (List<Contributor> contributions : contributorByID.values()) {
            result.add(new ContributorStats(contributions));
        }
        return result;
    }

    /**
     * Take a list of contributors and return a map of lists where contributors have been partitioned
     * by id.
     *
     * @param contributors a list consisting of potentially different contributors.
     * @return a map which contains the list separated by contributors, using the id as index.
     */
    public static Map<String, List<Contributor>> getDistinctContributors(List<Contributor> contributors) {
        Map<String, List<Contributor>> result = new HashMap<>();
        for (Contributor contributor : contributors) {
            String id = String.valueOf(contributor.getId());
            List<Contributor> list = result.get(id);
            if (list == null) {
                list = new LinkedList<>();
                result.put(id, list);
            }
            list.add(contributor);
        }
        return result;
    }

    @Override
    public String toString() {
        return String.format(
                "ContributorStats(%s, %s, %s, %s)", name, organizationName, url, snapshotDates);
    }
}
