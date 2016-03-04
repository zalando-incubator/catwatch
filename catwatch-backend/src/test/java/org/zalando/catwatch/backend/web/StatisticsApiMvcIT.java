package org.zalando.catwatch.backend.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.zalando.catwatch.backend.model.Statistics;
import org.zalando.catwatch.backend.repo.AbstractRepositoryIT;
import org.zalando.catwatch.backend.repo.StatisticsRepository;
import org.zalando.catwatch.backend.repo.builder.StatisticsBuilder;
import org.zalando.catwatch.backend.util.Constants;
import org.zalando.catwatch.backend.util.StringParser;

import java.util.Date;

import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.DAYS;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.zalando.catwatch.backend.util.TestUtils.createRelativeStatisticsUrl;

public class StatisticsApiMvcIT extends AbstractRepositoryIT {

    @Autowired
    private StatisticsApi statisticsApi;

    @Autowired
    private StatisticsRepository repository;

    @Autowired
    private Environment env;

    private MockMvc mockMvc;

    private String configuredOrganizations;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        mockMvc = MockMvcBuilders.standaloneSetup(statisticsApi).build();

        configuredOrganizations = env.getProperty(Constants.CONFIG_ORGANIZATION_LIST);
    }

    @Test
    public void testDateParams() throws Exception {

        Date oneDayAgo = Date.from(now().minus(1, DAYS));
        Date twoDaysAgo = Date.from(now().minus(2, DAYS));
        Date threeDaysAgo = Date.from(now().minus(3, DAYS));
        String organization = StringParser.parseStringList(configuredOrganizations, ",").iterator().next();

        if (organization == null) {
            return;
        }

        //when
        repository.deleteAll();
        String from = StringParser.getISO8601StringForDate(threeDaysAgo);
        String to = StringParser.getISO8601StringForDate(oneDayAgo);

        //do request with valid time formats
        mockMvc.perform(get(createRelativeStatisticsUrl(null, from, to)))
            //then
            .andExpect(status().isOk())
            .andExpect(content().string("[]"));
        //
        //when
        insertStatisics(organization, twoDaysAgo);

        //do request with valid time formats
        mockMvc.perform(get(createRelativeStatisticsUrl(null, from, to)))
            //then
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));

        //do request with no startDate
        mockMvc.perform(get(createRelativeStatisticsUrl(null, null, to)))
            //then
            .andExpect(status().is(200))
            .andExpect(jsonPath("$", hasSize(1)));

        //do request with no endDate time
        mockMvc.perform(get(createRelativeStatisticsUrl(null, from, null)))
            //then
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));

        //do request with invalid endDate time
        mockMvc.perform(get(createRelativeStatisticsUrl(null, from, new Date().toString())))
            //then
            .andExpect(status().is(400));

        //do request with invalid endDate time
        mockMvc.perform(get(createRelativeStatisticsUrl(null, to, from)))
            //then
            .andExpect(status().is(200))
            .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void testWithoutOrganizationParam() throws Exception {

        // when
        repository.deleteAll();

        // do
        mockMvc.perform(get(createRelativeStatisticsUrl(null, null, null)))
            // then
            .andExpect(status().isOk()).andExpect(content().string("[]"));

        // when
        insertStatisics("unknownOrganization");

        // do
        mockMvc.perform(get(createRelativeStatisticsUrl(null, null, null)))
            // then
            .andExpect(status().isOk()).andExpect(content().string("[]"));

        // when
        if (configuredOrganizations != null) {
            String organization = StringParser.parseStringList(configuredOrganizations, ",").iterator().next();
            insertStatisics(organization);

            // do
            mockMvc.perform(get(createRelativeStatisticsUrl(null, null, null)))
                // then
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)));

            insertStatisics(organization);

            // do
            mockMvc.perform(get(createRelativeStatisticsUrl(null, null, null)))
                // then
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)));
        }

    }

    @Test
    public void testWithOrganizationParam() throws Exception {

        String organization = "TestOrganization";

        // when
        repository.deleteAll();
        insertStatisics("unknownOrganization");

        // do
        mockMvc.perform(get(createRelativeStatisticsUrl(organization, null, null)))
            // then
            .andExpect(status().isOk()).andExpect(content().string("[]"));

        // when
        insertStatisics(organization);

        // do
        mockMvc.perform(get(createRelativeStatisticsUrl(organization, null, null)))
            // then
            .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)));

        // do (evil organization name/list)
        mockMvc.perform(get(createRelativeStatisticsUrl("," + organization + ",", null, null)))
            // then
            .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)));
    }

    private Statistics insertStatisics(final String organization) {

        return insertStatisics(organization, null);
    }

    private Statistics insertStatisics(final String organization, final Date snapshotDate) {

        Statistics s = new StatisticsBuilder(null).organizationName(organization).create();

        if (snapshotDate != null) {
            s.setSnapshotDate(snapshotDate);
        }

        return repository.save(s);
    }
}
