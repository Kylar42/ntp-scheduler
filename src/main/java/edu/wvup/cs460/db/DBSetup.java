package edu.wvup.cs460.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * User: Tom Byrne(tom.byrne@apple.com)
 * Copyright (C) 2011 Apple Inc.
 * "I code not because I have a problem to solve, but because there is
 * code within me, crying to get out."
 */
public class DBSetup {
    
    private static final DBSetup INSTANCE = new DBSetup();


    private static final String DB_CREATE = "CREATE DATABASE "+ConnectionPool.getInstance().dbName+";";
    private static final String DB_DROP = "DROP DATABASE "+ConnectionPool.getInstance().dbName+";";
    private static final String USER_DROP = "DROP USER "+ConnectionPool.getInstance().dbUser+";";
    private static final String USER_CREATE = "CREATE USER "+ConnectionPool.getInstance().dbUser+" with password '"+ConnectionPool.getInstance().dbPassword+"';";
    private static final String USER_GRANT = "GRANT ALL ON DATABASE "+ConnectionPool.getInstance().dbName+" to "+ConnectionPool.getInstance().dbUser;
    private static final String CREATE_MAIN_COURSE_TABLE =
            "CREATE TABLE IF NOT EXISTS COURSE_LIST ("+
                    "CRN VARCHAR(12) NOT NULL,"+ //CRN of course, like 5609
                    "TYPE VARCHAR(8) NOT NULL,"+ //Short type, like "E-C" or "Lec"
                    "CROSSLISTED BOOLEAN NOT NULL DEFAULT FALSE,"+ //Boolean
                    "SUBJECT VARCHAR(8) NOT NULL,"+ //Subject, like ECON
                    "COURSE_NUMBER VARCHAR(8) NOT NULL,"+
                    "COURSE_TITLE VARCHAR(255) NOT NULL,"+
                    "CREDITS SMALLINT NOT NULL,"+
                    "DAYS VARCHAR(16) NOT NULL,"+
                    "TIME VARCHAR(32) NOT NULL,"+
                    "INSTRUCTOR VARCHAR(64) NOT NULL,"+
                    "ROOM VARCHAR(32) NOT NULL,"+
                    "START_DATE timestamp NOT NULL,"+
                    "END_DATE timestamp NOT NULL,"+
                    "SEATS_AVAILABLE SMALLINT NOT NULL,"+
                    "TERM_LENGTH VARCHAR(64) NOT NULL,"+
                    "CAMPUS VARCHAR(32) NOT NULL,"+
                    "PRIMARY KEY (CRN));";


    /**
     * Type 		VARCHAR(8)
     Crosslisted Boolean
     CRN			VARCHAR(12)
     Subject		VARCHAR(8)
     CRS			VARCHAR(8)
     Title		VARCHAR(255)
     CR(edits)	INT
     Days		VARCHAR(7)
     Time		VARCHAR(32) (Note - should be start time and end time)
     Instructor 	VARCHAR(64)
     Room		VARCHAR(32)
     Start date	datetime
     Meeting dates - MEETING DATES TABLE
     Seats avail	unsigned int
     Length of term VARCHAR(64)
     End date 	datetime
     campus		VARCHAR(32)

     */
    public static void createOrSetupDB(){
        Connection myConnection = ConnectionPool.getInstance().getRootConnection();
        try{
            final PreparedStatement preparedStatement = myConnection.prepareStatement(DB_CREATE);
            final int executed = preparedStatement.executeUpdate();
            System.out.println("Created:"+executed);
        }catch(SQLException sql){
            sql.printStackTrace();
        }finally{
            ConnectionPool.getInstance().returnRootConnection(myConnection);
        }
    }
    public static void dropUser(){
        Connection myConnection = ConnectionPool.getInstance().getRootConnection();
        try{
            final PreparedStatement preparedStatement = myConnection.prepareStatement(USER_DROP);
            final int executed = preparedStatement.executeUpdate();
            System.out.println("DB Created:"+executed);
        }catch(SQLException sql){
            sql.printStackTrace();
        }finally{
            ConnectionPool.getInstance().returnRootConnection(myConnection);
        }
    }
    public static void createUser(){
        Connection myConnection = ConnectionPool.getInstance().getRootConnection();
        try{
            PreparedStatement preparedStatement = myConnection.prepareStatement(USER_CREATE);
            int executed = preparedStatement.executeUpdate();
            System.out.println("User Created:"+executed);
            preparedStatement = myConnection.prepareStatement(USER_GRANT);
            executed = preparedStatement.executeUpdate();
            System.out.println("User Granted:"+executed);
        }catch(SQLException sql){
            sql.printStackTrace();
        }finally{
            ConnectionPool.getInstance().returnRootConnection(myConnection);
        }
    }
    

    public static void createOrSetupTables(){
        Connection myConnection = ConnectionPool.getInstance().getConnection();
        try{
            final PreparedStatement preparedStatement = myConnection.prepareStatement(CREATE_MAIN_COURSE_TABLE);
            final int executed = preparedStatement.executeUpdate();
            System.out.println("Table Created:"+executed);
        }catch(SQLException sql){
            sql.printStackTrace();
        }finally{
            ConnectionPool.getInstance().returnConnection(myConnection);
        }
    }

    public static void dropDB(){
        Connection myConnection = ConnectionPool.getInstance().getRootConnection();
        try{
            final PreparedStatement preparedStatement = myConnection.prepareStatement(DB_DROP);
            final int executed = preparedStatement.executeUpdate();
            System.out.println("DB Dropped:"+executed);
        }catch(SQLException sql){
            sql.printStackTrace();
        }finally{
            ConnectionPool.getInstance().returnRootConnection(myConnection);
        }
    }

    public static void main(String[] args) {
        DBSetup.dropDB();
        DBSetup.dropUser();
        DBSetup.createOrSetupDB();
        DBSetup.createUser();
        DBSetup.createOrSetupTables();
    }
}

