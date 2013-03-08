package edu.wvup.cs460;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import java.util.Properties;

/**
 * User: Tom Byrne(tom.byrne@apple.com)
 * Copyright (C) 2013 Apple Inc.
 * "Code early, Code often."
 */
public class AppProperties {


    private Properties mergedProperties = new Properties();

    /* friendly */ AppProperties(){

    }

    /* friendly */ void mergeProperties(Properties p){
        //Iterate to enforce string
        for(String key : p.stringPropertyNames()){
            mergedProperties.setProperty(key, p.getProperty(key));
        }
    }

    public String getProperty(String key){
        return findProperty(key);
    }

    public String getProperty(String key, String defaultVal){
        return findProperty(key, defaultVal);
    }

    private String findProperty(String key){
        String toReturn = mergedProperties.getProperty(key);
        return null == toReturn ? System.getProperty(key) : toReturn;
    }

    private String findProperty(String key, String defaultVal){
        String toReturn = mergedProperties.getProperty(key);
        return null == toReturn ? System.getProperty(key, defaultVal) : toReturn;
    }

    public long getPropertyAsLong(String key, long defaultValue){
        String val = findProperty(key);

        try{
            return null == val ? defaultValue : Long.parseLong(val);
        }catch(NumberFormatException ignore){
            return defaultValue;
        }
    }

    public long getPropertyAsInt(String key, int defaultValue){
        String val = findProperty(key);

        try{
            return null == val ? defaultValue : Integer.parseInt(val);
        }catch(NumberFormatException ignore){
            return defaultValue;
        }
    }

    public boolean getPropertyAsBoolean(String key, boolean defaultValue){
        String val = findProperty(key);
        return (null == val) ? defaultValue : Boolean.parseBoolean(val);

    }

}
