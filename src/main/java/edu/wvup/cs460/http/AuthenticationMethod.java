package edu.wvup.cs460.http;

import edu.wvup.cs460.action.ChainStatus;
import edu.wvup.cs460.action.MethodResponse;
import edu.wvup.cs460.http.authentication.AuthenticationContext;
import edu.wvup.cs460.http.authentication.AuthenticationHandler;
import edu.wvup.cs460.http.authentication.AuthenticationTypes;
import edu.wvup.cs460.http.authentication.Principal;
import edu.wvup.cs460.util.Tuple;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.jboss.netty.handler.codec.http.Cookie;
import org.jboss.netty.handler.codec.http.DefaultCookie;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;

import java.util.List;
import java.util.Map;

/**
 * Handle calls for authentication: logging in and logging out.
 * User: Tom Byrne(kylar42@gmail.com)
 * "Code early, Code often."
 *
 */
public class AuthenticationMethod extends PostMethod{
    final AuthenticationHandler _handler;
    public AuthenticationMethod(MethodContext context, AuthenticationHandler authenticationHandler) {
        super(context);
        _handler = authenticationHandler;//use to create cookies and send back redirect.
    }

    @Override
    public void handleRequest(RequestWrapper reqWrapper, ResponseWrapper respWrapper) {
        //check to see if we're a logout, if so, do logout actions. If not, do login actions.
        ParsedURL url = context().getParsedURL();
        if(url.getObjectType() != ParsedURL.OBJECT_TYPE.authentication){
            sendBadRequestResponse(respWrapper, "Bad Request.");
            return;
        }

        if(url.getActionType() == ParsedURL.ACTION_TYPE.invalidate){
            doLogoutActions(reqWrapper, respWrapper);
            return;
        }
        //if we get here, we will assume we're doing a validate authentication, or try to. This will redirect to the login page if it's bad anyways.

        Object jsonBody = parseJSonObjectFromRequest(reqWrapper);

        doLoginActions(respWrapper, jsonBody);
    }


    private void doLogoutActions(RequestWrapper reqWrapper, ResponseWrapper respWrapper){
        //we need to remove all the cookies set, set a new expired cookie, and drop any passed in cookies out of the cache.
        _handler.removeAllCookiesFromCache(reqWrapper.getRequest().getHeaders());
        respWrapper.setReplayCookies(false);
        Cookie expirationCookie = new DefaultCookie("NTP", "expired");
        expirationCookie.setPath("/");
        expirationCookie.setMaxAge(0);//this will cause the browser to expire it immediately.
        respWrapper.addCookie(expirationCookie);
        respWrapper.sendRedirect(WellKnownURLs.LOGIN_PAGE);
        return;
    }


    private void doLoginActions(ResponseWrapper respWrapper, Object jsonBody) {
        if(null == jsonBody){
            sendBadRequestResponse(respWrapper, "POST Data without JSON body.");
            return;
        }

        if(jsonBody instanceof JSONArray){
            JSONArray array = (JSONArray)jsonBody;
            //should only be two, username, password.
            Tuple<String, String> credentials = extractCredentials(array);
            if(null == credentials.getKey() || null == credentials.getValue()){
                sendBadRequestResponse(respWrapper, "Invalid Data in JSON.");
                return;
            }

            //if we got here we have credentials. Let's see if we can authenticate.
            AuthenticationContext authContext = new AuthenticationContext(credentials.getKey(), credentials.getValue(), AuthenticationTypes.COOKIE, "NTP");
            final Principal principal = _handler.authenticate(authContext);
            if(principal == Principal.UNAUTHORIZED){
                //send back bad juju.
                respWrapper.setReplayCookies(false);
                respWrapper.writeResponse(HttpResponseStatus.FORBIDDEN, "Invalid Credentials.", MimeType.TEXT_PLAIN);
            }else{
                //set a cookie
                final String cookieVal = _handler.cookieHandler().createAndStoreCookie(authContext, 3600000, "");
                Cookie newCookie = new DefaultCookie("NTP", cookieVal);
                newCookie.setPath("/");
                newCookie.setMaxAge(3600);//one hour
                respWrapper.addCookie(newCookie);
                respWrapper.setReplayCookies(false);//don't send back any cookies we were passed in.
                String redirectURL = getRedirectForUser(principal);
                //respWrapper.sendRedirect(redirectURL);
                respWrapper.writeResponse(HttpResponseStatus.OK, redirectURL, MimeType.TEXT_PLAIN);
                return;
            }

        }

        sendBadRequestResponse(respWrapper, "Bad Request.");
    }

    private String getRedirectForUser(Principal p){
        //Going to do a little hack for now.
        if(Principal.READ_USER == p){
            return WellKnownURLs.READ_USER_ENTRY;
        }else if(Principal.READ_WRITE_USER == p){
            return WellKnownURLs.READ_WRITE_USER_ENTRY;
        }else{
            return WellKnownURLs.LOGIN_PAGE;
        }

    }

    private Tuple<String, String> extractCredentials(JSONArray array) {
        String user = null;
        String password = null;

        for(Object o : array){
            if(o instanceof JSONObject){
                JSONObject obj = (JSONObject)o;
                Object name = obj.get("name");
                Object value = obj.get("value");
                if(null == name || null == value){
                    continue;
                }

                if("user".equals(name.toString())){
                    user = value.toString();
                }else if("pass".equals(name.toString())){
                    password = value.toString();
                }else{
                    LOG.warn("Was passed a JSONObject I didn't understand:{},{}", name, value);
                }
            }
        }

        return new Tuple<String, String>(user, password);
    }


}
