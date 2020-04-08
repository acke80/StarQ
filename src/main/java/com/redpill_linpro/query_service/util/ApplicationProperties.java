package com.redpill_linpro.query_service.util;

import edu.stanford.nlp.util.StringUtils;


public final class ApplicationProperties {

    private static java.util.Properties properties =
            StringUtils.propFileToProperties(new FileHandler("application.properties").getPath());

    private ApplicationProperties(){
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
