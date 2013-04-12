package edu.wvup.cs460.http;

import static org.jboss.netty.handler.codec.http.HttpHeaders.*;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.*;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.*;
import static org.jboss.netty.handler.codec.http.HttpVersion.*;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.Cookie;
import org.jboss.netty.handler.codec.http.CookieDecoder;
import org.jboss.netty.handler.codec.http.CookieEncoder;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpChunk;
import org.jboss.netty.handler.codec.http.HttpChunkTrailer;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;
import org.jboss.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultHttpServerHandler extends SimpleChannelUpstreamHandler {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultHttpServerHandler.class);


    private HttpRequest request;
    /**
     * Buffer that stores the response content
     */
    private final StringBuilder buf = new StringBuilder();

    private AbstractHttpMethod _method;
    private RequestWrapper _reqWrapper;
    private ResponseWrapper _respWrapper;


    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        if (e.getMessage() instanceof HttpRequest) {
            //create new wrapper and dispatch  it to expected method.
            HttpRequest request = this.request = (HttpRequest) e.getMessage();
            _reqWrapper = new RequestWrapper(request);
            _respWrapper = new ResponseWrapper(ctx, request);
            _method = MethodFactory.getInstance().methodForRequest(request);
            try {
                _method.handleRequest(_reqWrapper, _respWrapper);
            } catch (Throwable e1) {
                _respWrapper.writeResponse(HttpResponseStatus.INTERNAL_SERVER_ERROR, e1.getMessage(), MimeType.TEXT_PLAIN);
            }
        } else if (e.getMessage() instanceof HttpChunk) {
            LOG.error("Was passed in an HTTP Chunk!");//I have the chunk aggregator in my pipeline, this should not happen.
        }
    }

    /**
     * if this needs to be used, a piece of code like so:
     *
     * if (is100ContinueExpected(request)) {
     *   send100Continue(e);
     * }
     *
     * should suffice.
     *
     * @param e
     */
    private static void send100Continue(MessageEvent e) {
        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, CONTINUE);
        e.getChannel().write(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
            throws Exception {
        LOG.error("Unknown error occurred!", e.getCause());
        e.getChannel().close();
    }
}