package org.zalando.catwatch.backend.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zalando.catwatch.backend.model.Project;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class LanguageStats {

    private static final Logger logger = LoggerFactory.getLogger(LanguageStats.class);

    private String languageName;
    private List<Integer> projectCounts;
    private List<Date> snapshotDates;

    public static String UNKNOWN = "unknown";

    public LanguageStats(String languageName, List<Integer> projectCounts, List<Date> snapshotDates) {
        if (languageName == null) {
            this.languageName = UNKNOWN;
        } else {
            this.languageName = languageName;
        }
        this.projectCounts = projectCounts;
        this.snapshotDates = snapshotDates;
    }

    /**
     * Go through all the projects and collect the counts per snapshot and
     * per language.
     *
     * @param projectList
     * @return
     */
    public static List<LanguageStats> buildStats(List<Project> projectList) {
        List<Project> projects = filterUniqueSnapshots(projectList);

        // For each date, we have a map of all the counts. Later we piece the
        // results together from these pieces of information.
        Map<Date, Map<String,Integer>> counts = new HashMap<>();
        TreeSet<Date> dates = new TreeSet<>();
        Set<String> languages = new HashSet<>();

        for (Project p: projects) {
            String language = p.getPrimaryLanguage();
            Date date = p.getSnapshotDate();

            if (language == null)
                language = "unknown";

            dates.add(date);
            languages.add(language);

            Map<String,Integer> hist = counts.get(date);
            if (hist == null) {
                hist = new HashMap<>();
                counts.put(date, hist);
            }

            if (hist.containsKey(language)) {
                hist.put(language, hist.get(language) + 1);
            } else {
                hist.put(language, 1);
            }
        }

        List<LanguageStats> result = new ArrayList<>();
        for (String l: languages) {
            List<Integer> projectCounts = new ArrayList<>();
            List<Date> snapshotDates = new ArrayList<>(dates);

            for(Date d: snapshotDates) {
                Integer i = counts.get(d).get(l);
                if (i == null) {
                    projectCounts.add(0);
                } else {
                    projectCounts.add(i);
                }
            }
            result.add(new LanguageStats(l, projectCounts, snapshotDates));
        }
        return result;
    }

    /**
     * For some reason, there are duplicate snapshots sometimes. This method takes care of that
     * and removes the duplicates.
     *
     * @param projects
     * @return
     */
    public static List<Project> filterUniqueSnapshots(List<Project> projects) {
        Set<String> nameAndDateSet = new HashSet<>();

        int newCount = 0;
        int oldCount = 0;

        List<Project> result = new ArrayList<>();
        for (Project p: projects) {
            String key = p.getPrimaryLanguage() +
                    ":" + p.getName() +
                    ":" + p.getOrganizationName() +
                    ":" + p.getSnapshotDate().getTime();

            if (!nameAndDateSet.contains(key)) {
                newCount++;
                result.add(p);
                nameAndDateSet.add(key);
            } else {
                oldCount++;
            }
        }

        return result;
    }

    @JsonProperty(value="name")
    public String getLanguageName() {
        return languageName;
    }

    @JsonProperty(value="project_counts")
    public List<Integer> getProjectCounts() {
        return projectCounts;
    }

    @JsonProperty(value="snapshot_dates")
    @JsonSerialize(using = JsonDateListSerializer.class)
    public List<Date> getSnapshotDates() {
        return snapshotDates;
    }
}
