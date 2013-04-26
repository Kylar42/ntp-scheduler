package edu.wvup.cs460.http;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpChunk;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.jboss.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * This is where the Netty Framework calls us.
 * We'll create and dispatch according to a set of criteria, primarily the authorization status and
 * the type of HTTP Method.
 */

public class DefaultHttpServerHandler extends SimpleChannelUpstreamHandler {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultHttpServerHandler.class);


    //========================================================================

    private AbstractHttpMethod _method;
    private RequestWrapper _reqWrapper;
    private ResponseWrapper _respWrapper;


    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        if (e.getMessage() instanceof HttpRequest) {
            //create new wrapper and dispatch  it to expected method.
            HttpRequest request =  (HttpRequest) e.getMessage();
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