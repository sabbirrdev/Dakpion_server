package com.company.efood;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@SpringBootApplication
public class EFoodApplication {
	public static void main(String[] args) {
		SpringApplication.run(EFoodApplication.class, args);
	}
}
