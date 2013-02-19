package edu.wvup.cs460.db;

import com.sun.org.apache.bcel.internal.generic.INSTANCEOF;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * User: Tom Byrne(tom.byrne@apple.com)
 * Copyright (C) 2011 Apple Inc.
 */

public class ConnectionPool {
    final String dbRootUrl = "jdbc:postgresql://localhost/";
    final String dbRootUser = "postgres";
    final String dbRootPassword = "postgres";//yes, yes, it's in plain text.


    final String dbName = "hr_info";
    final String dbUrl = "jdbc:postgresql://localhost/"+dbName;
    final String dbClass = "org.postgresql.Driver";
    final String dbUser = "cs460";
    final String dbPassword = "cs460";

    private static final ConnectionPool INSTANCE = new ConnectionPool();

    private final ConcurrentLinkedQueue<Connection> ROOT_CONNECTIONS = new ConcurrentLinkedQueue<Connection>() ;
    private final ConcurrentLinkedQueue<Connection> NORMAL_CONNECTIONS = new ConcurrentLinkedQueue<Connection>() ;

    public static ConnectionPool getInstance(){ return INSTANCE; }

    /** I am going to make this just create a new one for now. */
    public synchronized Connection getConnection(){
        if(null == NORMAL_CONNECTIONS.peek()){
            return INSTANCE.newDefaultConnection();
        }else{
            return NORMAL_CONNECTIONS.poll();
        }

    }
    
    private Connection newDefaultConnection(){
        try {
            Class.forName(dbClass);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Connection newRootConnection(){
        try {
            Class.forName(dbClass);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            return DriverManager.getConnection(dbRootUrl,dbRootUser, dbRootPassword);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Connection getRootConnection(){
        if(null == ROOT_CONNECTIONS.peek()){
            return newRootConnection();
        }else{
            return ROOT_CONNECTIONS.poll();
        }

    }
    
    public synchronized void returnConnection(Connection c){
        NORMAL_CONNECTIONS.add(c);
    }
    public synchronized void returnRootConnection(Connection c){
        ROOT_CONNECTIONS.add(c);
    }

}
