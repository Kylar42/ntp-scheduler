package edu.wvup.cs460.db;

import edu.wvup.cs460.AppProperties;

import java.util.Properties;

/**
 * User: Tom Byrne(tom.byrne@apple.com)
 * "Code early, Code often."
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

    //TODO: Init these from properties.


    DBContext(AppProperties toInitFrom){
        String dbHost = toInitFrom.getProperty("db.host", "localhost");
        DB_ROOT_URL = "jdbc:postgres://"+dbHost+"/";
        DB_ROOT_USER = toInitFrom.getProperty("db.root.username", "postgres");
        DB_ROOT_PASSWORD = toInitFrom.getProperty("db.root.password", "postgres");
        DB_NAME = toInitFrom.getProperty("db.name", "course_schedules");
        DB_URL = "jdbc:postgres://"+dbHost+"/"+DB_NAME;
        DB_READ_WRITE_USERNAME = toInitFrom.getProperty("db.readwrite.username", "cs460");
        DB_READ_WRITE_PASSWORD = toInitFrom.getProperty("db.readwrite.password", "cs460");
        DB_READ_ONLY_USERNAME = toInitFrom.getProperty("db.readonly.username", "cs460");
        DB_READ_ONLY_PASSWORD = toInitFrom.getProperty("db.readonly.password", "cs460");

    }

}
