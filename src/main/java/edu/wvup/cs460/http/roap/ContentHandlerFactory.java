package edu.wvup.cs460.http.roap;

import edu.wvup.cs460.action.MethodResponse;
import edu.wvup.cs460.http.ParsedURL;
import edu.wvup.cs460.http.ResponseWrapper;

/**
 * User: Tom Byrne(tom.byrne@apple.com)
 * Copyright (C) 2013 Apple Inc.
 * "Code early, Code often."
 */
public class ContentHandlerFactory{

    public static final ContentHandler UNKNOWN_HANDLER = new UnknownContentHandler();

    public interface ContentHandler{
       void handleContent(ResponseWrapper responseWrapper, Object content, ParsedURL url);
    }

    public static ContentHandler contentHandlerforURL(ParsedURL parsedURL){
        switch (parsedURL.getObjectType()) {
            case classlist:
                return new ClassListHandler();
            case classmeta:
                return new ClassMetaHandler();
            case unknown:
            default:
                return UNKNOWN_HANDLER;
        }

    }


    public static class UnknownContentHandler implements ContentHandler{
        @Override
        public void handleContent(ResponseWrapper responseWrapper, Object content, ParsedURL url) {
            responseWrapper.writeResponse(MethodResponse.NOT_FOUND);
        }
    }
}
