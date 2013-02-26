package edu.wvup.cs460.http;

import org.jboss.netty.handler.codec.http.HttpRequest;

/**
 * User: Tom Byrne(tom.byrne@apple.com)
 * Copyright (C) 2013 Apple Inc.
 * "Code early, Code often."
 */
public class MethodFactory {

    private static MethodFactory INSTANCE = new MethodFactory();


    public static MethodFactory getInstance(){
        return INSTANCE;
    }


    public HttpMethod methodForRequest(HttpRequest httpRequest){
        return new GetMethod();
    }
}
