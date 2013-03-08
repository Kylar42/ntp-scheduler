package edu.wvup.cs460.http;

import edu.wvup.cs460.util.MimeUtils;
import edu.wvup.cs460.util.StringUtils;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * User: Tom Byrne(tom.byrne@apple.com)
 * Copyright (C) 2013 Apple Inc.
 * "Code early, Code often."
 */
public class DynamicGetMethod extends AbstractHttpMethod {


    public DynamicGetMethod(MethodContext context){
        super(context);
    }

    @Override
    public void handleRequest(RequestWrapper reqWrapper, ResponseWrapper respWrapper) {
        String url = context().getUri();
        //open a file in our root.


    }



}
