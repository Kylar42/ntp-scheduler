package edu.wvup.cs460.http;

import edu.wvup.cs460.action.ChainStatus;
import edu.wvup.cs460.action.MethodResponse;
import edu.wvup.cs460.dataaccess.DataStorage;
import edu.wvup.cs460.datamodel.CourseMetadata;
import edu.wvup.cs460.http.roap.ContentHandlerFactory;
import edu.wvup.cs460.util.MimeUtils;
import edu.wvup.cs460.util.StringUtils;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Tom Byrne(kylar42@gmail.com)
 * "Code early, Code often."
 */
public class PostMethod extends AbstractHttpMethod {

    Logger LOG = LoggerFactory.getLogger(PostMethod.class);

    public PostMethod(MethodContext context) {
        super(context);
    }


    protected boolean isRequestJSon(RequestWrapper requestWrapper) {
        String contentType = getHeaderValue(HeaderNames.ContentType, null);
        MimeType incomingType = MimeType.typeFromStringWithoutCharset(contentType);
        return MimeType.APP_JSON == incomingType;
    }

    protected Object parseJSonObjectFromRequest(RequestWrapper reqWrapper) {
        if (!isRequestJSon(reqWrapper)) {
            return null;
        }
        //parse JSON
        final String jsonString = reqWrapper.getRequest().getContent().toString(CharsetUtil.UTF_8);
        JSONParser parser = new JSONParser(JSONParser.MODE_PERMISSIVE);
        try {
            return parser.parse(jsonString);
        } catch (ParseException e) {
            LOG.error("Unable to parse JSON body from request.", e);
        }
        return null;
    }

    @Override
    public void handleRequest(RequestWrapper reqWrapper, ResponseWrapper respWrapper) {

        final ChainStatus authenticate = super.authenticate(respWrapper);
        if (!authenticate.shouldContinue()) {
            return;
        }

        String url = context().getUri();
        Object parsedJson = parseJSonObjectFromRequest(reqWrapper);

        if (null == parsedJson) {
            sendBadRequestResponse(respWrapper, "POST Data without JSON body.");
            return;
        }


        final ContentHandlerFactory.ContentHandler contentHandler = ContentHandlerFactory.contentHandlerforURL(context().getParsedURL(), HttpMethod.POST);

        contentHandler.handleContent(respWrapper, parsedJson, context());

    }

    protected void sendBadRequestResponse(ResponseWrapper respWrapper, String message) {
        MethodResponse toReturn = new MethodResponse(HttpResponseStatus.BAD_REQUEST, message, MimeType.TEXT_PLAIN);
        respWrapper.writeResponse(toReturn);
    }


}
