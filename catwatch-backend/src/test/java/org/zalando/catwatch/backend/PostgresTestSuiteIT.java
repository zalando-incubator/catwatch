package org.zalando.catwatch.backend;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.zalando.catwatch.backend.repo.ContributorRepositoryIT;
import org.zalando.catwatch.backend.repo.ProjectRepositoryIT;
import org.zalando.catwatch.backend.repo.StatisticsRepositoryIT;
import org.zalando.catwatch.backend.web.ContributorsApiIT;
import org.zalando.catwatch.backend.web.ProjectsApiIT;
import org.zalando.catwatch.backend.web.StatisticsApiIT;

/**
 * Runs some integrations tests using the PostgreSQL database.
 *
 * @author rwitzel
 */
@RunWith(Suite.class)
@SuiteClasses({ //
        ContributorRepositoryIT.class, //
        ProjectRepositoryIT.class, //
        StatisticsRepositoryIT.class, //
        ContributorsApiIT.class, //
        ProjectsApiIT.class, //
        StatisticsApiIT.class})
public class PostgresTestSuiteIT {

    private static String springProfilesActivePreviously;

    @BeforeClass
    public static void setUpClass() {
        springProfilesActivePreviously = System.getProperty("spring.profiles.active");
        System.setProperty("spring.profiles.active", "postgresql,integrationtest");
    }

    @AfterClass
    public static void tearDownClass() {
        if (springProfilesActivePreviously != null) {
            System.setProperty("spring.profiles.active", springProfilesActivePreviously);
        }
    }

}
