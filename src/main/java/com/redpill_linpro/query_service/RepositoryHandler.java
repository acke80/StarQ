package com.redpill_linpro.query_service;

import org.eclipse.rdf4j.federated.FedXFactory;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.*;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.http.HTTPRepository;
import org.eclipse.rdf4j.repository.RepositoryConnection;

import java.util.ArrayList;
import java.util.List;

public class RepositoryHandler {
    private RepositoryConnection connection;
    private final Repository repository = FedXFactory.newFederation()
            .withSparqlEndpoint("http://dbpedia.org/sparql")
            .withSparqlEndpoint("https://query.wikidata.org/sparql")
            .create();


    public RepositoryHandler() { }

    public List<String> sendQuery(String text) throws Exception{
        List<String> bindings = new ArrayList<>();


        try {
            connection = repository.getConnection();

            TupleQuery tupleQuery = connection.prepareTupleQuery(text);

            TupleQueryResult tupleQueryResult = tupleQuery.evaluate();

            while (tupleQueryResult.hasNext()) {
                BindingSet bindingSet = tupleQueryResult.next();
                System.out.println(bindingSet);

                for (Binding binding : bindingSet) {
                    String name = binding.getName();
                    Value value = binding.getValue();

                    bindings.add(name + " = " + value);
                    System.out.println(name + " = " + value);
                }
            }
        }finally{
            connection.close();
        }

        return bindings;
    }

}
