package com.afa.devicer.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SuppressWarnings("PMD")
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class BackOfficeWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackOfficeWebApplication.class, args);
	}

}
