package edu.wvup.cs460.http;

/**
 * User: Tom Byrne(kylar42@gmail.com)
 * "Code early, Code often."
 */
public enum HeaderNames {

    ContentType("Content-Type"),
    Authorization("Authorization");

    private String _formattedValue;
    private HeaderNames(String formattedValue){
        _formattedValue = formattedValue;
    }

    public String getFormattedValue(){
        return _formattedValue;
    }

}
