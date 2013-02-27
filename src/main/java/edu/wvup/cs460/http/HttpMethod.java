package edu.wvup.cs460.http;

import org.jboss.netty.handler.codec.http.HttpRequest;

import java.io.InputStream;

/**
 * User: Tom Byrne(tom.byrne@apple.com)
 * Copyright (C) 2013 Apple Inc.
 * "Code early, Code often."
 */
public abstract class HttpMethod {


    public abstract void  handleRequest(RequestWrapper requestWrapper, ResponseWrapper responseWrapper);

}