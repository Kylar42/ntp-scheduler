package edu.wvup.cs460.http;

import com.Ostermiller.util.CircularByteBuffer;
import org.jboss.netty.handler.codec.http.HttpChunk;
import org.jboss.netty.handler.codec.http.HttpRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * User: Tom Byrne(kylar42@gmail.com)
 * "Code early, Code often."
 * This is a wrapper for the incoming HTTPRequest.
 * Originally I wrote a chunked stream handler to accumulate the chunks iteratively
 * but then went with a chunk handler in the channel instead.
 */
public class RequestWrapper {

    private final HttpRequest _request;
    private final boolean     _chunked;


    public RequestWrapper(HttpRequest request){
        _request = request;
        _chunked = _request.isChunked();
    }



    public HttpRequest getRequest(){
        return _request;
    }

}
