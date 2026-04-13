package com.ecommerce.project;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class ECommApplication {

	/**
	 * Bootstraps the Spring Boot application.
	 *
	 * @param args runtime arguments
	 */
	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

        System.setProperty("DB_URL", dotenv.get("DB_URL"));
        System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
        System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));

        System.setProperty("JWT_SECRET", dotenv.get("JWT_SECRET"));
        System.setProperty("JWT_EXPIRATION", dotenv.get("JWT_EXPIRATION"));
        System.setProperty("JWT_COOKIE_NAME", dotenv.get("JWT_COOKIE_NAME"));

        System.setProperty("PORT", dotenv.get("PORT"));
        System.setProperty("FRONTEND_URL", dotenv.get("FRONTEND_URL"));
        System.setProperty("IMAGE_BASE_URL", dotenv.get("IMAGE_BASE_URL"));
        SpringApplication.run(ECommApplication.class, args);
	}

}
