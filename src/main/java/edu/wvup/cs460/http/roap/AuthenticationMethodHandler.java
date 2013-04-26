package edu.wvup.cs460.http.roap;

import edu.wvup.cs460.http.MethodContext;
import edu.wvup.cs460.http.MimeType;
import edu.wvup.cs460.http.ResponseWrapper;
import edu.wvup.cs460.http.WellKnownURLs;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;

/**
 * User: Tom Byrne(kylar42@gmail.com)
 * "Code early, Code often."
 * Handle authentication method request - essentially a logout.
 */
public class AuthenticationMethodHandler implements ContentHandlerFactory.ContentHandler {

    @Override
    public void handleContent(ResponseWrapper responseWrapper, Object content, MethodContext context) {

        responseWrapper.setReplayCookies(false);
        responseWrapper.writeResponse(HttpResponseStatus.OK, WellKnownURLs.LOGIN_PAGE, MimeType.TEXT_PLAIN);
    }
}
