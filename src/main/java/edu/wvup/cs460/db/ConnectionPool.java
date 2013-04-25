package edu.wvup.cs460.db;

import edu.wvup.cs460.configuration.AppProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 */

public class ConnectionPool {

    private final static Logger LOG = LoggerFactory.getLogger(ConnectionPool.class);


    private final int _maxConnectionCount;
    private final ArrayBlockingQueue<Connection> ROOT_CONNECTIONS;
    private final ArrayBlockingQueue<Connection> NORMAL_CONNECTIONS;
    private final AtomicInteger _normalConnectionCount = new AtomicInteger(0);//to be used to determine if we should poll or not.
    private final AtomicInteger _rootConnectionCount = new AtomicInteger(0);//to be used to determine if we should poll or not.

    private final DBContext _context;

    public ConnectionPool(DBContext context){
        _context = context;
        _maxConnectionCount = context.CONNECTION_POOL_MAX_SIZE;
        ROOT_CONNECTIONS = new ArrayBlockingQueue<Connection>(_maxConnectionCount);
        NORMAL_CONNECTIONS = new ArrayBlockingQueue<Connection>(_maxConnectionCount);
    }

    /** I am going to make this just create a new one for now. */
    public synchronized Connection getConnection(){
        if(null == NORMAL_CONNECTIONS.peek() && _normalConnectionCount.get() < _maxConnectionCount){
            _normalConnectionCount.getAndIncrement();
            return newDefaultConnection();
        }else{
            try {
                return NORMAL_CONNECTIONS.take();
            } catch (InterruptedException e) {
                LOG.error("Interrupted exception while getting connection!", e);
                return null;
            }
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
        if(null == ROOT_CONNECTIONS.peek() && _rootConnectionCount.get() < _maxConnectionCount){
            _rootConnectionCount.getAndIncrement();
            return newRootConnection();
        }else{
            try {
                return ROOT_CONNECTIONS.take();
            } catch (InterruptedException e) {
                LOG.error("Exception occurred trying to take a root connection.", e);
                return null;//catastrophic error has occurred.
            }
        }

    }
    
    public void returnConnection(Connection c){
        try {
            NORMAL_CONNECTIONS.put(c);
        } catch (InterruptedException e) {
            LOG.error("Interrupted Exception occurred trying to return a connection!");
        }
    }
    public void returnRootConnection(Connection c){
        try {
            ROOT_CONNECTIONS.put(c);
        } catch (InterruptedException e) {
            LOG.error("Error occurred trying to return a root connection!");
        }
    }

}
