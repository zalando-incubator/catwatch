package org.zalando.catwatch.backend.web.config;

import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.zalando.catwatch.backend.web.AbstractCatwatchIT;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class CorsFilterIT extends AbstractCatwatchIT {

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
