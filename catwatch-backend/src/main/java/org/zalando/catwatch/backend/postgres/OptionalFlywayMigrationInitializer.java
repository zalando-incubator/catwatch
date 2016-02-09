package org.zalando.catwatch.backend.postgres;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationInitializer;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.zalando.catwatch.backend.repo.util.DatabasePing.isDatabaseAvailable;

public class OptionalFlywayMigrationInitializer extends FlywayMigrationInitializer {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public OptionalFlywayMigrationInitializer(Flyway flyway, FlywayMigrationStrategy migrationStrategy) {
        super(flyway, migrationStrategy);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (isDatabaseAvailable(jdbcTemplate)) {
            super.afterPropertiesSet();
        }
    }
}
