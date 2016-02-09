package org.zalando.catwatch.backend.config;

import java.io.IOException;
import java.util.Date;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.zalando.catwatch.backend.model.ContributorKey;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectMapperTest {

    @Test
    public void testObjectMapperDateFormat() throws JsonGenerationException, JsonMappingException, IOException {
        CatwatchConfig config = new CatwatchConfig();
        ObjectMapper mapper = config.objectMapper();
        Date now = new Date();
        String expected = mapper.getDateFormat().format(now);
        ContributorKey key = new ContributorKey(12, 13, now);
        String result = mapper.writeValueAsString(key);
        Assertions.assertThat(result).contains(expected);
    }

}
