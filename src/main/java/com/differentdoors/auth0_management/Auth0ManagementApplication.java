package com.differentdoors.auth0_management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
public class Auth0ManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(Auth0ManagementApplication.class, args);
	}

}
