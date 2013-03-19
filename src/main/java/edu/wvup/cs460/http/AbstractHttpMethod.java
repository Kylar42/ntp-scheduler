package edu.wvup.cs460.http;

import edu.wvup.cs460.NTPAppServer;
import edu.wvup.cs460.action.ChainStatus;
import edu.wvup.cs460.action.MethodResponse;
import edu.wvup.cs460.dataaccess.DataStorage;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
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

    private MethodContext _context;

    protected AbstractHttpMethod(MethodContext context) {
        _context = context;
    }


    protected MethodContext context() {
        return _context;
    }

    public abstract void handleRequest(RequestWrapper requestWrapper, ResponseWrapper responseWrapper);

    protected ChainStatus checkForRelativeSegments(String url) {
        if (null != url && url.contains(".."))

        {
            return ChainStatus.FAIL_DO_NOT_CONTINUE;
        }

        return ChainStatus.PASS_CONTINUE;
    }

    protected String getHeaderValue(HeaderNames header, String defaultValue) {
        final String toReturn = _context.getHeaders().get(header.getFormattedValue());
        return (null == toReturn || toReturn.isEmpty()) ? defaultValue : toReturn;
    }

    protected DataStorage getStorageService(){
        return NTPAppServer.getInstance().getStorageService();
    }
}