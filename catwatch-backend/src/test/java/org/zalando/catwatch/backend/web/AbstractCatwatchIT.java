package org.zalando.catwatch.backend.web;

import org.junit.Before;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.web.client.RestTemplate;
import org.zalando.catwatch.backend.repo.AbstractRepositoryIT;

import java.net.URL;

@WebIntegrationTest
@IntegrationTest({ "server.port=0" })
public abstract class AbstractCatwatchIT extends AbstractRepositoryIT {

	@Value("${local.server.port}")
	private int port;

	protected URL base;
	
	protected RestTemplate template;

	@Before
	public void setUp() throws Exception {
		this.base = new URL("http://localhost:" + port);
		template = new TestRestTemplate();
	}

}