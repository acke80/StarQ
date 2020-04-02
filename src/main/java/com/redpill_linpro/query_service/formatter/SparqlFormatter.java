package com.redpill_linpro.query_service.formatter;

import com.redpill_linpro.query_service.util.SimpleTriple;
import com.redpill_linpro.query_service.util.Vocabulary;

import java.util.List;

public class SparqlFormatter {
    private List<SimpleTriple> simpleTriples;
    private Vocabulary vocabulary;

    public void SparqlFormatter(TripleFormatter tripleFormatter, Vocabulary voc) {
        simpleTriples = tripleFormatter.getFormattedTriples();
        vocabulary = voc;
    }

    public String createSparqlQuery() {
        StringBuilder query = new StringBuilder();
        query.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX voc: " + vocabulary.URI + "\n" +
                "select distinct ?answer where {\n");

        for (int i = 0; i < simpleTriples.size(); i++) {
            query.append(simpleTriples.get(i).SUBJECT + " " +
                    simpleTriples.get(i).RELATION + " " +
                    simpleTriples.get(i).OBJECT + ".\n");
        }

        query.append("}");
        String finalQuery = query.toString();
        return finalQuery;
    }
}
