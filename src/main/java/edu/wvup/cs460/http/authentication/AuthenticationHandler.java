package edu.wvup.cs460.http.authentication;

import edu.wvup.cs460.http.HeaderNames;
import org.jboss.netty.handler.codec.http.Cookie;
import org.jboss.netty.handler.codec.http.CookieDecoder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: Tom Byrne(kylar42@gmail.com)
 * "Code early, Code often."
 */
public class AuthenticationHandler {

    private CookieHandler _cookieHandler = new CookieHandler();//only one instance, since AuthenticationHandler should only exist in one place.


    public void removeAllCookiesFromCache(List<Map.Entry<String, String>> headers){
        if(null == headers){
            return;//my work here is done.
        }
        CookieDecoder decoder = new CookieDecoder();
        for(Map.Entry<String, String> entry : headers){
            if(HeaderNames.Cookie.equals(entry.getKey())){
                final Set<Cookie> decodedCookieSet = decoder.decode(entry.getValue());
                for(Cookie c : decodedCookieSet){
                    if("NTP".equals(c.getName())){
                        _cookieHandler.dropCookieFromCache(c.getValue());
                    }
                }
            }
        }
    }

    public Principal authenticateFromCookie(List<Map.Entry<String, String>> headers) {
        //look for a cookie.
        //TODO: Handle multiple cookies.
        final String cookieHeader = firstInList(HeaderNames.Cookie, headers);
        String cookie = null;
        if (null == cookieHeader || cookieHeader.length() < 1) {
            return Principal.UNAUTHORIZED;
        }
        try {
            CookieDecoder cookieDecoder = new CookieDecoder();
            final Set<Cookie> decoded = cookieDecoder.decode(cookieHeader);

            for (Cookie c : decoded) {
                //look for our cookie!
                if ("NTP".equals(c.getName())) {
                    cookie = c.getValue();
                    break;
                }
            }
        } catch (Throwable ignore) {
            //if something goes wrong decoding cookies, I don't care, I'm just going to return UNAUTHORIZED anyway.
        }

        if (null != cookie) {
            AuthenticationContext goodContext = null;
            try {
                goodContext = _cookieHandler.fetchCookie(cookie);
            } catch (AuthenticationException e) {
                return Principal.UNAUTHORIZED;
            }
            //it was a valid cookie.
            return authenticate(goodContext);
        }

        //if we get here, we should return the UNAUTHORIZED, in which case we'll redirect to the auth page.
        return Principal.UNAUTHORIZED;
    }


    /**
     * This is basically a placeholder method. If we're going to call out to any other service to determine
     * what we can do, this is the place to do it.
     *
     * @param context
     * @return
     */
    public final Principal authenticate(AuthenticationContext context) {
        if (context.getUser().equalsIgnoreCase("guest")) {
            return Principal.READ_USER;
        } else if (context.getUser().equalsIgnoreCase("admin")) {
            return Principal.READ_WRITE_USER;
        }

        return Principal.UNAUTHORIZED;
    }

    private Collection<String> headersFromList(HeaderNames headerName, List<Map.Entry<String, String>> headers) {
        ArrayList<String> toReturn = new ArrayList<String>();
        for (Map.Entry<String, String> header : headers) {
            if (headerName.getFormattedValue().equals(header.getKey())) {
                toReturn.add(header.getValue());
            }
        }

        return toReturn;
    }

    private String firstInList(HeaderNames headerName, List<Map.Entry<String, String>> headers) {
        for (Map.Entry<String, String> header : headers) {
            if (headerName.getFormattedValue().equals(header.getKey())) {
                return header.getValue();
            }
        }
        return null;
    }

    public CookieHandler cookieHandler() {
        return _cookieHandler;
    }
}
