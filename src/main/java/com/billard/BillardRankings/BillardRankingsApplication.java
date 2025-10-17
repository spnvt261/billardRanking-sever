package com.billard.BillardRankings;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class BillardRankingsApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.load(); // tự động đọc file .env ở project root

		// Thiết lập biến hệ thống để Spring Boot có thể đọc
		System.setProperty("CLOUDINARY_CLOUD_NAME", dotenv.get("CLOUDINARY_CLOUD_NAME"));
		System.setProperty("CLOUDINARY_API_KEY", dotenv.get("CLOUDINARY_API_KEY"));
		System.setProperty("CLOUDINARY_API_SECRET", dotenv.get("CLOUDINARY_API_SECRET"));
		SpringApplication.run(BillardRankingsApplication.class, args);
	}

}
