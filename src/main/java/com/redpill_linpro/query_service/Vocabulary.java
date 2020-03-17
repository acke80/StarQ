package com.redpill_linpro.query_service;

import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.tomcat.jni.File;

public final class Vocabulary {

    public final String URI;
    public final String QUERY;
    public final String FILENAME;

    private String[] relations;

    public Vocabulary(String fileName, String uri){
        FILENAME = fileName;
        URI = uri;
        QUERY = generateQuery();
    }

    public String getMatch(String relation){
        return null;
    }

    /** Generates the query for selecting all relations in the repository. */
    private String generateQuery(){
        ParameterizedSparqlString pss = new ParameterizedSparqlString();
        pss.setCommandText(new FileHandler("voc/vocabularyQuery").getFileData());
        pss.setNsPrefix("voc", URI);
        return pss.toString();
    }

    private void generateVocabulary(){

    }
}
