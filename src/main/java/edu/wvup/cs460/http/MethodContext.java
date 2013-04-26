package edu.wvup.cs460.http;

import edu.wvup.cs460.http.authentication.Principal;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Tom Byrne(kylar42@gmail.com)
 * "Code early, Code often."
 * Context that a Method needs to run. Contains the URI, ParsedURL, Headers, Principal.
 */
public class MethodContext {

    private final Map<String, List<String>> _queryStringParams;
    private final String                    _uri;
    private final Map<String, String>       _headers;
    private final Principal                 _principal;
    private final ParsedURL                 _parsedURL;



    public MethodContext(HttpRequest req, Principal principal){
        _principal = principal;
        _uri = req.getUri();
        _parsedURL = ParsedURL.createParsedURL(_uri);

        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(req.getUri());
        _queryStringParams = queryStringDecoder.getParameters();
        _headers = new HashMap<String, String>();
        for(Map.Entry<String, String> header : req.getHeaders()){
            _headers.put(header.getKey(), header.getValue());
        }
    }

    public String getUri() {
        return _uri;
    }

    public List<String> getQueryStringParam(String key){
        return _queryStringParams.get(key);
    }

    public Map<String, String> getHeaders(){
        return _headers;
    }

    public Principal getPrincipal(){
        return _principal;
    }

    public ParsedURL getParsedURL(){
        return _parsedURL;
    }
}
