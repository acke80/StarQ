package com.redpill_linpro.query_service;

import com.redpill_linpro.query_service.parser.QueryParser;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static com.redpill_linpro.query_service.util.ApplicationProperties.*;

@SpringBootApplication
public class QueryServiceApplication {

	public static void main(String[] args) {
		QueryParser.initProperties(getAppPropertyFile("model"));
		SpringApplication.run(QueryServiceApplication.class, args);
		System.out.println("\n Start Successful \n");
	}
}