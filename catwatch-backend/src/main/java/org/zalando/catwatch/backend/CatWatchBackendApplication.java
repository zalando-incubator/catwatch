package org.zalando.catwatch.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CatWatchBackendApplication {

	public static void main(String[] args) {

		// show details about auto configuration
		// System.setProperty("debug", "true");

		// https://github.com/spring-projects/spring-boot/issues/1219
		// System.setProperty("spring.profiles.default", "postgresql");

		// System.setProperty("spring.profiles.active", "postgresql");
		// System.setProperty("spring.profiles.active", "hbm2ddl");

		SpringApplication.run(CatWatchBackendApplication.class);
	}
}
