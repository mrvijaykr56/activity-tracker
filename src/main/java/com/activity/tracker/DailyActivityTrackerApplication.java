package com.activity.tracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@EnableJpaRepositories(basePackages = "com.activity.tracker.repo;")
public class DailyActivityTrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(DailyActivityTrackerApplication.class, args);
	}

}
