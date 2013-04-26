package edu.wvup.cs460.http;

import edu.wvup.cs460.action.MethodResponse;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.http.Cookie;
import org.jboss.netty.handler.codec.http.CookieDecoder;
import org.jboss.netty.handler.codec.http.CookieEncoder;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.util.CharsetUtil;

import javax.management.monitor.StringMonitorMBean;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.COOKIE;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.LOCATION;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.SET_COOKIE;
import static org.jboss.netty.handler.codec.http.HttpHeaders.addHeader;
import static org.jboss.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.OK;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * User: Tom Byrne(kylar42@gmail.com)
 * "Code early, Code often."
 * Wrapper for the response, including the Channel Context to write to.
 */
public class ResponseWrapper {

    private final ChannelHandlerContext _channelContext;
    private final HttpRequest _request;

    private final Set<Cookie> _newCookies = new HashSet<Cookie>();
    private boolean _replayCookies = true;

    public ResponseWrapper(ChannelHandlerContext chc, HttpRequest request) {
        _channelContext = chc;
        _request = request;
    }

    public void setReplayCookies(boolean shouldReplayCookies){
        _replayCookies = shouldReplayCookies;
    }

    public void addCookie(Cookie cookie) {
        _newCookies.add(cookie);
    }

    public void sendRedirect(final String url) {
        final Map<String, String> headers = new HashMap<String, String>() {{
            put(LOCATION, url);
        }};
        writeResponse(HttpResponseStatus.MOVED_PERMANENTLY, "File Moved.", null, headers);
    }

    public void writeResponse(MethodResponse response) {
        writeResponse(response.getResponseStatus(), response.getResponseData(), response.getResponseType());
    }

    public void writeResponse(HttpResponseStatus status, String content, MimeType contentType) {
        writeResponse(status, content, contentType, Collections.EMPTY_MAP);
    }

    public void writeResponse(HttpResponseStatus status, byte[] content, MimeType contentType) {
        writeResponse(status, content, contentType, Collections.EMPTY_MAP);
    }

    public void writeResponse(HttpResponseStatus status, String content, MimeType contentType, Map<String, String> headers) {
        writeResponse(status, content.getBytes(), contentType, headers);
    }

    /**
     * Main call to write a response. It sets the content type and length,
     * writes the content, sets the cookies.
     *
     * @param status
     * @param content
     * @param contentType
     * @param headers
     */
    public void writeResponse(HttpResponseStatus status, byte[] content, MimeType contentType, Map<String, String> headers) {
        // Decide whether to close the connection or not.
        boolean keepAlive = isKeepAlive(_request);

        // Build the response object.
        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, status);
        //Set the content type header
        if (null != contentType && MimeType.UNKNOWN != contentType) {
            response.setHeader(CONTENT_TYPE, contentType.formattedString());

        }
        //set hte content length
        if(null != content){
            //response.setHeader(CONTENT_LENGTH, response.getContent().readableBytes());
            response.setHeader(CONTENT_LENGTH, content.length);
        }

        //set the rest of the headers passed in.
        for (String headerName : headers.keySet()) {
            response.setHeader(headerName, headers.get(headerName));
        }

        //set connection keepalive header.
        if (keepAlive) {
            // Add keep alive header as per:
            // - http://www.w3.org/Protocols/HTTP/1.1/draft-ietf-http-v11-spec-01.html#Connection
            response.setHeader(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        }

        // Encode the cookies sent in, if we're supposed to..
        if (_replayCookies) {
            String cookieString = _request.getHeader(COOKIE);
            if (cookieString != null) {
                CookieDecoder cookieDecoder = new CookieDecoder();
                Set<Cookie> cookies = cookieDecoder.decode(cookieString);
                if (!cookies.isEmpty()) {
                    // Reset the cookies if necessary.
                    CookieEncoder cookieEncoder = new CookieEncoder(true);
                    for (Cookie cookie : cookies) {
                        cookieEncoder.addCookie(cookie);
                        response.addHeader(SET_COOKIE, cookieEncoder.encode());
                    }
                }
            }
        }
        //Add new cookies.
        if(!_newCookies.isEmpty()){
            CookieEncoder cookieEncoder = new CookieEncoder(true);
            for (Cookie cookie : _newCookies) {
                cookieEncoder.addCookie(cookie);
                response.addHeader(SET_COOKIE, cookieEncoder.encode());
            }
        }


        //set response.
        response.setContent(ChannelBuffers.copiedBuffer(content));

        // Write the response.
        ChannelFuture future = _channelContext.getChannel().write(response);

        // Close the non-keep-alive connection after the write operation is done.
        if (!keepAlive) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }

    /**
     * Commented out on purpose. We're not allowing basic auth right now.
     * @param realm
     */
    public void writeUnauthorizedResponse(final String realm) {
        //redirect to login page.
        /*
        Map<String, String> headers = new HashMap<String, String>(){{
            put("WWW-Authenticate", " Basic realm=\""+realm+"\"");
        }
        };
        writeResponse(HttpResponseStatus.UNAUTHORIZED, "", MimeType.TEXT_PLAIN, headers);
        */
    }

}
