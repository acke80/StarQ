package com.redpill_linpro.query_service;

import java.util.ArrayList;
import java.util.List;

public class QueryHandler {
    SPARQLFileHandler file;
    List<String> prefixes = new ArrayList<String>();

    public QueryHandler(SPARQLFileHandler file){
        this.file = file;
        System.out.println(file.getFileData());
    }
}
