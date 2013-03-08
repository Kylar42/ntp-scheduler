package edu.wvup.cs460.http;

/**
 * User: Tom Byrne(tom.byrne@apple.com)
 * Copyright (C) 2013 Apple Inc.
 * "Code early, Code often."
 */
public enum HeaderNames {

    ContentType("Content-Type");

    private String _formattedValue;
    private HeaderNames(String formattedValue){
        _formattedValue = formattedValue;
    }

    public String getFormattedValue(){
        return _formattedValue;
    }

}
