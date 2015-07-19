package org.zalando.catwatch.backend.github;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.OkUrlFactory;
import org.kohsuke.github.*;
import org.kohsuke.github.extras.OkHttpConnector;
import org.zalando.catwatch.backend.model.Contributor;
import org.zalando.catwatch.backend.model.Language;
import org.zalando.catwatch.backend.model.Project;
import org.zalando.catwatch.backend.model.Statistics;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.Callable;

import static java.util.stream.Collectors.*;

// TODO javadoc
public class GitHubCrawler implements Callable<GitHubCrawler.Snapshot> {

    // TODO javadoc
    public static class Snapshot {
        public Statistics statistics;
        public Collection<Project> projects;
        public Collection<Contributor> contributors;
        public Collection<Language> languages;
    }

    private static final int MAX_PAGE_SIZE = 100;
    private static final int MEGABYTE = 1024 * 1024;

    private final Date snapshotDate;
    private final String organisationTitle;
    private final String cachePath;
    private final int cacheSize;

    // TODO javadoc
    public GitHubCrawler(String organisationTitle, String cachePath, int cacheSize) {
        this.snapshotDate = Date.from(ZonedDateTime.now().toInstant());
        this.organisationTitle = organisationTitle;
        this.cachePath = cachePath;
        this.cacheSize = cacheSize * MEGABYTE;
    }

    // TODO javadoc
    @Override
    public Snapshot call() throws Exception {
        File cacheDirectory = new File(cachePath);
        Cache cache = new Cache(cacheDirectory, cacheSize);
        GitHub gitHub = GitHubBuilder.fromCredentials().withConnector(
                new OkHttpConnector(
                        new OkUrlFactory(
                                new OkHttpClient().setCache(cache))))
                .build();
        GHOrganization organization = gitHub.getOrganization(organisationTitle);
        List<GHRepository> repositories = organization.listRepositories(MAX_PAGE_SIZE).asList().stream()
                .filter(r -> !r.isPrivate())
                .collect(toList());

        Snapshot snapshot = new Snapshot();
        snapshot.statistics = collectStatistics(repositories, organization);
        snapshot.projects = collectProjects(repositories);
        snapshot.contributors = collectContributors(repositories, organization);
        snapshot.languages = collectLanguages(repositories);

        return snapshot;
    }

    // TODO javadoc
    private Statistics collectStatistics(List<GHRepository> repositories, GHOrganization organization) throws IOException {
        Statistics statistics = new Statistics(organization.getId(), snapshotDate);

        statistics.setPublicProjectCount(organization.getPublicRepoCount());
        statistics.setMembersCount(organization.listPublicMembers().asList().size());
        statistics.setTeamsCount(organization.listTeams().asList().size());
        statistics.setAllContributorsCount((int) repositories.stream()
                .map(this::getContributors)
                .map(PagedIterable::asList)
                .flatMap(List::stream)
                .map(GHRepository.Contributor::getId)
                .distinct()
                .count());
        statistics.setAllStarsCount(repositories.stream()
                .map(GHRepository::getWatchers)
                .reduce(0, Integer::sum));
        statistics.setAllForksCount(repositories.stream()
                .map(GHRepository::getForks)
                .reduce(0, Integer::sum));
        statistics.setAllSizeCount(repositories.stream()
                .map(GHRepository::getSize)
                .reduce(0, Integer::sum));
        statistics.setProgramLanguagesCount((int) repositories.stream()
                .map(GHRepository::getLanguage)
                .distinct()
                .count());
        statistics.setTagsCount((int) repositories.stream()
                .map(this::getTags)
                .map(PagedIterable::asList)
                .count());
        statistics.setOrganizationName(organization.getName());

        return statistics;
    }

    // TODO javadoc
    private Collection<Project> collectProjects(List<GHRepository> repositories) throws IOException, URISyntaxException {
        List<Project> projects = new ArrayList<>();

        for (GHRepository repository : repositories) {
            Project project = new Project(repository.getId(), snapshotDate);

            project.setName(repository.getName());
            project.setUrl(repository.getHtmlUrl().toURI().toString());
            project.setDescription(repository.getDescription());
            project.setStarsCount(repository.getWatchers());
            project.setCommitsCount(repository.listCommits().asList().size());
            project.setForksCount(repository.getForks());
            project.setContributorsCount(repository.listContributors().asList().size());
            project.setScore(0); // TODO implement
            project.setLastPushed(repository.getPushedAt().toString());
            project.setPrimaryLanguage(repository.getLanguage());
            project.setLanguageList(new ArrayList<>(repository.listLanguages().keySet()));
            project.setOrganizationName(organisationTitle);

            projects.add(project);
        }

        return projects;
    }

    // TODO javadoc
    private Collection<Contributor> collectContributors(List<GHRepository> repositories, GHOrganization organization) throws IOException, URISyntaxException {
        Collection<Contributor> contributors = new ArrayList<>();

        // Get a list of all contributors of all repositories
        Collection<GHRepository.Contributor> ghContributors = repositories.stream()
                .map(this::getContributors)
                .map(PagedIterable::asList)
                .flatMap(List::stream)
                .collect(toList());

        // Get a map of <Contributor ID> - <Contributions statistics>
        Map<Integer, IntSummaryStatistics> idStatisticsMap = ghContributors.stream()
                .collect(groupingBy(GHObject::getId,
                        summarizingInt(GHRepository.Contributor::getContributions)));

        // Eliminate duplicates in contributors list
        ghContributors = ghContributors.stream()
                .collect(collectingAndThen(toCollection(() -> new TreeSet<>(Comparator.comparingInt(GHObject::getId))),
                        ArrayList::new));

        // Build list of contributors
        for (GHRepository.Contributor ghContributor : ghContributors) {
            Contributor contributor = new Contributor(ghContributor.getId(), organization.getId(), snapshotDate);

            contributor.setName(ghContributor.getName());
            contributor.setUrl(ghContributor.getHtmlUrl().toURI().toString());
            contributor.setOrganizationalCommitsCount((int) idStatisticsMap.get(ghContributor.getId()).getSum());
            contributor.setOrganizationalProjectsCount((int) idStatisticsMap.get(ghContributor.getId()).getCount());
            contributor.setPersonalProjectsCount(ghContributor.getPublicRepoCount());
            contributor.setOrganizationName(organisationTitle);

            contributors.add(contributor);
        }

        return contributors;
    }

    // TODO javadoc
    private Collection<Language> collectLanguages(List<GHRepository> repositories) {
        Collection<Language> languages = new ArrayList<>();

        Map<String, LongSummaryStatistics> stat = repositories.stream()
                .map(this::getLanguages)
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
            language.setPercentage((int) (entry.getValue().getSum() / allLanguageSize));

            languages.add(language);
        }

        return languages;
    }

    // TODO javadoc
    private PagedIterable<GHRepository.Contributor> getContributors(GHRepository repository) {
        try {
            return repository.listContributors();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // TODO javadoc
    private PagedIterable<GHTag> getTags(GHRepository repository) {
        try {
            return repository.listTags();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // TODO javadoc
    private Map<String, Long> getLanguages(GHRepository repository) {
        try {
            return repository.listLanguages();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}