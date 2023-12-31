package com.ecoship.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class EcoshipBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(EcoshipBackendApplication.class, args);
	}

}
