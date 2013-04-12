package edu.wvup.cs460.http.authentication;

/**
 * User: Tom Byrne(kylar42@gmail.com)
 * "Code early, Code often."
 */
public class AuthenticationException extends Exception {

    public enum AuthExceptionType{
        BAD_USER, BAD_PASSWORD, EXPIRED, NOT_FOUND, UNKNOWN_COOKIE_VERSION, UNKNOWN;
    }


    private final AuthExceptionType _type;

    public AuthenticationException(AuthExceptionType type){
        _type = type;
    }


    public AuthExceptionType getType(){ return _type;}
}
