package com.redpill_linpro.query_service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class SparqlFileHandler {

    private String dir;

    public SparqlFileHandler(String dir){
        this.dir = dir;
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

    private void writeFileData(){

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
