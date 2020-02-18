package com.redpill_linpro.query_service;

import java.util.ArrayList;
import java.util.List;

public class QueryHandler {
    private SparqlFileHandler file;
    private String query;


    public QueryHandler(SparqlFileHandler file){
        this.file = file;
        this.query = this.file.getFileData();
    }

    public QueryHandler(String query){
        this(new SparqlFileHandler(""));

    }

}
