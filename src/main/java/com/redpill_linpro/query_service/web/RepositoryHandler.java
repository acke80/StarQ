package com.redpill_linpro.query_service.web;

import org.eclipse.rdf4j.query.*;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.http.HTTPRepository;
import org.eclipse.rdf4j.repository.RepositoryConnection;

import java.util.ArrayList;
import java.util.List;

import static com.redpill_linpro.query_service.util.ApplicationProperties.*;

public final class RepositoryHandler {
    private static RepositoryConnection connection = null;
    private static Repository repository = new HTTPRepository(getAppProperty("repoURL"));

    private RepositoryHandler() { }

    /**
     * Sends a query to a SPARQL endpoint and returns the data asked for in the query
     * @param text The query that will be sent to the endpoint
     * @return A list of names and values
     * @throws RepositoryException
     */
    public static List<String> sendQuery(String text) throws RepositoryException {
        List<String> bindings = new ArrayList<>();

        try {
            connection = repository.getConnection();

            TupleQuery tupleQuery = connection.prepareTupleQuery(QueryLanguage.SPARQL, text);

            TupleQueryResult tupleQueryResult = tupleQuery.evaluate();

            while (tupleQueryResult.hasNext()) {
                BindingSet bindingSet = tupleQueryResult.next();

                for (Binding binding : bindingSet) {
                    bindings.add(binding.getValue().stringValue());
                }
            }
        } finally{
            connection.close();
        }

        return bindings;
    }

}
