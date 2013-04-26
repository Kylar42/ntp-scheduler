package edu.wvup.cs460.http;

import edu.wvup.cs460.NTPAppServer;
import edu.wvup.cs460.action.ChainStatus;
import edu.wvup.cs460.action.MethodResponse;
import edu.wvup.cs460.dataaccess.DataStorage;
import edu.wvup.cs460.http.authentication.Principal;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Tom Byrne(kylar42@gmail.com)
 * "Code early, Code often."
 *
 * This will be the base class for all our HTTP Methods.
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


    /**
     * easy implementation. If we're not authorized, send back an unauthorized response.
     * @param responseWrapper
     * @return
     */
    protected ChainStatus authenticate(ResponseWrapper responseWrapper){
        //if it's unauthenticated, send back a 401.
        final Principal principal = _context.getPrincipal();
        if(Principal.UNAUTHORIZED.equals(principal)){
            responseWrapper.writeUnauthorizedResponse("ntp");
            return ChainStatus.FAIL_DO_NOT_CONTINUE;
        }
        return ChainStatus.PASS_CONTINUE;
    }

    /**
     * A little protection against relativeURL hacks.
     * @param url
     * @return
     */
    protected ChainStatus checkForRelativeSegments(String url) {
        if (null != url && url.contains(".."))

        {
            return ChainStatus.FAIL_DO_NOT_CONTINUE;
        }

        return ChainStatus.PASS_CONTINUE;
    }

    /**
     * Convenience method to get a header value.
     * @param header
     * @param defaultValue
     * @return
     */
    protected String getHeaderValue(HeaderNames header, String defaultValue) {
        final String toReturn = _context.getHeaders().get(header.getFormattedValue());
        return (null == toReturn || toReturn.isEmpty()) ? defaultValue : toReturn;
    }

    /**
     * Convenience method to get the storage service.
     * @return
     */
    protected DataStorage getStorageService(){
        return NTPAppServer.getInstance().getStorageService();
    }


}
