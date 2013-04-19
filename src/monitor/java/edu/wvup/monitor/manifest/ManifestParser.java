package edu.wvup.monitor.manifest;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.net.URLConnection;

/**
 * User: Tom Byrne(kylar42@gmail.com)
 * "Code early, Code often."
 */
public class ManifestParser {

    private final static Logger LOG = LoggerFactory.getLogger(ManifestParser.class);

    public static Manifest parseFromURL(URL manifestURL) {
        JSONObject parsedObject = null;
        try {
            final URLConnection urlConnection = manifestURL.openConnection();
            urlConnection.connect();
            final String contentType = urlConnection.getContentType();
            if (null != contentType && contentType.contains("application/json")) {

                JSONParser parser = new JSONParser(JSONParser.MODE_PERMISSIVE);
                //If it's a valid Manifest, it's a JSONObject, since our ManifestGenerator must have done it.
                //If this throws, we'll see it in the log, but we'll just back out and return null.
                parsedObject = (JSONObject) parser.parse(urlConnection.getInputStream());

            }
        } catch (Throwable e) {
            LOG.error("Unable to fetch data. Is the app running?", e);
            return null;
        }

        if (null == parsedObject) {
            return null;//don't continue if we didn't get anything.
        }




        return ManifestTransformer.manifestFromJSonObject(parsedObject);

    }


}
