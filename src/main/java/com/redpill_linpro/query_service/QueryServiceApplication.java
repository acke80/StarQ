package com.redpill_linpro.query_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.net.URISyntaxException;

@SpringBootApplication
public class QueryServiceApplication {

	public static void main(String[] args) throws IOException, URISyntaxException {
		SpringApplication.run(QueryServiceApplication.class, args);
		System.out.println("\n Start Successful \n");

		QueryParser.initProperties(NerTrain.modelFile);

	}
}