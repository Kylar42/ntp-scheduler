package edu.wvup.cs460.util;

import edu.wvup.cs460.http.Constants;
import edu.wvup.cs460.http.MimeType;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * User: Tom Byrne(tom.byrne@apple.com)
 * "Code early, Code often."
 */
public class MimeUtils {

    public static final Map<String, MimeType> CONTENT_TYPES;
    static{
        CONTENT_TYPES = new HashMap<String, MimeType>();
        CONTENT_TYPES.put("css", MimeType.TEXT_CSS);
        CONTENT_TYPES.put("html", MimeType.TEXT_HTML);
        CONTENT_TYPES.put("js", MimeType.APP_JAVASCRIPT);
        CONTENT_TYPES.put("json", MimeType.APP_JSON);
        CONTENT_TYPES.put("ico", MimeType.XICON);

    }

    public static final MimeType contentTypeForFile(final File f){
        final String name = f.getName();
        final int suffixNdx = name.lastIndexOf(".");
        MimeType contentType = null;
        if(suffixNdx >= 0){
            final String suffix = name.substring(suffixNdx+1);
            contentType = CONTENT_TYPES.get(suffix);

        }

        if(null == contentType){
            contentType = MimeType.UNKNOWN;
        }

        return contentType;
    }

}