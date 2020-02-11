package com.redpill_linpro.query_service;

public final class QueryParser {

    public static SPARQLFileHandler sf = new SPARQLFileHandler("res/test.txt");

    private QueryParser(){ }

    public static QueryHandler ParseSPARQL(SPARQLFileHandler file){
        return new QueryHandler(file);
    }
}
