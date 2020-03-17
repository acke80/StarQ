package com.redpill_linpro.query_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

@SpringBootApplication
public class QueryServiceApplication {

	public static void main(String[] args) throws IOException, URISyntaxException {
		SpringApplication.run(QueryServiceApplication.class, args);
		System.out.println("\n Start Successful \n");

		Vocabulary voc = new Vocabulary("https://swapi.co/vocabulary/");

		QueryParser.initProperties();

		QueryParser q1 = new QueryParser("Where was Shakespeare born?", voc);
		QueryParser q2 = new QueryParser("What is the temperature of truck?", voc);

	}

}
