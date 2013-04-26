package edu.wvup.cs460.http.roap;

import edu.wvup.cs460.NTPAppServer;
import edu.wvup.cs460.dataaccess.DataStorage;
import edu.wvup.cs460.datamodel.CourseMetadata;
import edu.wvup.cs460.http.MethodContext;
import edu.wvup.cs460.http.MimeType;
import edu.wvup.cs460.http.ParsedURL;
import edu.wvup.cs460.http.ResponseWrapper;
import edu.wvup.cs460.http.authentication.Principal;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Tom Byrne(kylar42@gmail.com)
 * "Code early, Code often."
 * Content handler to fetch and update CourseMetadata from the UI.
 */
public class ClassMetaHandler implements ContentHandlerFactory.ContentHandler {

    Logger LOG = LoggerFactory.getLogger(ClassMetaHandler.class);


    @Override
    public void handleContent(ResponseWrapper responseWrapper, Object content, MethodContext context) {
        //Are we searching, or updating?
        switch (context.getParsedURL().getActionType()) {

            case search:
                doClassSearch(responseWrapper, content, context);
                break;
            case update:
                doClassMetaUpdate(responseWrapper, content, context);
                break;
            case unknown:
                ContentHandlerFactory.UNKNOWN_HANDLER.handleContent(responseWrapper, content, context);
        }
    }

    /**
     * Convenience method to flip set a boolean in a CourseMetadata object.
     * TODO: See about moving this into the CourseMetadata object.
     * @param courseMeta
     * @param field
     * @param newValue
     */
    private void setCourseMeta(CourseMetadata courseMeta, String field, String newValue) {
        //assuming that newValue is going to be "on"

        boolean newVal = "on".equalsIgnoreCase(newValue);
        if (!newVal) {
            //bad. what do we do?
            LOG.error("We received a value we didn't understand!");
        }

        if ("isMath".equals(field)) {
            courseMeta.setIsMath(newVal);
        } else if ("isHumanities".equalsIgnoreCase(field)) {
            courseMeta.setIsHumanities(newVal);
        } else if ("isSocSci".equalsIgnoreCase(field)) {
            courseMeta.setIsSocialScience(newVal);
        } else if ("isNatSci".equalsIgnoreCase(field)) {
            courseMeta.setIsNaturalScience(newVal);
        } else if ("isComm".equalsIgnoreCase(field)) {
            courseMeta.setIsCommunications(newVal);
        } else if ("isCompLit".equalsIgnoreCase(field)) {
            courseMeta.setIsComputerLit(newVal);
        } else {
            LOG.error("We recieved a value we didn't understand!");
        }
    }

    /**
     * Convenience method to write auth exception.
     * @param responseWrapper
     */
    private void writeAuthenticationError(ResponseWrapper responseWrapper){
        responseWrapper.writeResponse(HttpResponseStatus.OK, "<p>You do not have the proper credentials to perform this operation.<p>", MimeType.TEXT_HTML);
    }

    /**
     * This gets called whem the client submits the form to update Metadata objects. We need to organize them and update the DB.
     * @param respWrapper
     * @param parsedJson
     * @param context
     */
    private void doClassMetaUpdate(ResponseWrapper respWrapper, Object parsedJson, MethodContext context) {
        //here we're checking to see if this is a valid principal.

        if(Principal.READ_WRITE_USER != context.getPrincipal()){
            writeAuthenticationError(respWrapper);
        }


        //This will be a map of maps.
        Map<String, Map<String, CourseMetadata>> courseMetaMap = new HashMap<String, Map<String, CourseMetadata>>();
        if (parsedJson instanceof JSONArray) {
            //
            JSONArray array = (JSONArray) parsedJson;
            for (Object obj : array) {
                if (obj instanceof JSONObject) {
                    JSONObject jsonObj = (JSONObject) obj;
                    final Object name = jsonObj.get("name");
                    final Object value = jsonObj.get("value");
                    if (null != name) {
                        final String[] split = name.toString().split("\\.");
                        if (split.length != 3) {
                            LOG.error("Error in meta update trying to parse class. " + name);
                            continue;
                        }
                        CourseMetadata curCourse = courseForClass(split[0], split[1], courseMetaMap);
                        setCourseMeta(curCourse, split[2], value.toString());
                    }
                    //System.out.println("name" + name + " Value:" + value);
                }
            }

            //Iterate through the set of subjects, and update the database with our CourseMetadata objects.
            for (String subject : courseMetaMap.keySet()) {
                final Map<String, CourseMetadata> subjectMap = courseMetaMap.get(subject);
                for (CourseMetadata courseMeta : subjectMap.values()) {
                    NTPAppServer.getInstance().getStorageService().courseMetadataStorage().update(courseMeta);
                }
            }

            //write a simple response back so the client knows we were successful.
            respWrapper.writeResponse(HttpResponseStatus.OK, "<p>Updated<p>", MimeType.TEXT_HTML);

        }
    }

