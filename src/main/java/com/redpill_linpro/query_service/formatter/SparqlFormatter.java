package com.redpill_linpro.query_service.formatter;

import com.redpill_linpro.query_service.util.SimpleTriple;
import com.redpill_linpro.query_service.util.Vocabulary;

import java.util.List;

//TODO: Make the query append first text from FileHandler instance.
public class SparqlFormatter {
    private List<SimpleTriple> simpleTriples;
    private Vocabulary vocabulary;
    private TripleFormatter tripleFormatter;

    public SparqlFormatter(TripleFormatter tripleFormatter, Vocabulary voc) {
        simpleTriples = tripleFormatter.getFormattedTriples();
        vocabulary = voc;
        this.tripleFormatter = tripleFormatter;
    }

    /** Creates a SPARQL query to send to a repository
     * @return A string in the form of a SPARQL query */
    public String createSparqlQuery() {
        StringBuilder query = new StringBuilder();
        String select = "?rootLabel ?answerLabel";
        query.append(sparqlSelection(select));

        for (SimpleTriple simpleTriple : simpleTriples) {
            if (!simpleTriple.isEmpty()) {
                String triple = simpleTriple.SUBJECT + " " + simpleTriple.RELATION + " " + simpleTriple.OBJECT + " .\n";
                query.append(triple);
                if (isLastTriple(simpleTriple)) {
                    if (tripleFormatter.hasRootLabel()) {
                        query.append("filter(contains(?rootLabel, " +
                                "\"" + tripleFormatter.getRootLabel() + "\"" + ")) .\n");
                    }
                    if(tripleFormatter.hasRelation()) {
                        query.append("optional {?answer rdfs:label ?literal .}\n" +
                                "bind(if(isURI(?answer), coalesce(?literal), coalesce(?answer)) as ?answerLabel) .\n");
                    }
                }
            }
        }

        query.append("}");
        String finalQuery = query.toString();
        System.out.println(finalQuery);
        return finalQuery;
    }

    // Set the selection for the query. Ex: * selects everything, ?s ?o selects the variables ?s and ?o.
    private StringBuilder sparqlSelection(String selection) {
        StringBuilder query = new StringBuilder();
        query.append(
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX voc: " + "<" + vocabulary.URI + ">" + "\n\n" +
                "select distinct " + selection + " where {\n");
        return query;
    }

    private boolean isLastTriple(SimpleTriple currentTriple) {
        if (simpleTriples.get(simpleTriples.size() - 1) == currentTriple)
            return true;
        else
            return false;
    }
}
