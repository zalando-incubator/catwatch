package org.zalando.catwatch.backend.github;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summarizingInt;
import static java.util.stream.Collectors.summarizingLong;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

import java.io.IOException;

import java.net.URISyntaxException;

import java.time.ZonedDateTime;

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

import org.kohsuke.github.GHObject;
import org.kohsuke.github.GHOrganization;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.PagedIterable;
import org.kohsuke.github.RateLimitHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zalando.catwatch.backend.model.Contributor;
import org.zalando.catwatch.backend.model.Language;
import org.zalando.catwatch.backend.model.Project;
import org.zalando.catwatch.backend.model.Statistics;

/**
 * A task to get organisation snapshot from GitHub using API V3.
 *
 * <p>
 * The code of this class is not optimised in terms of number of API requests in favour of code simplicity and
 * readability. However, this should not affect API rate limit if http cache is used. If Api limit is reached the task
 * is blocked until the limit is reset.
 *
 * @see RateLimitHandler
 * @see <a href="https://developer.github.com/v3/#rate-limiting">API documentation from GitHub</a>
 */
public class TakeSnapshotTask implements Callable<Snapshot> {

    private static final Logger logger = LoggerFactory.getLogger(TakeSnapshotTask.class);
    private static final int MAX_PAGE_SIZE = 100;

    private final GitHub gitHub;
    private final String organisationName;
    private final Date snapshotDate;

    private Scorer scorer;

    public TakeSnapshotTask(final GitHub gitHub, final String organisationName, Scorer scorer) {
        this.gitHub = gitHub;
        this.organisationName = organisationName;
        this.scorer = scorer;
        this.snapshotDate = Date.from(ZonedDateTime.now().toInstant());
    }

    @Override
    public Snapshot call() throws Exception {
        logger.info("Taking snapshot of organization '{}'.", organisationName);

        final GHOrganization organization = gitHub.getOrganization(organisationName);
        final List<GHRepository> publicRepositories = organization.listRepositories(MAX_PAGE_SIZE).asList().stream()
                .filter(r -> !r.isPrivate()).collect(toList());

        return new Snapshot(collectStatistics(organization, publicRepositories), collectProjects(organization,
                publicRepositories), collectContributors(organization, publicRepositories),
                collectLanguages(publicRepositories));
    }

    private Statistics collectStatistics(final GHOrganization organization, final Collection<GHRepository> repositories)
            throws IOException {
        logger.info("Started collecting statistics for organization '{}'", organisationName);

        Statistics statistics = new Statistics(organization.getId(), snapshotDate);

        statistics.setPublicProjectCount(organization.getPublicRepoCount());
        statistics.setMembersCount(organization.listPublicMembers().asList().size());
        try {
            statistics.setTeamsCount(organization.listTeams().asList().size());
        } catch (Exception e) {
            logger.warn("Failed to set teams count for organization '{}': user has no rights to see teams.",
                    organisationName);
            statistics.setTeamsCount(0);
        }
        statistics.setAllContributorsCount((int) repositories.stream()
                .map(repository -> {
                    try {
                        return repository.listContributors();
                    } catch (IOException e) {
                        logger.error("Failed to list contributors for project '{}' of '{}'", repository.getName(), organisationName);
                        throw new RuntimeException(e);
                    }
                })
                .map(PagedIterable::asList)
                .flatMap(List::stream)
                .map(GHRepository.Contributor::getId)
                .distinct()
                .count());
        statistics.setAllStarsCount(repositories.stream().map(GHRepository::getWatchers).reduce(0, Integer::sum));
        statistics.setAllForksCount(repositories.stream().map(GHRepository::getForks).reduce(0, Integer::sum));
        statistics.setAllSizeCount(repositories.stream().map(GHRepository::getSize).reduce(0, Integer::sum));
        statistics.setProgramLanguagesCount((int) repositories.stream().map(GHRepository::getLanguage).distinct()
                .count());
        statistics.setTagsCount((int) repositories.stream()
                .map(repository -> {
                    try {
                        return repository.listTags();
                    } catch (IOException e) {
                        logger.error("Failed to list tags for project '{}' of '{}'", repository.getName(), organisationName);
                        throw new RuntimeException(e);
                    }
                })
                .map(PagedIterable::asList)
                .count());
        statistics.setOrganizationName(organization.getName());

        logger.info("Finished collecting statistics for organization '{}'", organisationName);

        return statistics;
    }

