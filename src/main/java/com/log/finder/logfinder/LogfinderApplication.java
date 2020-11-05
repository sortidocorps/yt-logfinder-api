package com.log.finder.logfinder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@EntityScan("com.log.finder.logfinder.model")
@SpringBootApplication
public class LogfinderApplication {

	public static void main(String[] args) {
		SpringApplication.run(LogfinderApplication.class, args);
	}

}
