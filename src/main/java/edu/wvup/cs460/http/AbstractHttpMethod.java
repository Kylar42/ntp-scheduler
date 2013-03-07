package edu.wvup.cs460.http;

import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * User: Tom Byrne(tom.byrne@apple.com)
 * Copyright (C) 2013 Apple Inc.
 * "Code early, Code often."
 */
public abstract class AbstractHttpMethod {

    public abstract void  handleRequest(RequestWrapper requestWrapper, ResponseWrapper responseWrapper);


}
