package com.redpill_linpro.query_service;

import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.*;
import org.eclipse.rdf4j.repository.http.HTTPRepository;
import org.eclipse.rdf4j.repository.RepositoryConnection;

import java.util.ArrayList;
import java.util.List;

public class GraphDBHandler {
    private RepositoryConnection connection;


    public GraphDBHandler() { }

    public List<String> sendQuery(String query) throws Exception{
        List<String> bindings = new ArrayList<>();

        try {
            connectRepository();

            TupleQuery tupleQuery = connection.prepareTupleQuery(QueryLanguage.SPARQL, query);

            TupleQueryResult tupleQueryResult = tupleQuery.evaluate();
            while (tupleQueryResult.hasNext()) {
                BindingSet bindingSet = tupleQueryResult.next();

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

    private void connectRepository(){
        HTTPRepository repository = new HTTPRepository("http://localhost:7200/repositories/LearningGraphDB");
        connection = repository.getConnection();
    }
}
