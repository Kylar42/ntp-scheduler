package edu.wvup.cs460.http;

/**
 * User: Tom Byrne(kylar42@gmail.com)
 * "Code early, Code often."
 * Enum for our header names.
 */
public enum HeaderNames {

    ContentType("Content-Type"),
    ContentLength("Content-Length"),
    Authorization("Authorization"),
    Cookie("Cookie"),
    SetCookie("Set-Cookie");

    private String _formattedValue;
    private HeaderNames(String formattedValue){
        _formattedValue = formattedValue;
    }

    public String getFormattedValue(){
        return _formattedValue;
    }

}
