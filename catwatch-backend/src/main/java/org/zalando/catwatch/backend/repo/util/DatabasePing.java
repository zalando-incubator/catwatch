package org.zalando.catwatch.backend.repo.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

public class DatabasePing {

    private static Logger logger = LoggerFactory.getLogger(DatabasePing.class);

    private static Boolean databaseAvailable;

    public static boolean isDatabaseAvailable(JdbcTemplate jdbcTemplate) {

        if (databaseAvailable == null) {

            try {
                // http://stackoverflow.com/questions/847246/what-is-the-best-way-to-ping-a-database-via-jdbc
                jdbcTemplate.execute("select 1;");
                databaseAvailable = true;
            } catch (RuntimeException e) {
                logger.info("Database not available", e);
                databaseAvailable = false;
            }

        }

        return databaseAvailable;
    }
}
