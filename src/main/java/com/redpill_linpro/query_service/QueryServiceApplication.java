package com.redpill_linpro.query_service;

import com.redpill_linpro.query_service.util.Vocabulary;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Scanner;

@SpringBootApplication
public class QueryServiceApplication {

	public static void main(String[] args) throws IOException, URISyntaxException {
		SpringApplication.run(QueryServiceApplication.class, args);
		System.out.println("\n Start Successful \n");

		QueryParser.initProperties(NerTrain.modelFile);

		/*
		QueryParser q1 = new QueryParser("Where was Anakin Skywalker born?", voc);
		QueryParser q2 = new QueryParser("Where does Yoda live?", voc);
		QueryParser q3 = new QueryParser("What is the gravity of Tatooine?", voc);
		QueryParser q4 = new QueryParser("What height is Yoda?", voc);
		*/
	}
}
