package com.differentdoors.auth0_management.services;

import com.differentdoors.auth0_management.models.Role;
import com.differentdoors.auth0_management.models.User;
import com.differentdoors.auth0_management.models.UserPage;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class UserService {

    @Autowired
    @Qualifier("Auth0")
    private WebClient webClient;

    private final ObjectMapper objectMapper = JsonMapper.builder()
            .findAndAddModules()
            .serializationInclusion(JsonInclude.Include.NON_NULL)
            .build();

    public UserPage getUsers(int page, int size, String sort, @Nullable List<String> fields) throws Exception {
        return objectMapper.readValue(webClient.get()
                .uri(builder -> builder.path("users")
                        .queryParam("page", page)
                        .queryParam("per_page", size)
                        .queryParam("include_totals", true)
                        .queryParam("sort", sort)
                        .queryParam("fields", fields!=null?String.join(",", fields):null)
                        .build()
                )
                .retrieve()
                .bodyToMono(String.class).block(), new TypeReference<>() {
        });
    }

    public List<User> searchUsers(String query, String sort, @Nullable List<String> fields) throws Exception {
        return objectMapper.readValue(webClient.get()
                .uri(builder -> builder.path("users")
                        .queryParam("q", query)
                        .queryParam("sort", sort)
                        .queryParam("fields", fields != null ? String.join(",", fields) : null)
                        .build())
                .retrieve()
                .bodyToMono(String.class).block(), new TypeReference<>() {
        });
    }

    public UserPage searchUsersPerPage(String query,int page, int size, @Nullable List<String> fields) throws Exception {
        return objectMapper.readValue(webClient.get()
                .uri(builder -> builder.path("users")
                        .queryParam("q", query)
                        .queryParam("page", page)
                        .queryParam("per_page", size)
                        .queryParam("include_totals", true)
                        .queryParam("fields", fields != null ? String.join(",", fields) : null)
                        .build())
                .retrieve()
                .bodyToMono(String.class).block(), new TypeReference<>() {
        });
    }

    public User getUser(String id, @Nullable List<String> fields) throws Exception {
        // De response wordt gelezen als een lijst van User objecten
        List<User> users = objectMapper.readValue(webClient.get()
                .uri(builder -> builder.path("users")
                        .queryParam("q", "user_id:\""+id+"\"")
                        .queryParam("fields", fields != null ? String.join(",", fields) : null)
                        .build())
                .retrieve()
                .bodyToMono(String.class).block(), new TypeReference<>() {
        });

        // Controleer of de lijst niet leeg is, en geef het eerste User object terug
        if (users != null && !users.isEmpty()) {
            return users.get(0); // retourneer het eerste User object
        } else {
            throw new Exception("Geen gebruikers gevonden voor ID: " + id);
        }
    }

    public User patchUser(String id, User user) throws Exception {
        return objectMapper.readValue(webClient.patch()
                .uri("users/"+id)
                .bodyValue(objectMapper.writeValueAsString(user))
                .retrieve()
                .bodyToMono(String.class).block(), new TypeReference<>() {
        });
    }

    public List<Role> getUserRoles(String id) throws Exception {
        return objectMapper.readValue(webClient.get()
                .uri("users/"+id+"/roles")
                .retrieve()
                .bodyToMono(String.class).block(), new TypeReference<>() {
        });
    }
}
