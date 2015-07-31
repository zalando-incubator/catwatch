package org.zalando.catwatch.backend.github;

import org.kohsuke.github.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class OrganizationWrapper {

    private static final Logger logger = LoggerFactory.getLogger(OrganizationWrapper.class);

    private final GHOrganization organization;
    private final List<RepositoryWrapper> repositories;

    OrganizationWrapper(GHOrganization organization) {
        this.organization = organization;
        List<RepositoryWrapper> repositories;
        try {
            repositories = organization.listRepositories().asList().stream()
                    .filter(r -> !r.isPrivate())
                    .filter(r -> !r.isFork())
                    .map(repository -> new RepositoryWrapper(repository, organization))
                    .collect(Collectors.toList());
        } catch (Throwable t) {
            logger.warn("Exception occurred while fetching public members of organization '{}'.", organization.getLogin());
            repositories = Collections.<RepositoryWrapper>emptyList();
        }
        this.repositories = Collections.unmodifiableList(repositories);
    }

    public List<GHTeam> listTeams() {
        try {
            return organization.listTeams().asList();
        } catch (Throwable t) {
            logger.warn("Exception occurred while fetching teams of organization '{}'.", organization.getLogin());
            return Collections.<GHTeam>emptyList();
        }
    }

    public List<GHUser> listMembers() {
        try {
            return organization.listMembers().asList();
        } catch (Throwable t) {
            logger.warn("Exception occurred while fetching public members of organization '{}'.", organization.getLogin());
            return Collections.<GHUser>emptyList();
        }
    }

    public List<RepositoryWrapper> listRepositories() {
        return repositories;
    }

    public int getId() {
        return organization.getId();
    }

    public int getPublicRepoCount() {
        try {
            return organization.getPublicRepoCount();
        } catch (IOException e) {
            logger.warn("Exception occurred while fetching public repositories count of organization '{}'.", organization.getLogin());
            return 0;
        }
    }

    public String getLogin() {
        return organization.getLogin();
    }
}
