package org.zalando.catwatch.backend.github;

import org.kohsuke.github.GHOrganization;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHTeam;
import org.kohsuke.github.GHUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Wrapper for GHOrganization object.
 * <p>
 * The objective of this class is to deal with exceptions during fetching
 * data via Kohsuke GitHub API so that TakeSnapshotTask is kept free from
 * try/catch clutter. For some reason the library throws Error on empty
 * responses (e.g. no contributors for project). Another scenario for
 * throwing an exception is having not enough rights to see private
 * details (e.g. teams of organization).
 *
 * @see GHOrganization
 */
public class OrganizationWrapper {

    private static final Logger logger = LoggerFactory.getLogger(OrganizationWrapper.class);

    private final GHOrganization organization;
    private final List<RepositoryWrapper> repositories;
    private List<GHUser> members;
    private List<GHTeam> teams;

    OrganizationWrapper(GHOrganization organization) {
        this.organization = organization;
        List<RepositoryWrapper> repositories;
        try {
            repositories = organization.listRepositories().asList().stream()
                    .filter(r -> !r.isPrivate()) // deal with public repos only
                    .filter(r -> !r.isFork())    // skip forks as they change statistics significantly
                    .map(repository -> new RepositoryWrapper(repository, organization))
                    .collect(Collectors.toList());
        } catch (Throwable t) {
            logger.warn("No repositories found for organization '{}'.", organization.getLogin());
            repositories = Collections.<RepositoryWrapper>emptyList();
        }
        this.repositories = Collections.unmodifiableList(repositories);
    }

    public List<GHTeam> listTeams() {
        if (this.teams == null) {
            try {
                this.teams = organization.listTeams().asList();
            } catch (Throwable t) {
                logger.warn("No teams found for organization '{}'.", organization.getLogin());
                this.teams = Collections.<GHTeam>emptyList();
            }
        }
        return  this.teams;
    }

    public List<GHUser> listMembers() {
        if (this.members == null) {
            try {
                this.members = organization.listMembers().asList();
            } catch (Throwable t) {
                logger.warn("No public members found for organization '{}'.", organization.getLogin());
                this.members = Collections.<GHUser>emptyList();
            }
        }
        return this.members;
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
            logger.warn("No public repositories count found for organization '{}'.", organization.getLogin());
            return 0;
        }
    }

    public String getLogin() {
        return organization.getLogin();
    }

    public Boolean contributorIsMember(GHRepository.Contributor contributor) {
        return this.listMembers().contains(contributor);
    }
}
