package com.example.idledetector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class IdleResourceDetectorApplication {

	public static void main(String[] args) {
		SpringApplication.run(IdleResourceDetectorApplication.class, args);
	}
}