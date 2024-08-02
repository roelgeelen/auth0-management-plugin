package com.differentdoors.auth0_management.services;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class RolesService {

    @Autowired
    @Qualifier("Auth0")
    private WebClient webClient;

    private final ObjectMapper objectMapper = JsonMapper.builder()
            .findAndAddModules()
            .serializationInclusion(JsonInclude.Include.NON_NULL)
            .build();

    public Object getRoles() throws Exception {
        return webClient.get()
                .uri("roles")
                .retrieve()
                .bodyToMono(Object.class).block();
    }
}
