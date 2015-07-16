package org.zalando.catwatch.backend.web;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.zalando.catwatch.backend.model.Statistics;
import org.zalando.catwatch.backend.repo.StatisticsRepository;

public class StatisticsControllerIT extends AbstractCatwatchIT {

    @Autowired
    private StatisticsRepository repository;

	@Before
	public void setUp() throws Exception {
		super.setUp();
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
