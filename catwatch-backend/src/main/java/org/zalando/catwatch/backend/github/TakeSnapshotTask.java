package org.zalando.catwatch.backend.github;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.collect.Lists;
import org.apache.tomcat.util.http.fileupload.util.Streams;
import org.kohsuke.github.GHObject;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.RateLimitHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zalando.catwatch.backend.model.CatwatchYaml;
import org.zalando.catwatch.backend.model.Contributor;
import org.zalando.catwatch.backend.model.Language;
import org.zalando.catwatch.backend.model.Project;
import org.zalando.catwatch.backend.model.Statistics;
import org.zalando.catwatch.backend.model.util.Scorer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Callable;

import static java.util.stream.Collectors.*;

/**
 * Task to get organisation snapshot from GitHub using Kohsuke GitHub API.
 * <p>
 * The code of this class is not optimised in terms of number of API requests
 * in favour of code simplicity and readability. However, this should not affect
 * API rate limit if http cache is used. If rate limit is reached the task
 * is blocked until the limit is reset.
 *
 * @see RateLimitHandler
 * @see <a href="http://github-api.kohsuke.org">Kohsuke GitHub API</a>
 * @see <a href="https://developer.github.com/v3/#rate-limiting">API documentation from GitHub</a>
 */
public class TakeSnapshotTask implements Callable<Snapshot> {

    private static final Logger logger = LoggerFactory.getLogger(TakeSnapshotTask.class);

    private final GitHub gitHub;
    private final String organisationName;
    private final Date snapshotDate;

    private Scorer scorer;

    public TakeSnapshotTask(final GitHub gitHub, final String organisationName, Scorer scorer, Date snapshotDate) {
        this.gitHub = gitHub;
        this.organisationName = organisationName;
        this.scorer = scorer;
        this.snapshotDate = snapshotDate;
    }

    @Override
    public Snapshot call() throws Exception {
        logger.info("Taking snapshot of organization '{}'.", organisationName);

        final OrganizationWrapper organization = new OrganizationWrapper(gitHub.getOrganization(organisationName));

        Snapshot snapshot = new Snapshot(
                collectStatistics(organization),
                collectProjects(organization),
                collectContributors(organization),
                collectLanguages(organization));

        logger.info("Successfully taken snapshot of organization '{}'.", organisationName);

        return snapshot;
    }

    Statistics collectStatistics(final OrganizationWrapper organization) throws IOException {
        logger.info("Started collecting statistics for organization '{}'.", organisationName);

        Statistics statistics = new Statistics(organization.getId(), snapshotDate);

        statistics.setPublicProjectCount(organization.listRepositories().size());
        statistics.setMembersCount(organization.listMembers().size());
        statistics.setTeamsCount(organization.listTeams().size());
        statistics.setAllContributorsCount((int) organization.listRepositories().stream()
                .map(RepositoryWrapper::listContributors)
                .flatMap(List::stream)
                .map(GHRepository.Contributor::getId)
                .distinct()
                .count());
        statistics.setExternalContributorsCount((int) organization.listRepositories().stream()
                .map(RepositoryWrapper::listContributors)
                .flatMap(List::stream)
                .filter(contributor -> !organization.contributorIsMember(contributor))
                .map(GHRepository.Contributor::getId)
                .distinct()
                .count());
        statistics.setAllStarsCount(organization.listRepositories().stream()
                .map(RepositoryWrapper::getStarsCount)
                .reduce(0, Integer::sum));
        statistics.setAllForksCount(organization.listRepositories().stream()
                .map(RepositoryWrapper::getForksCount)
                .reduce(0, Integer::sum));
        statistics.setAllSizeCount(organization.listRepositories().stream()
                .map(RepositoryWrapper::getSize)
                .reduce(0, Integer::sum));
        statistics.setProgramLanguagesCount((int) organization.listRepositories().stream()
                .map(RepositoryWrapper::getPrimaryLanguage)
                .distinct()
                .count());
        statistics.setTagsCount((int) organization.listRepositories().stream()
                .map(RepositoryWrapper::listTags)
                .flatMap(List::stream)
                .count());
        statistics.setOrganizationName(organization.getLogin());

        logger.info("Finished collecting statistics for organization '{}'.", organisationName);

        return statistics;
    }

