package org.zalando.catwatch.backend.github;

import java.io.IOException;

import java.net.URISyntaxException;

import java.time.ZonedDateTime;

import java.util.*;
import java.util.concurrent.Callable;

import org.kohsuke.github.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zalando.catwatch.backend.model.Contributor;
import org.zalando.catwatch.backend.model.Language;
import org.zalando.catwatch.backend.model.Project;
import org.zalando.catwatch.backend.model.Statistics;
import org.zalando.catwatch.backend.model.util.Scorer;

import static java.util.stream.Collectors.*;

/**
 * A task to get organisation snapshot from GitHub using API V3.
 * <p>
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

        final OrganizationWrapper organization = new OrganizationWrapper(gitHub.getOrganization(organisationName));

        return new Snapshot(collectStatistics(organization),
                collectProjects(organization.listRepositories(), organization.getLogin()),
                collectContributors(organization),
                collectLanguages( organization.listRepositories() ));
    }

    Statistics collectStatistics(final OrganizationWrapper organization)
            throws IOException {
        logger.info("Started collecting statistics for organization '{}'", organisationName);

        Statistics statistics = new Statistics(organization.getId(), snapshotDate);

        statistics.setPublicProjectCount(organization.getPublicRepoCount());
        statistics.setMembersCount(organization.listPublicMembers().size());
        statistics.setTeamsCount(organization.listTeams().size());
        statistics.setAllContributorsCount((int) organization.listRepositories().stream()
                .map(RepositoryWrapper::listContributors)
                .flatMap(List::stream)
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

        logger.info("Finished collecting statistics for organization '{}'", organisationName);

        return statistics;
    }

    Collection<Project> collectProjects(final List<RepositoryWrapper> repos, String orgLogin) throws IOException, URISyntaxException {
        logger.info("Started collecting projects for organization '{}'", organisationName);

        List<Project> projects = new ArrayList<>();

        for (RepositoryWrapper repository : repos) {
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
            project.setOrganizationName(orgLogin);
            project.setCommitsCount(repository.listCommits().size());
            project.setContributorsCount(repository.listContributors().size());
            project.setScore(scorer.score(project));

            projects.add(project);
        }

        logger.info("Finished collecting projects for organization '{}'", organisationName);

        return projects;
    }

    Collection<Contributor> collectContributors(final OrganizationWrapper organization) throws IOException, URISyntaxException {
        logger.info("Started collecting contributors for organization '{}'", organisationName);

        Collection<Contributor> contributors = new ArrayList<>();

        // Get a list of all contributors of all repositories
        Collection<GHRepository.Contributor> ghContributors = organization.listRepositories()
                .stream()
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

        logger.info("Finished collecting contributors for organization '{}'", organisationName);

        return contributors;
    }

    Collection<Language> collectLanguages(final List<RepositoryWrapper> repos) {
        logger.info("Started collecting languages for organization '{}'", organisationName);

        Collection<Language> languages = new ArrayList<>();

        Map<String, LongSummaryStatistics> stat = repos.stream()
                .map(RepositoryWrapper::listLanguages)
                .map(Map::entrySet)
                .flatMap(Set::stream)
                .collect(groupingBy(Map.Entry::getKey,
                        summarizingLong(Map.Entry::getValue)));

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

        logger.info("Finished collecting languages for organization '{}'", organisationName);

        return languages;
    }

	Date getSnapshotDate() {
		return snapshotDate;
	}
}
