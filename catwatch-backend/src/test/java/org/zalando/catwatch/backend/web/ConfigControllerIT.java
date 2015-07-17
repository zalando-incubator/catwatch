package org.zalando.catwatch.backend.web;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.springframework.http.ResponseEntity;

public class ConfigControllerIT extends AbstractCatwatchIT {

	@Test
	public void testConfigOutput() throws Exception {
		ResponseEntity<String> response = template.getForEntity(base.toString() + "/config", String.class);
		assertThat(response.getBody(), containsString("organization.list"));
		assertThat(response.getBody(), containsString("zalando")); // default value
	}
}
