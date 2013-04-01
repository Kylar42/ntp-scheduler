package edu.wvup.cs460.http.roap;

import edu.wvup.cs460.http.MethodContext;
import edu.wvup.cs460.http.ResponseWrapper;

/**
 * User: Tom Byrne(tom.byrne@apple.com)
 * Copyright (C) 2013 Apple Inc.
 * "Code early, Code often."
 */
public class AuthenticationMethodHandler implements ContentHandlerFactory.ContentHandler {

    @Override
    public void handleContent(ResponseWrapper responseWrapper, Object content, MethodContext context) {
        responseWrapper.writeUnauthorizedResponse("ntp");
    }
}
