package org.zalando.catwatch.backend.web.config;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.net.URL;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;
import org.zalando.catwatch.backend.CatWatchBackendApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CatWatchBackendApplication.class)
@WebIntegrationTest
@IntegrationTest({ "server.port=0" })
public class CorsFilterIT {

	@Value("${local.server.port}")
	private int port;

	private URL base;
	private RestTemplate template;

	@Before
	public void setUp() throws Exception {
		this.base = new URL("http://localhost:" + port + "/");
		template = new TestRestTemplate();
	}

	@Test
	public void testCorsHeadersInResponse_forRootPage() throws Exception {
		ResponseEntity<String> response = template.getForEntity(base.toString(), String.class);
		HttpHeaders headers = response.getHeaders();
		assertThat(headers.get("Access-Control-Allow-Origin"), equalTo(asList("*")));
		assertThat(headers.get("Access-Control-Allow-Methods"), equalTo(asList("POST, GET, OPTIONS, DELETE")));
		assertThat(headers.get("Access-Control-Max-Age"), equalTo(asList("3600")));
		assertThat(headers.get("Access-Control-Allow-Headers"), equalTo(asList("x-requested-with")));
	}

}
