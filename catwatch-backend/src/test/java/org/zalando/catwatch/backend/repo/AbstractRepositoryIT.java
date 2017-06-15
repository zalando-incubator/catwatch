package org.zalando.catwatch.backend.repo;

import static org.junit.Assume.assumeTrue;
import static org.zalando.catwatch.backend.repo.util.DatabasePing.isDatabaseAvailable;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
//@SpringApplicationConfiguration(classes = CatWatchBackendApplication.class)
public abstract class AbstractRepositoryIT {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Before
	public void skipIfDatabaseNotAvailable() {
		assumeTrue(isDatabaseAvailable(jdbcTemplate));
	}

}
