package edu.wvup.cs460.http;

import edu.wvup.cs460.http.authentication.AuthenticationHandler;
import edu.wvup.cs460.http.authentication.Principal;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;

/**
 * User: Tom Byrne(tom.byrne@apple.com)
 * "Code early, Code often."
 */
public class MethodFactory {

    private final AuthenticationHandler _authHandler = new AuthenticationHandler();

    private static MethodFactory INSTANCE = new MethodFactory();

    enum MethodType{
        GET,
        POST
    }

    public static MethodFactory getInstance(){
        return INSTANCE;
    }



    public AbstractHttpMethod methodForRequest(HttpRequest httpRequest){

        final String header = httpRequest.getHeader(HeaderNames.Authorization.getFormattedValue());
        final Principal principal = _authHandler.authenticate(header);

        MethodContext context = new MethodContext(httpRequest, principal);
        //TODO: Real factory here.
        if(httpRequest.getMethod().equals(HttpMethod.POST)){
            return new PostMethod(context);
        }
        final String uri = httpRequest.getUri();
        if(uri.startsWith("/search/")){
            return new DynamicGetMethod(context);
        }
        return new GetMethod(context);
    }
}
