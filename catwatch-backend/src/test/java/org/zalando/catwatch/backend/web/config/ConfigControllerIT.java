package org.zalando.catwatch.backend.web.config;

import org.junit.Test;
import org.zalando.catwatch.backend.web.AbstractCatwatchIT;

import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

public class ConfigControllerIT extends AbstractCatwatchIT {

    @SuppressWarnings("unchecked")
    @Test
    public void testConfigOutput() throws Exception {
        Map<String, String> response = template.getForEntity(base.toString() + "/config", Map.class).getBody();
        assertThat(response.get("organization.list"), containsString(","));
        assertThat(response.get("schedule"), containsString("*"));
    }
}
