package org.zalando.catwatch.backend.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.zalando.catwatch.backend.github.Snapshot;
import org.zalando.catwatch.backend.github.SnapshotProvider;
import org.zalando.catwatch.backend.repo.ContributorRepository;
import org.zalando.catwatch.backend.repo.ProjectRepository;
import org.zalando.catwatch.backend.repo.StatisticsRepository;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static com.google.common.primitives.Bytes.asList;
import static java.lang.String.format;
import static java.net.NetworkInterface.getByInetAddress;
import static java.time.Instant.now;
import static java.util.Date.from;
import static java.util.stream.Collectors.joining;

/**
 * Fetches organizations data from GitHub and saves it to the database.
 */
@Component
public class Fetcher {

    private static final Logger logger = LoggerFactory.getLogger(Fetcher.class);

    private final ProjectRepository projectRepository;
    private final StatisticsRepository statisticsRepository;
    private final ContributorRepository contributorRepository;
    private final SnapshotProvider snapshotProvider;
    private final String[] organizations;

    @Autowired
    public Fetcher(ProjectRepository projectRepository,
                   StatisticsRepository statisticsRepository,
                   ContributorRepository contributorRepository,
                   SnapshotProvider snapshotProvider,
                   @Value("#{'${organization.list}'.split(',')}") String[] organizations) {
        this.projectRepository = projectRepository;
        this.statisticsRepository = statisticsRepository;
        this.contributorRepository = contributorRepository;
        this.snapshotProvider = snapshotProvider;
        this.organizations = organizations;
    }

    /**
     * This is used to fetch data from GitHub.
     */
    public boolean fetchData() {
        Date snapshotDate = from(now());

        logger.info("Starting fetching data. Snapshot date: {} {}, IP and MAC Address: {}.",
                snapshotDate, snapshotDate.getTime(), getIpAndMacAddress());

        List<Future<Snapshot>> futures = new ArrayList<>();

        try {
            for (String organizationName : organizations) {
                futures.add(snapshotProvider.takeSnapshot(organizationName, snapshotDate));
                logger.info("Enqueued task TakeSnapshotTask for organization '{}'.", organizationName);
            }
        } catch (IOException e) {
            logger.error("Unable to fetch data from GitHub API. Missing GitHub API credentials?.", e);
            throw new CrawlerRetryException(e);
        }
        logger.info("Submitted {} TakeSnapshotTasks.", futures.size());

        for (Future<Snapshot> future : futures) {
            try {
                Snapshot snapshot = future.get();

                statisticsRepository.save(snapshot.getStatistics());
                projectRepository.save(snapshot.getProjects());
                contributorRepository.save(snapshot.getContributors());
                // TODO languagesRepository.save(snapshot.getLanguages());

                logger.info("Successfully saved data for organization '{}'.", snapshot.getStatistics()
                        .getOrganizationName());
            } catch (InterruptedException | ExecutionException e) {
                logger.error("Error occurred while processing organization.", e);
                throw new CrawlerRetryException(e);
            }
        }
        logger.info("Finished fetching data.");
        return true;
    }

    private String getIpAndMacAddress() {
        try {
            InetAddress ip = InetAddress.getLocalHost();

            String mac = asList(getByInetAddress(ip).getHardwareAddress()).stream()
                    .map(b -> format("%02X", b))
                    .collect(joining("-"));

            return ip.getHostAddress() + "#" + mac;

        } catch (Exception e) {
            return "unknownAddress";
        }
    }
}
