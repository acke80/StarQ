package com.redpill_linpro.query_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class QueryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(QueryServiceApplication.class, args);
		System.out.println("\n Start Successful \n");
		//QueryParser qp = new QueryParser("What is the capital of France?");
	}
}
