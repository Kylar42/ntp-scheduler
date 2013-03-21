package edu.wvup.cs460.http;

import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;

/**
 * User: Tom Byrne(tom.byrne@apple.com)
 * Copyright (C) 2013 Apple Inc.
 * "Code early, Code often."
 */
public class MethodFactory {

    private static MethodFactory INSTANCE = new MethodFactory();

    enum MethodType{
        GET,
        POST
    }

    public static MethodFactory getInstance(){
        return INSTANCE;
    }


    public AbstractHttpMethod methodForRequest(HttpRequest httpRequest){
        MethodContext context = new MethodContext(httpRequest);
        //TODO: Real factory here.
        if(httpRequest.getMethod().equals(HttpMethod.POST)){
            return new PostMethod(context);
        }
        final String uri = httpRequest.getUri();
        if(uri.startsWith("/search/")){
            return new DynamicGetMethod(context);
        }
        return new GetMethod(context);
    }
}
