package org.zalando.catwatch.backend.github;

import org.kohsuke.github.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class RepositoryWrapper {

    private static final Logger logger = LoggerFactory.getLogger(RepositoryWrapper.class);

    private final GHRepository repository;
    private final GHOrganization organization;

    RepositoryWrapper(GHRepository repository, GHOrganization organization) {
        this.repository = repository;
        this.organization = organization;
    }

    public int getId() {
        return repository.getId();
    }

    public String getName() {
        return repository.getName();
    }

    public URL getUrl() {
        return repository.getHtmlUrl();
    }

    public String getDescription() {
        return repository.getDescription();
    }

    public int getStarsCount() {
        return repository.getWatchers();
    }

    public int getForksCount() {
        return repository.getForks();
    }

    public int getSize() {
        return repository.getSize();
    }

    public Date getLastPushed() {
        return repository.getPushedAt();
    }

    public String getPrimaryLanguage() {
        return repository.getLanguage();
    }

    public Map<String, Long> listLanguages() {
        try {
            return repository.listLanguages();
        } catch (IOException e) {
            logger.warn("Exception occurred while fetching languages of project '{}', organization '{}'.", repository.getName(), organization.getLogin());
            return Collections.<String, Long>emptyMap();
        }
    }

    public String getOrganizationName() {
        return organization.getLogin();
    }

    public List<GHCommit> listCommits() {
        try {
            return repository.listCommits().asList();
        } catch (Error e) {
            logger.warn("Exception occurred while fetching commits of project '{}', organization '{}'.", repository.getName(), organization.getLogin());
            return Collections.<GHCommit>emptyList();
        }
    }

    public List<GHRepository.Contributor> listContributors() {
        try {
            return repository.listContributors().asList();
        } catch (Throwable t) {
            logger.warn("Exception occurred while fetching contributors of project '{}', organization '{}'.", repository.getName(), organization.getLogin());
            return Collections.<GHRepository.Contributor>emptyList();
        }
    }

    public List<GHTag> listTags() {
        try {
            return repository.listTags().asList();
        } catch (Throwable t) {
            logger.warn("Exception occurred while fetching tags of project '{}', organization '{}'.", repository.getName(), organization.getLogin());
            return Collections.<GHTag>emptyList();
        }
    }

}
