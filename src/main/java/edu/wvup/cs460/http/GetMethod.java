package edu.wvup.cs460.http;

import edu.wvup.cs460.NTPAppServer;
import edu.wvup.cs460.action.ChainStatus;
import edu.wvup.cs460.configuration.ConfigurationException;
import edu.wvup.cs460.http.roap.ContentHandlerFactory;
import edu.wvup.cs460.util.MimeUtils;
import edu.wvup.cs460.util.StringUtils;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * User: Tom Byrne(kylar42@gmail.com)
 * "Code early, Code often."
 */
public class GetMethod extends AbstractHttpMethod {

    private static Logger LOG = LoggerFactory.getLogger(GetMethod.class);

    private static final File STATIC_CONTENT_ROOT = initStaticContentRoot();

    private static final File initStaticContentRoot() {
        //let's see if there's something in the properties.
        String path = NTPAppServer.getInstance().getAppProperties().getProperty("static.content.root", null);
        //check to see if it exists.
        if (null != path) {
            File f = new File(path);
            if (f.exists() && f.isDirectory()) {
                return f;
            }
        }
        //nope. let's see if there is a "static_content" directory in our running directory.

        File f = new File("static_content");

        if (f.exists() && f.isDirectory()) {
            return f;
        }

        //That didn't work either. Let's bail with an error.

        throw new ConfigurationException("Unable to create configuration properly - static.content.root is not properly defined.");

    }

    public GetMethod(MethodContext context) {
        super(context);
    }


    @Override
    public void handleRequest(RequestWrapper reqWrapper, ResponseWrapper respWrapper) {

        //check one specific issue.

        final ContentHandlerFactory.ContentHandler contentHandler = ContentHandlerFactory.contentHandlerforURL(context().getParsedURL(), HttpMethod.GET);
        if(null != contentHandler && ContentHandlerFactory.UNKNOWN_HANDLER != contentHandler){
            contentHandler.handleContent(respWrapper, null, context());
            return;
        }


        final ChainStatus authenticate = super.authenticate(respWrapper);
        if (!authenticate.shouldContinue()) {
            return;
        }

        String url = context().getUri();
        //open a file in our root.

        final ChainStatus chainStatus = checkForRelativeSegments(url);
        if (!chainStatus.shouldContinue()) {
            return;
        }


        File inputFile = new File(STATIC_CONTENT_ROOT, url);
        if (inputFile.isDirectory()) {
            //redirect to class-update.html.
            StringBuilder newUrl = new StringBuilder(url);
            if (newUrl.charAt(newUrl.length() - 1) != '/') {
                newUrl.append('/');
            }
            newUrl.append("index.html");
            //respWrapper.sendRedirect(newUrl.toString());
            //return;
            inputFile = new File(STATIC_CONTENT_ROOT, newUrl.toString());
        }

        if (!inputFile.exists()) {
            respWrapper.writeResponse(HttpResponseStatus.NOT_FOUND, "URL does not exist.", null);
            return;
        }

        try {
            FileInputStream fis = new FileInputStream(inputFile);
            MimeType mimeType = MimeUtils.contentTypeForFile(inputFile);

            byte[] output = null;
            try {
                output = StringUtils.readBytes(fis, 8192);
            } catch (IOException e) {
                LOG.error("Error occurred", e);
            }
            respWrapper.writeResponse(HttpResponseStatus.OK, output, mimeType);
            return;
        } catch (FileNotFoundException e) {
            LOG.error("Error occurred", e);
        }
    }


}
