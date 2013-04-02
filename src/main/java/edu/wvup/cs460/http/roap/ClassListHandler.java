package edu.wvup.cs460.http.roap;

import edu.wvup.cs460.NTPAppServer;
import edu.wvup.cs460.dataaccess.DataStorage;
import edu.wvup.cs460.datamodel.CourseInstance;
import edu.wvup.cs460.datamodel.CourseMetadata;
import edu.wvup.cs460.http.MethodContext;
import edu.wvup.cs460.http.MimeType;
import edu.wvup.cs460.http.ParsedURL;
import edu.wvup.cs460.http.ResponseWrapper;
import edu.wvup.cs460.util.StringUtils;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Tom Byrne(kylar42@gmail.com)
 * "Code early, Code often."
 */
public class ClassListHandler implements ContentHandlerFactory.ContentHandler {

    Logger LOG = LoggerFactory.getLogger(ClassListHandler.class);


    @Override
    public void handleContent(ResponseWrapper responseWrapper, Object content, MethodContext context) {
        //Are we searching, or updating?
        switch (context.getParsedURL().getActionType()) {

            case search:
                doClassSearch(responseWrapper, content);
                break;
            case update:
            case unknown:
                ContentHandlerFactory.UNKNOWN_HANDLER.handleContent(responseWrapper, content,  context);
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

    /**
     * course_instance.crn, course_instance.type, course_instance.subject, course_instance.course_number,
     * course_instance.course_title, course_instance.credits, course_instance.days, course_instance.time,
     * course_instance.instructor, course_instance.room, course_instance.start_date, course_instance.end_date,
     * course_instance.seats_available, course_instance.term_length,  course_instance.campus FROM course_instance
     * INNER JOIN course_meta
     *
     * @param courses
     * @return
     */
    private String constructListFromCourseInstances(List<CourseInstance> courses) {
        Collections.sort(courses);
        StringBuilder sb = new StringBuilder();
        sb.append("\t\t<form method=\"post\" action=\"\" id=\"class-update\">\n");
        sb.append("<table id=\"gen-table\">");
        sb.append("<thead>");
        sb.append("<th scope=\"col\">CRN</th>");
        sb.append("<th scope=\"col\">Course</th>");
        sb.append("<th scope=\"col\" width=\"150px\">Title</th>");
        sb.append("<th scope=\"col\" width=\"40px\">Hours</th>");
        sb.append("<th scope=\"col\">Days</th>");
        sb.append("<th scope=\"col\">Time</th>");
        sb.append("<th scope=\"col\" width=\"70px\">Start Date</th>");
        sb.append("<th scope=\"col\" width=\"70px\">End Date</th>");
        sb.append("<th scope=\"col\">Term</th>");
        sb.append("<th scope=\"col\">Instructor</th>");
        sb.append("<th scope=\"col\">Seats</th>");
        sb.append("<th scope=\"col\">Campus</th>");
        sb.append("</thead><tbody>");

        for (CourseInstance instance : courses) {
            //color if seats avail < 1
            String color = instance.getSeatsAvail() < 1 ? "BGCOLOR=\"#DDDDDD\"" : "BGCOLOR=\"#FFFFFF\"";
            sb.append("<tr ").append(color).append(">");

            String startDate = getFormattedDate(instance.getStartDate());
            String endDate = getFormattedDate(instance.getEndDate());
            String term = getFormattedTermString(instance.getTermLength());

            sb.append("<td>").append(instance.getCrn()).append("</td>");
            sb.append("<td>").append(instance.getSubject()+instance.getCourseNumber()).append("</td>");
            sb.append("<td>").append(instance.getCourseTitle()).append("</td>");
            sb.append("<td>").append(instance.getCredits()).append("</td>");
            sb.append("<td>").append(instance.getDays()).append("</td>");
            sb.append("<td>").append(instance.getTime()).append("</td>");
            sb.append("<td>").append(startDate).append("</td>");
            sb.append("<td>").append(endDate).append("</td>");
            sb.append("<td>").append(term).append("</td>");
            sb.append("<td>").append(instance.getInstructor()).append("</td>");
            sb.append("<td>").append(instance.getSeatsAvail()).append("</td>");
            sb.append("<td>").append(instance.getCampus()).append("</td>");

            sb.append("</tr>");
        }
        sb.append("</tbody></table>");

        return sb.toString();
    }

    private String getFormattedDate(Date date){
        if(null == date){
            return "";
        }
        SimpleDateFormat format = new SimpleDateFormat(StringUtils.TABLE_DATE_OUTPUT_STRING);
        return format.format(date);
    }
    private String getFormattedTermString(String termString){
        if(null == termString || -1 == termString.indexOf("(")){
            return "";
        }
        return termString.substring(0,termString.indexOf("("));
    }

}
