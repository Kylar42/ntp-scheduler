package edu.wvup.cs460.http.roap;

import edu.wvup.cs460.NTPAppServer;
import edu.wvup.cs460.dataaccess.DataStorage;
import edu.wvup.cs460.datamodel.CourseInstance;
import edu.wvup.cs460.datamodel.CourseMetadata;
import edu.wvup.cs460.http.MimeType;
import edu.wvup.cs460.http.ParsedURL;
import edu.wvup.cs460.http.ResponseWrapper;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Tom Byrne(tom.byrne@apple.com)
 * "Code early, Code often."
 */
public class ClassListHandler implements ContentHandlerFactory.ContentHandler {

    Logger LOG = LoggerFactory.getLogger(ClassListHandler.class);


    @Override
    public void handleContent(ResponseWrapper responseWrapper, Object content, ParsedURL url) {
        //Are we searching, or updating?
        switch (url.getActionType()) {

            case search:
                doClassSearch(responseWrapper, content);
                break;
            case update:
            case unknown:
                ContentHandlerFactory.UNKNOWN_HANDLER.handleContent(responseWrapper, content, url);
        }
    }

    private void doClassSearch(ResponseWrapper respWrapper, Object parsedJson) {
        Map<String, String> values = new HashMap<String, String>();

        if (parsedJson instanceof JSONArray) {
            JSONArray array = (JSONArray) parsedJson;
            String classToSearch = null;
            for (Object obj : array) {
                if (obj instanceof JSONObject) {
                    JSONObject jsonObj = (JSONObject) obj;
                    final Object name = jsonObj.get("name");
                    final Object value = jsonObj.get("value");
                    values.put(name.toString(), value.toString());
                }
            }


            //OK, classname has been found.
            final DataStorage storageService = NTPAppServer.getInstance().getStorageService();
            final List<CourseInstance> courseInstanceList = storageService.courseInstanceStorage().retrieveList(values);
            String form = constructListFromCourseInstances(courseInstanceList);

            respWrapper.writeResponse(HttpResponseStatus.OK, form, MimeType.TEXT_HTML);

        }
    }

    private String constructListFromCourseInstances(List<CourseInstance> instances){
        StringBuilder sb = new StringBuilder();

        return sb.toString();
    }

}
