package edu.wvup.monitor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * User: Tom Byrne(kylar42@gmail.com)
 * "Code early, Code often."
 */
public class Util {

    public static Properties parsePropsFromCommandLine( final String[] cliArgs) {
        final Properties cliProps = new Properties();

        if (cliArgs != null) {
            for (final String curArg : cliArgs) {
                if (curArg.startsWith("-D")) {
                    final String keyValueString = curArg.substring(2);
                    final int indexOfEquals = keyValueString.indexOf(('='));
                    if (indexOfEquals == -1) {
                        throw new RuntimeException("Invalid parameter definition: " + curArg);
                    }
                    final String keyString = keyValueString.substring(0, indexOfEquals).trim();
                    final String valueString = keyValueString.substring(indexOfEquals + 1).trim();
                    cliProps.put(keyString, valueString);
                }
            }
        }
        return cliProps;
    }

    public static Properties readPropsFile(File propsFile) throws IOException {
        Properties propsToReturn = new Properties();

        propsToReturn.load(new FileInputStream(propsFile));

        return propsToReturn;
    }

    public static String toHexString(byte[] bytes){
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            sb.append(Integer.toHexString(0xff & bytes[i]));
        }
        return sb.toString();
    }
}
