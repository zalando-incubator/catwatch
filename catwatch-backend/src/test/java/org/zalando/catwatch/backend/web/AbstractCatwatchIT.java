package org.zalando.catwatch.backend.web;

import java.net.URL;

import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.zalando.catwatch.backend.repo.AbstractRepositoryIT;

//@WebIntegrationTest
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
// @IntegrationTest({ "server.port=0" })
public abstract class AbstractCatwatchIT extends AbstractRepositoryIT {

    @Value("${local.server.port}")
    private int port;

    protected URL base;

    @Autowired
    protected TestRestTemplate template;

    @Before
    public void setUp() throws Exception {
        this.base = new URL("http://localhost:" + port);
//        template = new TestRestTemplate();
    }

}