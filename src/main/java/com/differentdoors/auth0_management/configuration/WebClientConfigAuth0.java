package com.differentdoors.auth0_management.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfigAuth0 {

    @Value("${different_doors.auth0.token_url}")
    protected String accessTokenUri;
    @Value("${different_doors.auth0.client_id}")
    protected String clientId;
    @Value("${different_doors.auth0.client_secret}")
    protected String clientSecret;

    @Bean
    public ReactiveClientRegistrationRepository clientRegistrationRepositoryAuth0() {
        ClientRegistration clientRegistration = ClientRegistration.withRegistrationId("auth0")
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .tokenUri(accessTokenUri)
                .build();
        return new InMemoryReactiveClientRegistrationRepository(clientRegistration);
    }

    @Bean("Auth0")
    WebClient webclientAuth0() {
        InMemoryReactiveOAuth2AuthorizedClientService clientService = new InMemoryReactiveOAuth2AuthorizedClientService(clientRegistrationRepositoryAuth0());
        AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager authorizedClientManager = new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(clientRegistrationRepositoryAuth0(), clientService);
        ServerOAuth2AuthorizedClientExchangeFilterFunction oauth = new ServerOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
        oauth.setDefaultClientRegistrationId("auth0");
        return WebClient.builder()
                .baseUrl("https://differentgroup.eu.auth0.com/api/v2/")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .filter(oauth)
                .build();
    }
}
