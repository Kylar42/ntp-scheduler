package edu.wvup.cs460.http.roap;

import edu.wvup.cs460.http.MethodContext;
import edu.wvup.cs460.http.MimeType;
import edu.wvup.cs460.http.ResponseWrapper;
import edu.wvup.cs460.http.WellKnownURLs;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;

/**
 * User: Tom Byrne(tom.byrne@apple.com)
 * Copyright (C) 2013 Apple Inc.
 * "Code early, Code often."
 */
public class AuthenticationMethodHandler implements ContentHandlerFactory.ContentHandler {

    @Override
    public void handleContent(ResponseWrapper responseWrapper, Object content, MethodContext context) {
        //was invalidate?

        responseWrapper.setReplayCookies(false);
        responseWrapper.writeResponse(HttpResponseStatus.OK, WellKnownURLs.LOGIN_PAGE, MimeType.TEXT_PLAIN);
    }
}
