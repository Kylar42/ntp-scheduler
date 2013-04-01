package edu.wvup.cs460.configuration;

import edu.wvup.cs460.util.PropertiesHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * User: Tom Byrne(kylar42@gmail.com)
 * "Code early, Code often."
 */
public class AppProperties {


    private Properties mergedProperties = new Properties();
    private Logger LOG = LoggerFactory.getLogger(AppProperties.class);

    public AppProperties(){

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
    public void initPropertiesFromCommandLine(String[] args){
        //look for properties
        Properties cliProps = PropertiesHelper.parsePropsFromCommandLine(args);

        final String mainPropsFilePath = cliProps.getProperty("main.properties");
        if(null != mainPropsFilePath){
            File mainPropsFile = new File(mainPropsFilePath);
            if(mainPropsFile.exists()){
                //read in main props
                try {
                    final Properties properties = PropertiesHelper.readPropsFile(mainPropsFile);
                    mergeProperties(properties);//merge to main.
                } catch (IOException e) {
                    LOG.error("Unable to read properties file.", e);
                }
            }
        }
        //System.getProperties().getProperty("someprops.propfield");

    }



}
