package edu.wvup.cs460.http;

import edu.wvup.cs460.action.ChainStatus;
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

    public PostMethod(MethodContext context){
        super(context);
    }

    @Override
    public void handleRequest(RequestWrapper reqWrapper, ResponseWrapper respWrapper) {

        final ChainStatus authenticate = super.authenticate(respWrapper);
        if(!authenticate.shouldContinue()) {
            return;
        }

        String url = context().getUri();
        //TODO: URL based dispatcher for REST.
        String contentType = getHeaderValue(HeaderNames.ContentType, null);
        Object parsedJson = null;
        MimeType incomingType = MimeType.typeFromStringWithoutCharset(contentType);
        if(MimeType.APP_JSON == incomingType){
            //parse JSON
            final String jsonString = reqWrapper.getRequest().getContent().toString(CharsetUtil.UTF_8);
            JSONParser parser = new JSONParser(JSONParser.MODE_PERMISSIVE);
            try {
                parsedJson = parser.parse(jsonString);
            } catch (ParseException e) {
                LOG.error("Unable to parse JSON body from request.", e);
            }
        }

        final ContentHandlerFactory.ContentHandler contentHandler = ContentHandlerFactory.contentHandlerforURL(context().getParsedURL(), HttpMethod.POST);

        contentHandler.handleContent(respWrapper, parsedJson, context());



    }






}
