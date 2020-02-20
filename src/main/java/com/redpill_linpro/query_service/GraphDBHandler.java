package com.redpill_linpro.query_service;

import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.*;
import org.eclipse.rdf4j.repository.http.HTTPRepository;
import org.eclipse.rdf4j.repository.RepositoryConnection;

public class GraphDBHandler {
    private RepositoryConnection connection;


    public GraphDBHandler() {
        connectRepository();
    }

    public void sendQuery(String query) {
        TupleQuery tupleQuery = connection.prepareTupleQuery(QueryLanguage.SPARQL, query);

        TupleQueryResult tupleQueryResult = tupleQuery.evaluate();
        while (tupleQueryResult.hasNext()) {
            BindingSet bindingSet = tupleQueryResult.next();

            for (Binding binding : bindingSet) {
                String name = binding.getName();
                Value value = binding.getValue();

                System.out.println(name + " = " + value);
            }
        }
    }

    private void connectRepository(){
        HTTPRepository repository = new HTTPRepository("http://localhost:7200/repositories/LearningGraphDB");
        connection = repository.getConnection();
    }
}
