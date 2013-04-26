package edu.wvup.cs460.util;

import edu.wvup.cs460.http.MimeType;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * User: Tom Byrne(kylar42@gmail.com)
 * "Code early, Code often."
 * A set of Utils for creating MimeTypes and dealing with ContentTypes from files.
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
        CONTENT_TYPES.put("jpeg", MimeType.JPEG);
        CONTENT_TYPES.put("jpg", MimeType.JPEG);
        CONTENT_TYPES.put("xml", MimeType.APP_XML);
        CONTENT_TYPES.put("plist", MimeType.APP_PLIST);
        CONTENT_TYPES.put("txt", MimeType.TEXT_PLAIN);

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
