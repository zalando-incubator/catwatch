package org.zalando.catwatch.backend.github;

import org.zalando.catwatch.backend.model.Contributor;
import org.zalando.catwatch.backend.model.Language;
import org.zalando.catwatch.backend.model.Project;
import org.zalando.catwatch.backend.model.Statistics;

import java.util.Collection;

public class Snapshot {

    private final Statistics statistics;
    private final Collection<Project> projects;
    private final Collection<Contributor> contributors;
    private final Collection<Language> languages;

    public Snapshot(Statistics statistics,
                    Collection<Project> projects,
                    Collection<Contributor> contributors,
                    Collection<Language> languages) {
        this.statistics = statistics;
        this.projects = projects;
        this.contributors = contributors;
        this.languages = languages;
    }

    public Statistics getStatistics() {
        return statistics;
    }

    public Collection<Project> getProjects() {
        return projects;
    }

    public Collection<Contributor> getContributors() {
        return contributors;
    }

    public Collection<Language> getLanguages() {
        return languages;
    }
}
