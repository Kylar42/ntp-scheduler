package edu.wvup.cs460.db;

import edu.wvup.cs460.configuration.AppProperties;
import org.junit.Test;

/**
 * User: Tom Byrne
 * "If I am unable to see, it is because
 * I am being stood upon by giants."
 */
public class DBContextTest {

    @Test
    public void testCreateDBContext(){
        AppProperties properties =new AppProperties();
        DBContext dbContext = new DBContext(properties);
    }

}
