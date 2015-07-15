package org.zalando.catwatch.backend.web;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

import java.net.URL;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;
import org.zalando.catwatch.backend.CatWatchBackendApplication;
import org.zalando.catwatch.backend.model.Statistics;
import org.zalando.catwatch.backend.repo.StatisticsRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CatWatchBackendApplication.class)
@WebIntegrationTest
@IntegrationTest({"server.port=0"})
public class StatisticsControllerIT {

    @Value("${local.server.port}")
    private int port;

	private URL base;
	private RestTemplate template;
	
    @Autowired
    private StatisticsRepository repository;

	@Before
	public void setUp() throws Exception {
		this.base = new URL("http://localhost:" + port + "/");
		template = new TestRestTemplate();
		
		fillRepository();
	}

	private void fillRepository() {
		this.repository.deleteAll();
		
		Statistics s = new Statistics();
	}

	@Test
	public void getAllStatistics() throws Exception {
		ResponseEntity<String> response = template.getForEntity(base.toString(), String.class);
		assertThat(response.getBody(), containsString("<h1>CatWatch Backend</h1>"));
	}
	
	@Test
	public void getStatisticsFromOneOrganization() throws Exception {
		ResponseEntity<String> response = template.getForEntity(base.toString(), String.class);
		assertThat(response.getBody(), containsString("<h1>CatWatch Backend</h1>"));
	}
}
