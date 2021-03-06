package edu.wvup.cs460.http.authentication;

/**
 * User: Tom Byrne(kylar42@gmail.com)
 * "Code early, Code often."
 * Enum stating the different types of authentication that we will accept.
 * Note that I've included DIGEST in here, but there is no code to support it. We'll actually treat
 * digest as unauthorized.
 */
public enum AuthenticationTypes {

    BASIC("Basic"), DIGEST("Digest"), COOKIE("Cookie"), UNAUTHENTICATED("unauthenticated"), UNKNOWN("");

    private String _type;
    private AuthenticationTypes(String val){
        _type = val;
    }

    public String typeValue(){
        return _type;
    }

    public static AuthenticationTypes typeFromValue(String val){
        if(null == val){
            return UNKNOWN;
        }

        for(AuthenticationTypes type : values()){
            if(val.equalsIgnoreCase(type._type)){
                return type;
            }
        }
        return UNKNOWN;
    }
}



