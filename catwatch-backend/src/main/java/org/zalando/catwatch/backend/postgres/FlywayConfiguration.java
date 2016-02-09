package org.zalando.catwatch.backend.postgres;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationInitializer;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayConfiguration {

    @Autowired(required = false)
    private FlywayMigrationStrategy migrationStrategy;

    @Bean
    public FlywayMigrationInitializer flywayInitializer(Flyway flyway) {
        return new OptionalFlywayMigrationInitializer(flyway, this.migrationStrategy);
    }
}
