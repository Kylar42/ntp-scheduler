package edu.wvup.cs460.http;

import edu.wvup.cs460.http.authentication.AuthenticationHandler;
import edu.wvup.cs460.http.authentication.Principal;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;

import java.util.List;
import java.util.Map;

/**
 * User: Tom Byrne(kylar42@gmail.com)
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
        final List<Map.Entry<String,String>> headers = httpRequest.getHeaders();


        final Principal principal = _authHandler.authenticateFromCookie(headers);//will fail if there is no cookie, and return Unauthorized.

        MethodContext context = new MethodContext(httpRequest, principal);

        //ParsedURI inside context can give us some extra information.
        ParsedURL pUrl = context.getParsedURL();
        if(pUrl.getObjectType() == ParsedURL.OBJECT_TYPE.authentication){
            return new AuthenticationMethod(context, _authHandler);
        }


        //if we are unauthorized, we can only allow access to the authorization page.
        if(Principal.UNAUTHORIZED == principal){
            if(httpRequest.getMethod().equals(HttpMethod.GET)){
                return new UnauthorizedGetMethod(context);
            }
            //if it's a POST or other, we'll fall through and fail.
        }


        if(httpRequest.getMethod().equals(HttpMethod.POST)){
            return new PostMethod(context);
        }
        final String uri = httpRequest.getUri();
        if(uri.startsWith("/search/")){
            return new DynamicGetMethod(context);
        }
        return new GetMethod(context);
    }

    private boolean isLoginAction(ParsedURL parsedURL){
        return null != parsedURL &&
                ParsedURL.OBJECT_TYPE.authentication.equals(parsedURL.getObjectType()) && //authentication object
                ParsedURL.ACTION_TYPE.validate.equals(parsedURL.getActionType());//validate action
    }
}
