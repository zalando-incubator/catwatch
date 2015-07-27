package org.zalando.catwatch.backend.repo.populate;

import static org.zalando.catwatch.backend.repo.populate.BuilderUtil.freshId;
import static org.zalando.catwatch.backend.repo.populate.BuilderUtil.random;
import static org.zalando.catwatch.backend.repo.populate.BuilderUtil.randomDate;
import static org.zalando.catwatch.backend.repo.populate.BuilderUtil.randomLanguage;
import static org.zalando.catwatch.backend.repo.populate.BuilderUtil.randomProjectName;

import java.util.Date;
import java.util.List;

import org.zalando.catwatch.backend.model.Project;
import org.zalando.catwatch.backend.repo.ProjectRepository;

public class ProjectBuilder {

    private Project project;

    private ProjectRepository projectRepository;

    public ProjectBuilder(final ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;

        Date date = randomDate();
        for (int i = 0; i < 5; i++) {
            project = new Project();
            project.setGitHubProjectId(freshId());
            project.setSnapshotDate(date);
            project.setName(randomProjectName());
            project.setPrimaryLanguage(randomLanguage());
            project.setForksCount(random(1, 10));
            project.setStarsCount(random(1, 4));
            project.setCommitsCount(random(1, 1000));
        }
    }

    private void updateUrl() {
        project.setUrl("https://github.com/" + project.getOrganizationName() + "/" + project.getName());
    }

    public ProjectBuilder name(final String name) {
        project.setName(name);
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

    public ProjectBuilder score(final List<String> languageList) {
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
