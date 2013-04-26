package edu.wvup.cs460.action;

import edu.wvup.cs460.http.MimeType;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;

/**
 * User: Tom Byrne(kylar42@gmail.com)
 * "Code early, Code often."
 * This is a contextual holder for the data required to send a response from an HTTP Method. It has a response code,
 * A Mime type, and content.
 */
public class MethodResponse {

    public static MethodResponse NOT_FOUND = new MethodResponse(HttpResponseStatus.NOT_FOUND);
    public static MethodResponse BAD_REQUEST = new MethodResponse(HttpResponseStatus.BAD_REQUEST);

    private final HttpResponseStatus  _responseStatus;
    private final String              _responseData;
    private final MimeType            _responseType;

    private MethodResponse(HttpResponseStatus status){
        _responseStatus = status;
        _responseData = null;
        _responseType = MimeType.UNKNOWN;
    }

    public MethodResponse(HttpResponseStatus status, String responseData, MimeType responseType){
        _responseStatus = status;
        _responseData = responseData;
        _responseType = responseType;
    }

    public HttpResponseStatus   getResponseStatus() { return _responseStatus;   }
    public String               getResponseData()   { return _responseData;     }
    public MimeType             getResponseType()   { return _responseType;     }

}
