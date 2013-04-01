package edu.wvup.cs460.http;

/**
 * User: Tom Byrne(kylar42@gmail.com)
 * "Code early, Code often."
 */
public enum MimeType {

    //Any
    ANY                         ("*/*",                 false),

    //Text
    TEXT_HTML                   ("text/html",           true),
    TEXT_PLAIN                  ("text/plain",          true),
    TEXT_CSS                    ("text/css",            true),
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



    //image
    PNG     ("image/png",       false),
    JPEG    ("image/jpg",      false),
    XICON   ("image/x-icon",    false),

    BINARY_OCTET_STREAM ("binary/octet-stream",     false),

    VIDEO_QUICKTIME     ("video/quicktime",         false),

    UNKNOWN     ("application/unknown", false);


    private String  _typeString;
    private boolean _isString;

    private MimeType(final String typeString, final boolean isStringType){
        _typeString = typeString;
        _isString = isStringType;
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

    public static MimeType typeFromStringWithoutCharset(String mimeType){
        if(null == mimeType){
            return UNKNOWN;
        }
        for(MimeType mt : values()){
            if(mimeType.startsWith(mt._typeString)){
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
        if(_isString){
            final StringBuilder toReturn = new StringBuilder(_typeString);
            toReturn.append("; charset=").append(charset);
            return toReturn.toString();
        }
        return _typeString;
    }

    public boolean isString(){
        return _isString;
    }
}
