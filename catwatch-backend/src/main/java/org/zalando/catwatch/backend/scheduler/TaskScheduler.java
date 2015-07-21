package org.zalando.catwatch.backend.scheduler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.zalando.catwatch.backend.github.GitHubCrawler;
import org.zalando.catwatch.backend.repo.ContributorRepository;
import org.zalando.catwatch.backend.repo.ProjectRepository;
import org.zalando.catwatch.backend.repo.StatisticsRepository;

import javax.annotation.PostConstruct;

@Component
public class TaskScheduler {

    @Autowired
    private Environment env;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private StatisticsRepository statisticsRepository;
    @Autowired
    private ContributorRepository contributorRepository;

    private List<String> organisations;
    private String cachePath;
    private int cacheSize;

    @PostConstruct
    public void init(){
        String organizationNames = env.getProperty("organization.list");
        this.organisations = Arrays.asList(organizationNames.split("\\s*,\\s*"));
        this.cachePath = env.getProperty("cache.directory");
        this.cacheSize = env.getProperty("cache.size.mb", Integer.class);
    }

    /**
     * This is used to fetch Organization statistics from GitHub
     * (This runs at 8 AM everyday)
     */
    @Scheduled(cron = "0 8 * * * ?")
    public void fetchData() {
        ExecutorService pool = Executors.newFixedThreadPool(organisations.size());

        List<Future<GitHubCrawler.Snapshot>> futures = new ArrayList<>(organisations.size());

        for(String organisation: organisations){
            futures.add(pool.submit(new GitHubCrawler(organisation, cachePath, cacheSize)));
        }

        for(Future<GitHubCrawler.Snapshot> future : futures){
            try {
                GitHubCrawler.Snapshot result = future.get();
                statisticsRepository.save(result.statistics);
                projectRepository.save(result.projects);
                contributorRepository.save(result.contributors);

            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

        }

        pool.shutdown();
    }
}
