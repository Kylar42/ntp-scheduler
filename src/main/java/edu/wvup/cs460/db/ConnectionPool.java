package edu.wvup.cs460.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @user Tom Byrne (kylar42@gmail.com)
 * "If I cannot see, it is because I am being stood upon by giants."
 *
 * This is a ConnectionPool implemetation. It is thread-safe, and bounded, and calls to
 * get a connection will block until a connection is available.
 */

public class ConnectionPool {

    private final static Logger LOG = LoggerFactory.getLogger(ConnectionPool.class);

    //==========================================================================================
    //Data Members
    private final int _maxConnectionCount;

    private final ArrayBlockingQueue<Connection> ROOT_CONNECTIONS;
    private final ArrayBlockingQueue<Connection> NORMAL_CONNECTIONS;

    private final AtomicInteger _normalConnectionCount = new AtomicInteger(0);//to be used to determine if we should poll or not.
    private final AtomicInteger _rootConnectionCount = new AtomicInteger(0);//to be used to determine if we should poll or not.

    private final DBContext _context;

    //==========================================================================================
    public ConnectionPool(DBContext context){
        _context            = context;
        _maxConnectionCount = context.CONNECTION_POOL_MAX_SIZE;

        ROOT_CONNECTIONS    = new ArrayBlockingQueue<Connection>(_maxConnectionCount);
        NORMAL_CONNECTIONS  = new ArrayBlockingQueue<Connection>(_maxConnectionCount);
    }

    /**
     * This is a synchronized call to get a standard connection, and by using the take() method of the
     * BlockingQueue, it will block until a connection is available.
     * If a connection is requested, and one does not exist, and we have created less than the max, we'll create a new one.
     * @return A connection for DB operations.
     */
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

    /**
     * Create a standard DBConnection, using our readwrite user.
     * @return
     */
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

    /**
     * Create a new connection to the DB using our root user and password.
     * @return
     */
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
    /**
     * This is a synchronized call to get a root connection, and by using the take() method of the
     * BlockingQueue, it will block until a connection is available.
     * If a connection is requested, and one does not exist, and we have created less than the max, we'll create a new one.
     * These connections can be used to perform administration functions like creating and dropping tables and users.
     * This is used by the DBSetup functionality.
     * @return A connection for administrator DB operations.
     */

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

    /**
     * Non-blocking call to return a connection to the pool. If any threads are waiting for a connection, they will
     * be immediately notified.
     * @param c
     */
    public void returnConnection(Connection c){
        try {
            NORMAL_CONNECTIONS.put(c);
        } catch (InterruptedException e) {
            LOG.error("Interrupted Exception occurred trying to return a connection!");
        }
    }
    /**
      * Non-blocking call to return a root/admin connection to the pool. If any threads are waiting for a connection, they will
      * be immediately notified.
      * @param c
      */
    public void returnRootConnection(Connection c){
        try {
            ROOT_CONNECTIONS.put(c);
        } catch (InterruptedException e) {
            LOG.error("Error occurred trying to return a root connection!");
        }
    }

}
