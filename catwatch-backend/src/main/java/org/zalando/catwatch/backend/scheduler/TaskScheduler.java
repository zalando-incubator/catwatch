package org.zalando.catwatch.backend.scheduler;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.OkUrlFactory;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.kohsuke.github.extras.OkHttpConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.zalando.catwatch.backend.github.Snapshot;
import org.zalando.catwatch.backend.github.TakeSnapshotTask;
import org.zalando.catwatch.backend.repo.ContributorRepository;
import org.zalando.catwatch.backend.repo.ProjectRepository;
import org.zalando.catwatch.backend.repo.StatisticsRepository;

@Component
public class TaskScheduler {

    private static final Logger logger = LoggerFactory.getLogger(TaskScheduler.class);

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private StatisticsRepository statisticsRepository;

    @Autowired
    private ContributorRepository contributorRepository;

    @SuppressWarnings("MismatchedReadAndWriteOfArray")
    @Value("#{'${organization.list}'.split(',')}")
    private String[] organizations;

    @Value("${cache.path}")
    private String cachePath;

    @Value("${cache.size}")
    private Integer cacheSize;

    @Value("${github.login}")
    private String login;

    @Value("${github.password}")
    private String password;

    private static final int MEGABYTE = 1024 * 1024;

    /**
     * This is used to fetch every Organization statistics from GitHub
     * (This runs at 8 AM everyday)
     */
    @Scheduled(cron = "0 8 * * * ?")
    public void fetchData() {
        logger.info("Starting fetching data");

        GitHub gitHub;
        try {
            gitHub = initializeGitHub(getCacheDirectory());
        } catch (IOException e) {
            logger.error("No GitHub API credentials found. Unable to fetch data from GitHub API.");
            return;
        }

        ExecutorService execitorService = Executors.newFixedThreadPool(Math.min(organizations.length, 5));
        List<Future<Snapshot>> futures = new ArrayList<>();

        for (String organizationName : organizations) {
            futures.add(execitorService.submit(new TakeSnapshotTask(gitHub, organizationName)));
        }

        logger.info("Submitted {} TakeSnapshotTasks.", futures.size());

        for (Future<Snapshot> future : futures) {
            try {
                Snapshot snapshot = future.get();
                logger.info("Successfully fetched data for organization '{}'", snapshot.getStatistics().getOrganizationName());

                statisticsRepository.save(snapshot.getStatistics());
                projectRepository.save(snapshot.getProjects());
                contributorRepository.save(snapshot.getContributors());
                // TODO languagesRepository.save(snapshot.getLanguages());

                logger.info("Successfully saved data for organizaion '{}'", snapshot.getStatistics().getOrganizationName());
            } catch (InterruptedException | ExecutionException e) {
                logger.error("Error occurred while processing organization.", e);
            }
        }

        logger.info("Shutting down executor service.");
        execitorService.shutdown();
    }

    private GitHub initializeGitHub(Optional<File> cacheDirectoryOptional) throws IOException {
        GitHub gitHub;
        if (cacheDirectoryOptional.isPresent()) {
            logger.info("Initializing {} mb cache.", cacheSize);
            Cache cache = new Cache(cacheDirectoryOptional.get(), cacheSize * MEGABYTE);

            logger.info("Initializing GitHub object.");
            gitHub = GitHubBuilder.fromCredentials().withConnector(
                    new OkHttpConnector(
                            new OkUrlFactory(
                                    new OkHttpClient().setCache(cache)))).build();
        } else {
            logger.warn("Initializing GitHub object without cache.");
            gitHub = GitHub.connect();
        }
        return gitHub;
    }

    private Optional<File> getCacheDirectory() {
        Path path = Paths.get(cachePath);

        if (Files.isDirectory(path)) {
            if (Files.isWritable(path)) {
                logger.info("Cache directory found.");
                return Optional.of(path.toFile());
            }
            logger.warn("Unable to write to cache directory '{}'.", cachePath);
            return Optional.empty();
        }

        logger.info("Cache directory '{}' is not found. Creating new directory.", cachePath);
        try {
            path = Files.createDirectories(path);
            logger.info("Cache directory created successfully.");
            return Optional.of(path.toFile());
        } catch (FileAlreadyExistsException e) {
            logger.warn("Failed to created cache directory: file already exists.");
        } catch (SecurityException e) {
            logger.warn("Failed to created cache directory: access denied.");
        } catch (IOException e) {
            logger.warn("Failed to created cache directory.", e);
        }
        return Optional.empty();
    }
}
