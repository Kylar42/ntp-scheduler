package edu.wvup.cs460.http;

/**
 * User: Tom Byrne(tom.byrne@apple.com)
 * Copyright (C) 2013 Apple Inc.
 * "Code early, Code often."
 */
public enum MimeType {

    //Any
    ANY                         ("*/*",                 false),

    //Text
    TEXT_HTML                   ("text/html",           true),
    TEXT_PLAIN                  ("text/plain",          true),
    TEXT_CSS                    ("text/css",            true),
    TEXT_XML                    ("text/xml",            false), // Some RFCs have not been updated to application/xml
    TEXT_VCARD                  ("text/vcard",          true),
    TEXT_PLIST                  ("text/plist",          true),
    TEXT_OCTET_STREAM           ("text/octet-stream",   false),
    TEXT_ANY                    ("text/*",              true),


    //application
    APP_WWW_FORM_ENCODED        ("application/x-www-form-urlencoded",   true),
    APP_PLIST                   ("application/x-plist",                 false),
    APP_PROTOBUF                ("application/protobuf",                false),
    APP_JAVASCRIPT              ("application/javascript",              true),
    APPLICATION_OCTET_STREAM    ("application/octet-stream",            false),
    APP_JSON                    ("application/json",                    true),
    APP_XML                     ("application/xml",                     true),
    APP_XBEL                    ("application/xbel+xml",                true),
    APP_ZIP                     ("application/zip",                     false),
    APP_APPLE_XML_PLIST         ("application/x-apple-plist",           true),



    //image
    PNG     ("image/png",       false),
    JPEG    ("image/jpeg",      false),
    XICON   ("image/x-icon",    false),


    BINARY_OCTET_STREAM ("binary/octet-stream",     false),

    VIDEO_QUICKTIME     ("video/quicktime",         false),

    UNKNOWN             ("application/newcastle",   false);


    private String  _typeString;
    private boolean _isStringType;

    private MimeType(final String typeString, final boolean isStringType){
        _typeString = typeString;
        _isStringType = isStringType;
    }

    public static MimeType typeFromStringValue(final String value){
        if(null == value || value.length() < 1){
            return UNKNOWN;
        }

        for(MimeType mt : values()){
            if(mt._typeString.equalsIgnoreCase(value)){
                return mt;
            }
        }

        return UNKNOWN;
    }

    public final String formattedString(){
        //return with default Charset
        return formattedString("UTF-8");
    }

    public final String formattedString(String charset){
        if(_isStringType){
            final StringBuilder toReturn = new StringBuilder(_typeString);
            toReturn.append("; charset=").append(charset);
            return toReturn.toString();
        }
        return _typeString;
    }
}
