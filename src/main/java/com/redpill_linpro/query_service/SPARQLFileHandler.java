package com.redpill_linpro.query_service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/*
* Used to import SPARQL code from file to string
*/
public class SPARQLFileHandler {

    private String dir;
    private StringBuilder stringBuilder;

    public SPARQLFileHandler(String dir){
        this.dir = dir;
        stringBuilder = new StringBuilder();
    }

    public String getFileData(){
        String data = "";
        try{
            data = readFileData();
        }catch(IOException e){
            e.printStackTrace();
        }
        return data;
    }

    private String readFileData() throws IOException {
        StringBuilder contentBuilder = new StringBuilder();
        BufferedReader br = new BufferedReader(new FileReader(this.dir));
        String line;

        while ((line = br.readLine()) != null)
            contentBuilder.append(line).append("\n");

        return contentBuilder.toString();
    }

}
