package edu.wvup.cs460.http.authentication;

/**
 * User: Tom Byrne(kylar42@gmail.com)
 * "Code early, Code often."
 */
public class Principal {

    public static final Principal UNAUTHORIZED = new Principal("UNAUTHORIZED");
    public static final Principal READ_USER = new Principal("READ");
    public static final Principal READ_WRITE_USER = new Principal("READ_WRITE");

    private String _user;

    public Principal(String user){
        _user = user;
    }

    public String getUser(){
        return _user;
    }
}
