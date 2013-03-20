package edu.wvup.cs460.http;

import edu.wvup.cs460.dataaccess.DataStorage;
import edu.wvup.cs460.datamodel.CourseMetadata;
import edu.wvup.cs460.http.roap.ContentHandlerFactory;
import edu.wvup.cs460.util.MimeUtils;
import edu.wvup.cs460.util.StringUtils;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Tom Byrne(tom.byrne@apple.com)
 * Copyright (C) 2013 Apple Inc.
 * "Code early, Code often."
 */
public class PostMethod extends AbstractHttpMethod {

    Logger LOG = LoggerFactory.getLogger(PostMethod.class);

    public PostMethod(MethodContext context){
        super(context);
    }

    @Override
    public void handleRequest(RequestWrapper reqWrapper, ResponseWrapper respWrapper) {
        String url = context().getUri();
        //TODO: URL based dispatcher for REST.
        String contentType = getHeaderValue(HeaderNames.ContentType, null);
        Object parsedJson = null;
        if(MimeType.APP_JSON.formattedString().equals(contentType)){
            //parse JSON
            final String jsonString = reqWrapper.getRequest().getContent().toString(CharsetUtil.UTF_8);
            JSONParser parser = new JSONParser(JSONParser.MODE_PERMISSIVE);
            try {
                parsedJson = parser.parse(jsonString);
            } catch (ParseException e) {
                LOG.error("Unable to parse JSON body from request.", e);
            }
        }

        ParsedURL parsedURL = new ParsedURL(url);

        final ContentHandlerFactory.ContentHandler contentHandler = ContentHandlerFactory.contentHandlerforURL(parsedURL);

        contentHandler.handleContent(respWrapper, parsedJson, parsedURL);



    }






}
