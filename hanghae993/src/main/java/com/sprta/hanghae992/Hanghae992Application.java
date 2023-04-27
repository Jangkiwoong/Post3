package com.sprta.hanghae992;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@EnableJpaAuditing
@SpringBootApplication
public class Hanghae992Application {

	public static void main(String[] args) {
		SpringApplication.run(Hanghae992Application.class, args);
	}

}
