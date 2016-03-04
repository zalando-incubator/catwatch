package org.zalando.catwatch.backend.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.zalando.stups.oauth2.spring.server.TokenInfoResourceServerTokenServices;

@Configuration
@EnableResourceServer
@EnableWebSecurity
public class OAuthConfiguration extends ResourceServerConfigurerAdapter {

    @Value("${spring.oauth2.resource.tokenInfoUri}")
    private String tokenInfoUri;

    /**
     * Configure scopes for specific controller/httpmethods/roles here.
     */
    @Override
    public void configure(final HttpSecurity http) throws Exception {

        //J-
        http
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.NEVER)
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/delete/**").access("#oauth2.hasScope('uid')")
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/init/**").access("#oauth2.hasScope('uid')");
        //J+
    }

    @Bean
    public ResourceServerTokenServices customResourceTokenServices() {

        return new TokenInfoResourceServerTokenServices(tokenInfoUri, "what_here");
    }

}