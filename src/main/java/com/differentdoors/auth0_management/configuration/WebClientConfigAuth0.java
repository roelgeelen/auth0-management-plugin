package com.differentdoors.auth0_management.configuration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.endpoint.WebClientReactiveClientCredentialsTokenResponseClient;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfigAuth0 {

    @Value("${different_doors.auth0.token_url}")
    protected String accessTokenUri;

    @Value("${different_doors.auth0.client_id}")
    protected String clientId;

    @Value("${different_doors.auth0.client_secret}")
    protected String clientSecret;

    @Value("${different_doors.auth0.audience}")
    protected String audience;


    @Bean("auth0ClientRegistrationRepository")
    public ReactiveClientRegistrationRepository reactiveClientRegistrationRepository() {
        return new InMemoryReactiveClientRegistrationRepository(
                ClientRegistration.withRegistrationId("auth0")
                        .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                        .tokenUri(accessTokenUri)
                        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                        .clientId(clientId)
                        .clientSecret(clientSecret)
                        .build()
        );
    }

    @Bean("Auth0")
    WebClient auth0WebClient(@Qualifier("auth0AuthorizedClientManager") ReactiveOAuth2AuthorizedClientManager authorizedClientManager) {
        var exchangeFilterFunction = new ServerOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
        exchangeFilterFunction.setDefaultClientRegistrationId("auth0");
        return WebClient.builder()
                .baseUrl("https://differentgroup.eu.auth0.com/api/v2/")
                .filter(exchangeFilterFunction)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Bean("auth0AuthorizedClientService")
    public ReactiveOAuth2AuthorizedClientService authorizedClientService(
            @Qualifier("auth0ClientRegistrationRepository") ReactiveClientRegistrationRepository clientRegistrationRepository) {
        return new InMemoryReactiveOAuth2AuthorizedClientService(clientRegistrationRepository);
    }

    @Bean("auth0AuthorizedClientManager")
    public ReactiveOAuth2AuthorizedClientManager authorizedClientManager(
            @Qualifier("auth0ClientRegistrationRepository") ReactiveClientRegistrationRepository clientRegistrationRepository,
            @Qualifier("auth0AuthorizedClientService") ReactiveOAuth2AuthorizedClientService authorizedClientService) {

        ReactiveOAuth2AuthorizedClientProvider authorizedClientProvider =
                ReactiveOAuth2AuthorizedClientProviderBuilder.builder()
                        .clientCredentials()
                        .build();

        AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager authorizedClientManager =
                new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(
                        clientRegistrationRepository,
                        authorizedClientService);
        authorizedClientManager.setAuthorizedClientProvider(ReactiveOAuth2AuthorizedClientProviderBuilder.builder()
                .clientCredentials(clientCredentialsGrantBuilder -> {
                            WebClientReactiveClientCredentialsTokenResponseClient accessTokenResponseClient = new WebClientReactiveClientCredentialsTokenResponseClient();
                            accessTokenResponseClient.setWebClient(WebClient.builder()
                                    .filter((request, next) -> {
                                        BodyInserters.FormInserter<String> body = (BodyInserters.FormInserter<String>) request.body();
                                        body.with("audience", audience);
                                        return next.exchange(request);
                                    })
                                    .build());
                            clientCredentialsGrantBuilder.accessTokenResponseClient(accessTokenResponseClient);
                        }
                )
                .build()
        );
        return authorizedClientManager;
    }

}