    Collection<Project> collectProjects(OrganizationWrapper organization) throws IOException, URISyntaxException {
        logger.info("Started collecting projects for organization '{}'.", organisationName);

        List<Project> projects = new ArrayList<>();

        for (RepositoryWrapper repository : organization.listRepositories()) {
            Project project = new Project();

            project.setGitHubProjectId(repository.getId());
            project.setSnapshotDate(snapshotDate);
            project.setName(repository.getName());
            project.setUrl(repository.getUrl().toURI().toString());
            project.setDescription(repository.getDescription());
            project.setStarsCount(repository.getStarsCount());
            project.setForksCount(repository.getForksCount());
            project.setLastPushed(repository.getLastPushed().toString());
            project.setPrimaryLanguage(repository.getPrimaryLanguage());
            project.setLanguageList(new ArrayList<>(repository.listLanguages().keySet()));
            project.setOrganizationName(organization.getLogin());
            project.setCommitsCount(repository.listCommits().size());
            project.setContributorsCount(repository.listContributors().size());
            project.setExternalContributorsCount((int) repository.listContributors().stream()
                    .filter(contributor -> !organization.contributorIsMember(contributor))
                    .map(GHRepository.Contributor::getId)
                    .distinct()
                    .count());
            project.setScore(scorer.score(project));

            project.setMaintainers(getProjectMaintainers(repository));

            readCatwatchYaml(repository, project);

            projects.add(project);
        }

        logger.info("Finished collecting projects for organization '{}'.", organisationName);

        return projects;
    }

    List<String> getProjectMaintainers(RepositoryWrapper repository) {
        try {
            return Lists.newArrayList(Streams.asString(repository.getFileContent("MAINTAINERS")).split("\n"));
        } catch (IOException ioe) {
            return Collections.emptyList();
        }
    }

    void readCatwatchYaml(RepositoryWrapper repository, Project project) {
        CatwatchYaml data;
        try {
            final ObjectMapper mapper = new ObjectMapper(new YAMLFactory()); // jackson databind

            data = mapper.readValue(repository.getFileContent(".catwatch.yaml"), CatwatchYaml.class);

        } catch (FileNotFoundException fnfe) {
            // ignore 404 for .catwatch.yaml
            data = null;
        } catch (IOException ioe) {
            logger.warn("Failed to read .catwatch.yaml for '{}'", repository.getName(), ioe);
            data = null;
        }
        if (null != data) {
            project.setTitle(data.getTitle());
            project.setImage(data.getImage());
        }
    }

    @SuppressWarnings("unchecked")
    Collection<Contributor> collectContributors(OrganizationWrapper organization) throws IOException, URISyntaxException {
        logger.info("Started collecting contributors for organization '{}'.", organisationName);

        Collection<Contributor> contributors = new ArrayList<>();

        // Get a list of all contributors of all repositories
        Collection<GHRepository.Contributor> ghContributors = organization.listRepositories().stream()
                .map(RepositoryWrapper::listContributors)
                .flatMap(List::stream)
                .collect(toList());

        // Get a map of <Contributor ID> - <Contributions statistics>
        Map<Integer, IntSummaryStatistics> idStatisticsMap = ghContributors.stream()
                .collect(groupingBy(GHObject::getId, summarizingInt(GHRepository.Contributor::getContributions)));

        // Eliminate duplicates in contributors list
        ghContributors = ghContributors.stream()
                .collect(collectingAndThen(toCollection(() ->
                        new TreeSet<>(Comparator.comparingInt(GHObject::getId))), ArrayList::new));

        // Build a list of contributors
        for (GHRepository.Contributor ghContributor : ghContributors) {
            Contributor contributor = new Contributor(ghContributor.getId(), organization.getId(), snapshotDate);

            contributor.setName(ghContributor.getName());
            contributor.setUrl(ghContributor.getHtmlUrl().toURI().toString());
            contributor.setOrganizationalCommitsCount((int) idStatisticsMap.get(ghContributor.getId()).getSum());
            contributor.setOrganizationalProjectsCount((int) idStatisticsMap.get(ghContributor.getId()).getCount());
            contributor.setPersonalProjectsCount(ghContributor.getPublicRepoCount());
            contributor.setOrganizationName(organisationName);

            contributors.add(contributor);
        }

        // TODO contributor.setPersonalCommitsCount()

        logger.info("Finished collecting contributors for organization '{}'.", organisationName);

        return contributors;
    }

    @SuppressWarnings("rawtypes")
    Collection<Language> collectLanguages(OrganizationWrapper organization) {
        logger.info("Started collecting languages for organization '{}'.", organisationName);

        Collection<Language> languages = new ArrayList<>();

        Map<String, LongSummaryStatistics> stat = organization.listRepositories().stream()
                .map(RepositoryWrapper::listLanguages)
                .map(Map::entrySet)
                .flatMap(Set::stream)
                .collect(groupingBy(Map.Entry::getKey,
                        summarizingLong(entry -> ((Number) ((Map.Entry) entry).getValue()).longValue())));

        final long allLanguageSize = stat.entrySet().stream()
                .map(entry -> entry.getValue().getSum())
                .reduce(0L, Long::sum);

        for (Map.Entry<String, LongSummaryStatistics> entry : stat.entrySet()) {
            Language language = new Language();

            language.setName(entry.getKey());
            language.setProjectsCount((int) entry.getValue().getCount());
            language.setPercentage((int) (entry.getValue().getSum() * 100 / allLanguageSize));

            languages.add(language);
        }

        logger.info("Finished collecting languages for organization '{}'.", organisationName);

        return languages;
    }
}
