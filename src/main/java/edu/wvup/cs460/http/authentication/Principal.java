package edu.wvup.cs460.http.authentication;

/**
 * User: Tom Byrne(tom.byrne@apple.com)
 * "Code early, Code often."
 */
public class Principal {

    public static final
    Principal UNAUTHORIZED = new Principal("UNAUTHORIZED");

    private String _user;

    public Principal(String user){
        _user = user;
    }

    public String getUser(){
        return _user;
    }
}
