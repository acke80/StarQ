package com.redpill_linpro.query_service;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.sequences.SeqClassifierFlags;
import edu.stanford.nlp.util.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.Scanner;

@SpringBootApplication
public class QueryServiceApplication {

	public static void main(String[] args) throws IOException, URISyntaxException {
		SpringApplication.run(QueryServiceApplication.class, args);
		System.out.println("\n Start Successful \n");

		Vocabulary voc = new Vocabulary("https://swapi.co/vocabulary/");
		for(String s : voc.getRelations())
			System.out.println(s);

		QueryParser.initProperties();

		/*
		QueryParser q1 = new QueryParser("Where was Anakin Skywalker born?", voc);
		QueryParser q2 = new QueryParser("Where does Yoda live?", voc);
		QueryParser q3 = new QueryParser("What is the gravity of Tatooine?", voc);
		QueryParser q4 = new QueryParser("What height is Yoda?", voc);
		*/

		Scanner s = new Scanner(System.in);
		QueryServiceApplication qsa = new QueryServiceApplication();
		QueryParser q;

		while(true){
			String str = s.nextLine();
			q = new QueryParser(str, voc);
		}
	}
}
