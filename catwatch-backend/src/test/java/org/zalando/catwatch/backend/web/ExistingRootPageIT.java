package org.zalando.catwatch.backend.web;

import org.junit.Test;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

public class ExistingRootPageIT extends AbstractCatwatchIT {

	@Test
	public void testRootPage() throws Exception {
		ResponseEntity<String> response = template.getForEntity(base.toString(), String.class);
		assertThat(response.getBody(), containsString("<h1>CatWatch Backend</h1>"));
	}
}
