package org.zalando.catwatch.backend.scheduler;

import static org.springframework.boot.SpringApplication.exit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("k8s-cron")
public class CronConfig {

    private final Logger log = LoggerFactory.getLogger(CronConfig.class);

    @Bean
    public CommandLineRunner cron(RetryableFetcher fetcher, ApplicationContext applicationContext) {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
                log.info("START FETCHING DATA VIA K8S-CRON");
                try {
                    fetcher.tryFetchData();
                    exit(applicationContext, new CustomExitCodeGenerator(0));
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    exit(applicationContext, new CustomExitCodeGenerator(1));
                }
            }
        };
    }

    static final class CustomExitCodeGenerator implements ExitCodeGenerator {
        private final int exitCode;

        public CustomExitCodeGenerator(int exitCode) {
            this.exitCode = exitCode;
        }

        @Override
        public int getExitCode() {
            return exitCode;
        }

    }
}
