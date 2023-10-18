package com.kmarinos.externalsqltablemonitoring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ExternalSqlTableMonitoringApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExternalSqlTableMonitoringApplication.class, args);
	}

}
