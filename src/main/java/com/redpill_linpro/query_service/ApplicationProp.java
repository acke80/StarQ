package com.redpill_linpro.query_service;

import com.redpill_linpro.query_service.util.FileHandler;
import edu.stanford.nlp.util.StringUtils;


public final class ApplicationProp {

    private static java.util.Properties properties =
            StringUtils.propFileToProperties(new FileHandler("application.properties").getPath());

    private ApplicationProp(){
    }

    public static FileHandler getAppPropertyFile(String key){
        return new FileHandler(properties.getProperty(key));
    }

    public static String getAppProperty(String key){
        return properties.getProperty(key);
    }

    public static void setAppProperty(String key, String value){
        properties.setProperty(key, value);
    }
}
