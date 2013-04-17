package edu.wvup.cs460.http;

import edu.wvup.cs460.NTPAppServer;
import edu.wvup.cs460.action.ChainStatus;
import net.minidev.json.JSONObject;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: Tom Byrne(kylar42@gmail.com)
 * "Code early, Code often."
 */
public class UnauthorizedGetMethod extends GetMethod{

    private static Logger LOG = LoggerFactory.getLogger(UnauthorizedGetMethod.class);


    public UnauthorizedGetMethod(MethodContext context) {
        super(context);
    }

    @Override
    protected final ChainStatus authenticate(ResponseWrapper responseWrapper) {
        //here we'll check that the requested URI is inside the "unauthorized" area, where we can read without any junk.


        final ParsedURL parsedURL = context().getParsedURL();
        LOG.debug(parsedURL.toString());

        //if we're in unauthorized area, let it go.
        if(isInUnsecureArea(parsedURL)){
            return ChainStatus.PASS_CONTINUE;
        }else{
            //write redirect
            responseWrapper.sendRedirect(WellKnownURLs.LOGIN_PAGE);
            return ChainStatus.FAIL_DO_NOT_CONTINUE;
        }

    }

    @Override
    public void handleRequest(RequestWrapper reqWrapper, ResponseWrapper respWrapper) {
        ParsedURL parsedURL = context().getParsedURL();
        if(ParsedURL.OBJECT_TYPE.info == parsedURL.getObjectType() &&
                ParsedURL.ACTION_TYPE.list == parsedURL.getActionType()){
            JSONObject infoObject = createInfoObject();
            respWrapper.writeResponse(HttpResponseStatus.OK, infoObject.toJSONString(), MimeType.APP_JSON);
            return;
        }
        //Special bit here to deal with requests from
        super.handleRequest(reqWrapper, respWrapper);
    }

    private JSONObject createInfoObject(){
        JSONObject toReturn = new JSONObject();
        String version = NTPAppServer.getInstance().getAppProperties().getProperty("app.version", "-1");
        toReturn.put("version", version);
        return toReturn;
    }
    private boolean isInUnsecureArea(ParsedURL parsedURL){
        return (parsedURL.getObjectType().equals(ParsedURL.OBJECT_TYPE.unauthorized));//anything in this tree will be OK.
    }

}
