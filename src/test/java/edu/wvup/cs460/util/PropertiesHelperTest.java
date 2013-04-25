package edu.wvup.cs460.util;

import org.junit.Before;
import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.assertEquals;

/**
 * User: Tom Byrne
 * "If I am unable to see, it is because
 * I am being stood upon by giants."
 */
public class PropertiesHelperTest {

    final String[] commandLineArgs = {"-Darg1=value1","-Darg2=value2"};


    @Test
    public void initFromCommandLineTest(){
        Properties properties = PropertiesHelper.parsePropsFromCommandLine(commandLineArgs);
        String val1 = properties.getProperty("arg1", null);

        assertEquals("value1", val1);


        String val2 = properties.getProperty("arg2", null);
        assertEquals("value2", val2);

        properties = PropertiesHelper.parsePropsFromCommandLine(null);

        val1 = properties.getProperty("arg1", "foobar");
        assertEquals("foobar", val1);


    }



}
