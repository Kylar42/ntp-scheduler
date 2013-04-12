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
 */
public class RequestWrapper {

    private final HttpRequest _request;
    private final boolean     _chunked;
    //private final BlockingInputStream _stream;


    public RequestWrapper(HttpRequest request){
        _request = request;
        _chunked = _request.isChunked();
        //_stream = new BlockingInputStream();
    }

    /*public void chunkedBytesReceived(HttpChunk chunk){
        if(chunk.getContent().readable()){
            chunk.getContent();
        }

    } */

    public HttpRequest getRequest(){
        return _request;
    }

    /*private class BlockingInputStream extends InputStream{
        CircularByteBuffer byteBuffer = new CircularByteBuffer(4096, true);

        @Override
        public int read() throws IOException {
            if(!_chunked){
                return _request.getContent().readInt();
            }else{
                return byteBuffer.getInputStream().read();
            }

        }
    } */
}
