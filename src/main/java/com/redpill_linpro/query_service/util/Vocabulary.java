package com.redpill_linpro.query_service.util;

import com.redpill_linpro.query_service.RepositoryHandler;
import org.apache.jena.query.ParameterizedSparqlString;

import java.util.ArrayList;
import java.util.List;

public final class Vocabulary {

    public final String URI;
    public final String QUERY;

    private String[] relations;

    public Vocabulary(String uri){
        URI = uri;
        QUERY = generateQuery();
        generateVocabulary();
    }

    /** Generates the query for selecting all relations in the repository. */
    private String generateQuery(){
        ParameterizedSparqlString pss = new ParameterizedSparqlString();
        pss.setCommandText(new FileHandler("static/sparql/vocabularyQuery").getFileData());
        pss.setNsPrefix("voc", URI);
        return pss.toString();
    }

    /** Populates the relations list by sending the QUERY to the Repository, and
     * retrieving all the relations. */
    private void generateVocabulary(){
        List<String> uriRelations = new ArrayList<>();
        try{
             uriRelations = RepositoryHandler.sendQuery(QUERY);
        } catch(Exception e){
            e.printStackTrace();
        }

        relations = new String[uriRelations.size()];

        for(int i = 0; i < relations.length; i++){
            String[] splits = uriRelations.get(i).split("/");
            relations[i] = splits[splits.length - 1];
        }
    }

    public String[] getRelations(){
        return relations;
    }

}