    private Collection<Project> collectProjects(final GHOrganization organization,
            final Collection<GHRepository> repositories) throws IOException, URISyntaxException {
        logger.info("Started collecting projects for organization '{}'", organisationName);

        List<Project> projects = new ArrayList<>();

        for (GHRepository repository : repositories) {
            Project project = new Project();
            project.setGitHubProjectId(repository.getId());
            project.setSnapshotDate(snapshotDate);

            project.setName(repository.getName());
            project.setUrl(repository.getHtmlUrl().toURI().toString());
            project.setDescription(repository.getDescription());
            project.setStarsCount(repository.getWatchers());
            project.setCommitsCount(repository.listCommits().asList().size());
            project.setForksCount(repository.getForks());
            project.setContributorsCount(repository.listContributors().asList().size());
            project.setLastPushed(repository.getPushedAt().toString());
            project.setPrimaryLanguage(repository.getLanguage());
            project.setLanguageList(new ArrayList<>(repository.listLanguages().keySet()));
            project.setOrganizationName(organization.getLogin());
            project.setScore(scorer.score(project));
            projects.add(project);
        }

        logger.info("Finished collecting projects for organization '{}'", organisationName);

        return projects;
    }

    private Collection<Contributor> collectContributors(final GHOrganization organization,
            final Collection<GHRepository> repositories) throws IOException, URISyntaxException {
        logger.info("Started collecting contributors for organization '{}'", organisationName);

        Collection<Contributor> contributors = new ArrayList<>();

        // Get a list of all contributors of all repositories
        Collection<GHRepository.Contributor> ghContributors = repositories
                .stream()
                .map(repository -> {
                    try {
                        return repository.listContributors();
                    } catch (IOException e) {
                        logger.error("Failed to list contributors for project '{}' of '{}'", repository.getName(), organisationName);
                        throw new RuntimeException(e);
                    }
                }).map(PagedIterable::asList).flatMap(List::stream).collect(toList());

        // Get a map of <Contributor ID> - <Contributions statistics>
        Map<Integer, IntSummaryStatistics> idStatisticsMap = ghContributors.stream().collect(
                groupingBy(GHObject::getId, summarizingInt(GHRepository.Contributor::getContributions)));

        // Eliminate duplicates in contributors list
        ghContributors = ghContributors.stream().collect(
                collectingAndThen(toCollection(() -> new TreeSet<>(Comparator.comparingInt(GHObject::getId))),
                        ArrayList::new));

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

        logger.info("Finished collecting contributors for organization '{}'", organisationName);

        return contributors;
    }

    private Collection<Language> collectLanguages(final Collection<GHRepository> repositories) {
        logger.info("Started collecting languages for organization '{}'", organisationName);

        Collection<Language> languages = new ArrayList<>();

        Map<String, LongSummaryStatistics> stat = repositories.stream().map(repository -> {
                    try {
                        return repository.listLanguages();
                    } catch (IOException e) {
                        logger.error("Failed to list languages for project '{}' of '{}'", repository.getName(), organisationName);
                        throw new RuntimeException(e);
                    }
                }).map(Map::entrySet).flatMap(Set::stream).collect(groupingBy(Map.Entry::getKey,
                        summarizingLong(Map.Entry::getValue)));

        final long allLanguageSize = stat.entrySet().stream().map(entry -> entry.getValue().getSum())
                .reduce(0L, Long::sum);

        for (Map.Entry<String, LongSummaryStatistics> entry : stat.entrySet()) {
            Language language = new Language();

            language.setName(entry.getKey());
            language.setProjectsCount((int) entry.getValue().getCount());
            language.setPercentage((int) (entry.getValue().getSum() * 100 / allLanguageSize));

            languages.add(language);
        }

        logger.info("Finished collecting languages for organization '{}'", organisationName);

        return languages;
    }

    // TODO implement me
    private int getContributorScore(GHRepository.Contributor contributor) {
        return 1;
    }
}
