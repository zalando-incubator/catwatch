package org.zalando.catwatch.backend.repo.builder;

import org.zalando.catwatch.backend.model.Project;
import org.zalando.catwatch.backend.repo.ProjectRepository;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.DAYS;

public class ProjectBuilder {

    private static Instant now = now();

    private Project project;

    private ProjectRepository projectRepository;

    public ProjectBuilder() {
        this(null);
    }

    public ProjectBuilder(final ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
        this.project = new Project();
    }

    public ProjectBuilder(final ProjectRepository projectRepository, final Date date, final Long gitHubProjectId,
                          final String name, final String language, final Integer forksCount, final Integer starsCount,
                          final Integer commitsCount, final Integer contributionCount, final Integer score,
                          final Integer externalContributorsCount) {
        this.projectRepository = projectRepository;

        project = new Project();
        project.setGitHubProjectId(gitHubProjectId);
        project.setSnapshotDate(date);
        project.setName(name);
        project.setPrimaryLanguage(language);
        project.setForksCount(forksCount);
        project.setStarsCount(starsCount);
        project.setCommitsCount(commitsCount);
        project.setContributorsCount(contributionCount);
        project.setExternalContributorsCount(externalContributorsCount);
        project.setScore(score);
    }

    private void updateUrl() {
        project.setUrl("https://github.com/" + project.getOrganizationName() + "/" + project.getName());
    }

    public Project getProject() {
        return project;
    }

    public ProjectBuilder name(final String name) {
        project.setName(name);
        return this;
    }

    public ProjectBuilder days(int numDaysBeforeNow) {
        project.setSnapshotDate(Date.from(now.minus(numDaysBeforeNow, DAYS)));
        return this;
    }

    public ProjectBuilder gitHubProjectId(final long gitHubProjectId) {
        project.setGitHubProjectId(gitHubProjectId);
        return this;
    }

    public ProjectBuilder snapshotDate(final Date snapshotDate) {
        project.setSnapshotDate(snapshotDate);
        return this;
    }

    public ProjectBuilder organizationName(final String organizationName) {
        project.setOrganizationName(organizationName);
        return this;
    }

    public ProjectBuilder primaryLanguage(final String primaryLanguage) {
        project.setPrimaryLanguage(primaryLanguage);
        return this;
    }

    public ProjectBuilder forksCount(final int forksCount) {
        project.setForksCount(forksCount);
        return this;
    }

    public ProjectBuilder starsCount(final int starsCount) {
        project.setStarsCount(starsCount);
        return this;
    }

    public ProjectBuilder commitsCount(final int commitsCount) {
        project.setCommitsCount(commitsCount);
        return this;
    }

    public ProjectBuilder contributorsCount(final int contributorsCount) {
        project.setContributorsCount(contributorsCount);
        return this;
    }

    public ProjectBuilder externalContributorsCount(final int externalContributorsCount) {
        project.setContributorsCount(externalContributorsCount);
        return this;
    }

    public ProjectBuilder description(final String description) {
        project.setDescription(description);
        return this;
    }

    public ProjectBuilder lastPushed(final String lastPushed) {
        project.setLastPushed(lastPushed);
        return this;
    }

    public ProjectBuilder score(final int score) {
        project.setScore(score);
        return this;
    }

    public ProjectBuilder languages(final List<String> languageList) {
        project.setLanguageList(languageList);
        return this;
    }

    public Project create() {

        updateUrl();

        Project p = new Project();
        p.setGitHubProjectId(project.getGitHubProjectId());
        p.setSnapshotDate(project.getSnapshotDate());
        p.setName(project.getName());
        p.setPrimaryLanguage(project.getPrimaryLanguage());
        p.setForksCount(project.getForksCount());
        p.setStarsCount(project.getStarsCount());
        p.setOrganizationName(project.getOrganizationName());
        p.setCommitsCount(project.getCommitsCount());
        p.setContributorsCount(project.getContributorsCount());
        p.setExternalContributorsCount(project.getExternalContributorsCount());
        p.setDescription(project.getDescription());
        p.setLastPushed(project.getLastPushed());
        p.setScore(project.getScore());
        p.setLanguageList(project.getLanguageList());
        return p;
    }

    public Project save() {
        return projectRepository.save(create());
    }

}
