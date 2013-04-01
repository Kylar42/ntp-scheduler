package edu.wvup.cs460.http.roap;

import edu.wvup.cs460.action.MethodResponse;
import edu.wvup.cs460.http.MethodContext;
import edu.wvup.cs460.http.ParsedURL;
import edu.wvup.cs460.http.ResponseWrapper;
import org.jboss.netty.handler.codec.http.HttpMethod;

/**
 * User: Tom Byrne(kylar42@gmail.com)
 * "Code early, Code often."
 */
public class ContentHandlerFactory {

    public static final ContentHandler UNKNOWN_HANDLER = new UnknownContentHandler();

    public interface ContentHandler {
        void handleContent(ResponseWrapper responseWrapper, Object content, MethodContext context);
    }

    public static ContentHandler contentHandlerforURL(ParsedURL parsedURL, HttpMethod method) {
        if (HttpMethod.POST == method) {
            switch (parsedURL.getObjectType()) {
                case classlist:
                    return new ClassListHandler();
                case classmeta:
                    return new ClassMetaHandler();
                case authentication:
                    return new AuthenticationMethodHandler();
                case unknown:
                default:
                    return UNKNOWN_HANDLER;
            }
        }
        if (HttpMethod.GET == method) {
            if (parsedURL.getObjectType() == ParsedURL.OBJECT_TYPE.authentication) {
                return new AuthenticationMethodHandler();
            } else {
                return UNKNOWN_HANDLER;
            }
        }


        return UNKNOWN_HANDLER;


    }


    public static class UnknownContentHandler implements ContentHandler {
        @Override
        public void handleContent(ResponseWrapper responseWrapper, Object content, MethodContext context) {
            responseWrapper.writeResponse(MethodResponse.NOT_FOUND);
        }
    }
}
