package edu.wvup.cs460.db;

import edu.wvup.cs460.AppProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * User: Tom Byrne(tom.byrne@apple.com)
 * Copyright (C) 2011 Apple Inc.
 */

public class ConnectionPool {

    private final static Logger LOG = LoggerFactory.getLogger(ConnectionPool.class);


    private final ConcurrentLinkedQueue<Connection> ROOT_CONNECTIONS = new ConcurrentLinkedQueue<Connection>() ;
    private final ConcurrentLinkedQueue<Connection> NORMAL_CONNECTIONS = new ConcurrentLinkedQueue<Connection>() ;

    private final DBContext _context;

    public ConnectionPool(DBContext context){
        _context = context;
    }

    /** I am going to make this just create a new one for now. */
    public synchronized Connection getConnection(){
        if(null == NORMAL_CONNECTIONS.peek()){
            return newDefaultConnection();
        }else{
            return NORMAL_CONNECTIONS.poll();
        }

    }
    
    private Connection newDefaultConnection(){
        try {
            Class.forName(_context.DB_CLASS);
        } catch (ClassNotFoundException e) {
            LOG.error("Error occurred", e);
        }
        try {
            return DriverManager.getConnection(_context.DB_URL, _context.DB_READ_WRITE_USERNAME, _context.DB_READ_WRITE_PASSWORD);
        } catch (SQLException e) {
            LOG.error("Error occurred", e);
        }
        return null;
    }

    private Connection newRootConnection(){
        try {
            Class.forName(_context.DB_CLASS);
        } catch (ClassNotFoundException e) {
            LOG.error("Error occurred", e);
        }
        try {
            return DriverManager.getConnection(_context.DB_ROOT_URL, _context.DB_ROOT_USER, _context.DB_ROOT_PASSWORD);
        } catch (SQLException e) {
            LOG.error("Error occurred", e);
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
