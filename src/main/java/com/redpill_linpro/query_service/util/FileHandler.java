package com.redpill_linpro.query_service.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public final class FileHandler {

    private String dir;

    public FileHandler(String dir){
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

    public String getPath(){
        return this.getClass().getClassLoader().getResource(dir).getPath();
    }

    private String readFileData() throws IOException {
        StringBuilder contentBuilder = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(
                this.getClass().getResourceAsStream("/" + dir)));

        String line;

        while ((line = br.readLine()) != null)
            contentBuilder.append(line).append("\n");

        br.close();

        return contentBuilder.toString();
    }

}
