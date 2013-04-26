package edu.wvup.cs460.db;

import edu.wvup.cs460.configuration.AppProperties;

import java.util.Properties;

/**
 * User: Tom Byrne(kylar42@gmail.com)
 * "Code early, Code often."
 * A contextual object that creates a set of constants for DB Access from passed in properties.
 */
public class DBContext {
    final String DB_ROOT_URL;
    final String DB_ROOT_USER;
    final String DB_ROOT_PASSWORD;


    final String DB_NAME;// = "course_schedules";
    final String DB_URL;
    final String DB_CLASS = "org.postgresql.Driver";
    final String DB_READ_WRITE_USERNAME;
    final String DB_READ_WRITE_PASSWORD;

    final String DB_READ_ONLY_USERNAME;
    final String DB_READ_ONLY_PASSWORD;
    final int    CONNECTION_POOL_MAX_SIZE;

    //TODO: Init these from properties.


    public DBContext(AppProperties toInitFrom){
        String dbHost = toInitFrom.getProperty("db.host", "localhost");
        DB_ROOT_URL = "jdbc:postgresql://"+dbHost+"/";
        DB_ROOT_USER = toInitFrom.getProperty("db.root.username", "postgres");
        DB_ROOT_PASSWORD = toInitFrom.getProperty("db.root.password", "postgres");
        DB_NAME = toInitFrom.getProperty("db.name", "course_schedules");
        DB_URL = "jdbc:postgresql://"+dbHost+"/"+DB_NAME;
        DB_READ_WRITE_USERNAME = toInitFrom.getProperty("db.readwrite.username", "cs460");
        DB_READ_WRITE_PASSWORD = toInitFrom.getProperty("db.readwrite.password", "cs460");
        DB_READ_ONLY_USERNAME = toInitFrom.getProperty("db.readonly.username", "cs460");
        DB_READ_ONLY_PASSWORD = toInitFrom.getProperty("db.readonly.password", "cs460");
        CONNECTION_POOL_MAX_SIZE = toInitFrom.getPropertyAsInt("db.connectionpool.max.size", 10);




    }

}
