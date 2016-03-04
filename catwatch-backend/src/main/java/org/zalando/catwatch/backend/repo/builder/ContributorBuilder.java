package org.zalando.catwatch.backend.repo.builder;

import org.zalando.catwatch.backend.model.Contributor;
import org.zalando.catwatch.backend.repo.ContributorRepository;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.DAYS;
import static org.zalando.catwatch.backend.repo.builder.BuilderUtil.*;

public class ContributorBuilder {

    private static Instant now = now();

    private Contributor contributor;

    private ContributorRepository contributorRepository;

    public ContributorBuilder() {
        this(null);
    }

    public ContributorBuilder(ContributorRepository contributorRepository) {
        this.contributorRepository = contributorRepository;
        contributor = new Contributor(freshId(), freshId(), randomDate());
        contributor.setName("" + UUID.randomUUID());
        contributor.setUrl("https://github.com/" + contributor.getName());
        contributor.setOrganizationName("" + UUID.randomUUID());
        contributor.setOrganizationalCommitsCount(50 + random(1, 40));
        contributor.setOrganizationalProjectsCount(5 + random(1, 4));
        contributor.setPersonalCommitsCount(random(1, 40));
        contributor.setPersonalProjectsCount(random(1, 4));
    }

    public ContributorBuilder id(long id) {
        contributor.getKey().setId(id);
        return this;
    }

    public ContributorBuilder url(String url) {
        contributor.setUrl(url);
        return this;
    }

    public ContributorBuilder name(String name) {
        contributor.setName(name);
        return this;
    }

    public ContributorBuilder days(int numDaysBeforeNow) {
        contributor.getKey().setSnapshotDate(Date.from(now.minus(numDaysBeforeNow, DAYS)));
        return this;
    }

    public ContributorBuilder organizationName(String organizationName) {
        contributor.setOrganizationName(organizationName);
        return this;
    }

    public ContributorBuilder organizationId(long organizationId) {
        contributor.getKey().setOrganizationId(organizationId);
        return this;
    }

    public ContributorBuilder orgCommits(Integer organizationalCommitsCount) {
        contributor.setOrganizationalCommitsCount(organizationalCommitsCount);
        return this;
    }

    public ContributorBuilder orgProjects(Integer organizationalProjectsCount) {
        contributor.setOrganizationalProjectsCount(organizationalProjectsCount);
        return this;
    }

    public ContributorBuilder persProjects(Integer personalProjectsCount) {
        contributor.setPersonalProjectsCount(personalProjectsCount);
        return this;
    }

    public ContributorBuilder persCommits(Integer personalCommitsCount) {
        contributor.setPersonalCommitsCount(personalCommitsCount);
        return this;
    }

    public ContributorBuilder snapshotDate(final Date snapshotDate) {
        contributor.getKey().setSnapshotDate(snapshotDate);
        return this;
    }

    public Contributor create() {
        Contributor c = new Contributor(contributor.getId(), contributor.getOrganizationId(),
                contributor.getSnapshotDate());
        c.setName(contributor.getName());
        c.setUrl(contributor.getUrl());
        c.setOrganizationName(contributor.getOrganizationName());
        c.setOrganizationalCommitsCount(contributor.getOrganizationalCommitsCount());
        c.setOrganizationalProjectsCount(contributor.getOrganizationalProjectsCount());
        c.setPersonalCommitsCount(contributor.getPersonalCommitsCount());
        c.setPersonalProjectsCount(contributor.getPersonalProjectsCount());
        return c;
    }

    public Contributor save() {
        return contributorRepository.save(create());
    }

}