    /**
     * Retrieve a CourseMetadata object from our map of maps, given a subject and class number.
     * @param subject
     * @param classNumber
     * @param courseMetaMap
     * @return
     */
    private CourseMetadata courseForClass(String subject, String classNumber, Map<String, Map<String, CourseMetadata>> courseMetaMap) {
        CourseMetadata toReturn;
        //see if the class exists first.
        Map<String, CourseMetadata> courseSubjectMetadataMap = courseMetaMap.get(subject);

        if (null == courseSubjectMetadataMap) {
            //create a new one, and a new CourseMeta as well.
            courseSubjectMetadataMap = new HashMap<String, CourseMetadata>();
            toReturn = new CourseMetadata(subject, classNumber);
            courseSubjectMetadataMap.put(classNumber, toReturn);
            courseMetaMap.put(subject, courseSubjectMetadataMap);
        } else {
            //have a map. let's see if the subject is there.
            toReturn = courseSubjectMetadataMap.get(classNumber);
            if (null == toReturn) {
                toReturn = new CourseMetadata(subject, classNumber);
                courseSubjectMetadataMap.put(classNumber, toReturn);
            }
        }
        return toReturn;
    }

    /**
     * Called when the UI attempts to get a list of CourseMetadata objects to update. Search the DB, then create a dynamic form to return.
     * @param respWrapper
     * @param parsedJson
     * @param context
     */
    private void doClassSearch(ResponseWrapper respWrapper, Object parsedJson, MethodContext context) {

        //here we're checking to see if this is a valid principal.

        if(Principal.READ_WRITE_USER != context.getPrincipal()){
            writeAuthenticationError(respWrapper);
        }

        if (parsedJson instanceof JSONArray) {
            JSONArray array = (JSONArray) parsedJson;
            String classToSearch = null;
            for (Object obj : array) {
                if (obj instanceof JSONObject) {
                    JSONObject jsonObj = (JSONObject) obj;
                    final Object name = jsonObj.get("name");
                    final Object value = jsonObj.get("value");
                    if ("classname".equals(name)) {
                        classToSearch = value.toString();
                    }
                    //System.out.println("name" + name + " Value:" + value);
                }
            }

            //OK, classname has been found.
            final DataStorage storageService = NTPAppServer.getInstance().getStorageService();
            final List<CourseMetadata> coursesWithSubjectSubstring = storageService.courseMetadataStorage().retrieveList(classToSearch);
            String form = constructDynamicFormFromCourseMeta(coursesWithSubjectSubstring);

            respWrapper.writeResponse(HttpResponseStatus.OK, form, MimeType.TEXT_HTML);

        }
    }

    /**
     * Create a dynamic HTML form that embeds the coursemeta information, so that when it's re-submitted, we can easily
     * parse out the info.
     * @param courses
     * @return
     */
    private String constructDynamicFormFromCourseMeta(List<CourseMetadata> courses) {
        Collections.sort(courses);
        StringBuilder sb = new StringBuilder();
        sb.append("\t\t<form method=\"post\" action=\"\" id=\"class-update\">\n");
        sb.append("<table id=\"gen-table\">");
        sb.append("<thead>");
        sb.append("<th scope=\"col\">Name</th>");
        sb.append("<th scope=\"col\">Math</th>");
        sb.append("<th scope=\"col\">Humanities</th>");
        sb.append("<th scope=\"col\">Social Science</th>");
        sb.append("<th scope=\"col\">Natural Science</th>");
        sb.append("<th scope=\"col\">Communications</th>");
        sb.append("<th scope=\"col\">Comp Lit</th>");
        sb.append("</thead><tbody>");
        for (CourseMetadata course : courses) {
            sb.append("<tr>");

            String idPrefix = course.getSubject() + "." + course.getCourseNumber() + ".";
            //<label>Class Name</label> <input type="text" name="math101.classname" value="Math 101" disabled="true"/>
            sb.append("<td><input type=\"text\" name=\"" + idPrefix + "classname\" value=\"" +
                              course.getSubject() + course.getCourseNumber() + "\" readonly /> </td>");
            //			<input type="checkbox" name="math101.isMath" /><label>is Math</label>
            boolean isMath = course.isMath();
            sb.append("\t\t\t<td><input type=\"checkbox\" name=\"" + idPrefix + "isMath\" " + (isMath ? "checked=\"checked\"" : "") + " /></td>\n");

            boolean isHumanities = course.isHumanities();
            sb.append("\t\t\t<td><input type=\"checkbox\" name=\"" + idPrefix + "isHumanities\" " + (isHumanities ? "checked=\"checked\"" : "") + " /></td>\n");

            boolean isSocSci = course.isSocialScience();
            sb.append("\t\t\t<td><input type=\"checkbox\" name=\"" + idPrefix + "isSocSci\" " + (isSocSci ? "checked=\"checked\"" : "") + " /></td>\n");

            boolean isNatsci = course.isNaturalScience();
            sb.append("\t\t\t<td><input type=\"checkbox\" name=\"" + idPrefix + "isNatSci\" " + (isNatsci ? "checked=\"checked\"" : "") + " /></td>\n");

            boolean isComm = course.isCommunications();
            sb.append("\t\t\t<td><input type=\"checkbox\" name=\"" + idPrefix + "isComm\" " + (isComm ? "checked=\"checked\"" : "") + " /></td>\n");

            boolean isCompLit = course.isComputerLit();
            sb.append("\t\t\t<td><input type=\"checkbox\" name=\"" + idPrefix + "isCompLit\" " + (isCompLit ? "checked=\"checked\"" : "") + " /></td>\n");
            sb.append("</tr>");

        }

        sb.append("</tbody></table>");

        sb.append("\t\t\t<input type=\"button\" value=\"Update\" onclick=\"classUpdate(this.form)\" />\n</form>");

        return sb.toString();
    }

}
