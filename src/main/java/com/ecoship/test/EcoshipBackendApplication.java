package com.ecoship.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication
public class EcoshipBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(EcoshipBackendApplication.class, args);
	}

}
