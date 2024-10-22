package com.differentdoors.auth0_management.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    private String given_name;
    private String user_id;
    private String email;
    private String name;
    private String job_title;
    private String picture;
    private UserMetadata user_metadata;
    private AppMetadata app_metadata;
    private List<Identity> identities;
    private List<String> groups;
    private String family_name;
    private boolean email_verified;
    private String last_login;
    private Number logins_count;
    private String manager;
}
