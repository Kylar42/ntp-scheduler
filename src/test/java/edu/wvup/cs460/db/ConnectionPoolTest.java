package edu.wvup.cs460.db;

import edu.wvup.cs460.configuration.AppProperties;
import org.junit.Test;

import java.sql.Connection;
import java.util.ArrayList;

/**
 * User: Tom Byrne
 * "If I am unable to see, it is because
 * I am being stood upon by giants."
 */
public class ConnectionPoolTest {
    @Test
    public void testGetConnection() {
        AppProperties props = new AppProperties();
        DBContext context = new DBContext(props);
        ConnectionPool pool = new ConnectionPool(context);
        Connection connection = pool.getConnection();
        pool.returnConnection(connection);
    }

    @Test
    public void testGetRootConnection() {
        AppProperties props = new AppProperties();
        DBContext context = new DBContext(props);
        ConnectionPool pool = new ConnectionPool(context);
        Connection connection = pool.getRootConnection();
        pool.returnRootConnection(connection);
    }

    @Test
    public void testConnectionPoolSize() {
        final AppProperties props = new AppProperties();
        final DBContext context = new DBContext(props);
        final ConnectionPool pool = new ConnectionPool(context);
        final Connection firstConnection = pool.getConnection();

        final ArrayList<Connection> conns = new ArrayList<Connection>();

        new Thread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                pool.returnConnection(firstConnection);
            }
        }.start();

        for(int i = 0; i < 10; i++){//last one will wait until the freed one happens.
            conns.add(pool.getConnection());
        }

        for(Connection c : conns){
            pool.returnConnection(c);
        }
    }
}
