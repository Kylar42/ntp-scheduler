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

        File inputFile = new File(Constants.STATIC_CONTENT_ROOT, url);
        if(inputFile.isDirectory()){
            //redirect to index.html.
            StringBuilder newUrl = new StringBuilder(url);
            if(newUrl.charAt(newUrl.length()-1) != '/'){
                newUrl.append('/');
            }
            newUrl.append("index.html");
            //respWrapper.sendRedirect(newUrl.toString());
            //return;
            inputFile = new File(Constants.STATIC_CONTENT_ROOT, newUrl.toString());
        }

        if(!inputFile.exists()){
            respWrapper.writeResponse(HttpResponseStatus.NOT_FOUND, "URL does not exist.", null);
            return;
        }

        try {
            FileInputStream fis = new FileInputStream(inputFile);
            String output = null;
            try {
                output = StringUtils.readString(fis, Charset.forName("UTF8"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            respWrapper.writeResponse(HttpResponseStatus.OK, output, MimeUtils.contentTypeForFile(inputFile));
            return;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }



}
