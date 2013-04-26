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
 * This is an extention of our HTTP Get method, but allows very specific
 * requests to be served to an unauthorized principal. This is basically our Login Page and our info request/heartbeat
 *
 */
public class UnauthorizedGetMethod extends GetMethod{

    private static Logger LOG = LoggerFactory.getLogger(UnauthorizedGetMethod.class);


    public UnauthorizedGetMethod(MethodContext context) {
        super(context);
    }

    /**
     * This is the real difference in the GET method. We're going to check to see if the request is for something in the
     * "unauthorized" area, and if so, we're going to tell it to continue.
     * If not, then let's redirect back to the login page.
     * @param responseWrapper
     * @return
     */
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

    /**
     * We allow this to come through without any authorization so that the Monitor app can get info without needing credentials.
     * It can also be used as a heartbeat.
     * @return
     */
    private JSONObject createInfoObject(){
        JSONObject toReturn = new JSONObject();
        String version = NTPAppServer.getInstance().getAppProperties().getProperty("app.version", "-1");
        toReturn.put("app.version", version);
        toReturn.put("application", "NTPAppServer");
        long upTime = System.currentTimeMillis() - NTPAppServer.getInstance().getAppProperties().getPropertyAsLong("start.time", 0);

        toReturn.put("uptime", upTime);
        return toReturn;
    }
    private boolean isInUnsecureArea(ParsedURL parsedURL){
        return (parsedURL.getObjectType().equals(ParsedURL.OBJECT_TYPE.unauthorized));//anything in this tree will be OK.
    }

}
