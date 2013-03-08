package edu.wvup.cs460.http;

import edu.wvup.cs460.dataaccess.DataStorage;
import edu.wvup.cs460.datamodel.CourseMetadata;
import edu.wvup.cs460.util.MimeUtils;
import edu.wvup.cs460.util.StringUtils;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
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
 * User: Tom Byrne(tom.byrne@apple.com)
 * Copyright (C) 2013 Apple Inc.
 * "Code early, Code often."
 */
public class PostMethod extends AbstractHttpMethod {

    Logger LOG = LoggerFactory.getLogger(PostMethod.class);

    public PostMethod(MethodContext context){
        super(context);
    }

    @Override
    public void handleRequest(RequestWrapper reqWrapper, ResponseWrapper respWrapper) {
        String url = context().getUri();
        //TODO: URL based dispatcher for REST.
        String contentType = getHeaderValue(HeaderNames.ContentType, null);
        Object parsedJson = null;
        if(MimeType.APP_JSON.formattedString().equals(contentType)){
            //parse JSON
            final String jsonString = reqWrapper.getRequest().getContent().toString(CharsetUtil.UTF_8);
            JSONParser parser = new JSONParser(JSONParser.MODE_PERMISSIVE);
            try {
                parsedJson = parser.parse(jsonString);
            } catch (ParseException e) {
                LOG.error("Unable to parse JSON body from request.", e);
            }
        }


        if(url.startsWith("/search/classes")){
            doClassSearch(respWrapper, parsedJson);
            return;
        }

        if(url.startsWith("/update/classmeta")){
            doClassMetaUpdate(respWrapper, parsedJson);
            return;
        }

    }

    private CourseMetadata courseForClass(String subject, String classNumber, Map<String, Map<String, CourseMetadata>> courseMetaMap){
        CourseMetadata toReturn;
        //see if the class exists first.
        Map<String, CourseMetadata> courseSubjectMetadataMap = courseMetaMap.get(subject);

        if(null == courseSubjectMetadataMap){
            //create a new one, and a new CourseMeta as well.
            courseSubjectMetadataMap = new HashMap<String, CourseMetadata>();
            toReturn = new CourseMetadata(subject, classNumber);
            courseSubjectMetadataMap.put(classNumber, toReturn);
            courseMetaMap.put(subject, courseSubjectMetadataMap);
        }else{
            //have a map. let's see if the subject is there.
            toReturn = courseSubjectMetadataMap.get(classNumber);
            if(null == toReturn){
                toReturn = new CourseMetadata(subject, classNumber);
                courseSubjectMetadataMap.put(classNumber, toReturn);
            }
        }
        return toReturn;
    }

    private void setCourseMeta(CourseMetadata courseMeta, String field, String newValue){
        //assuming that newValue is going to be "on"

        boolean newVal = "on".equalsIgnoreCase(newValue);
        if(!newVal){
            //bad. what do we do?
            LOG.error("We recieved a value we didn't understand!");

        }

        if("isMath".equals(field)){
            courseMeta.setIsMath(newVal);
        }else if("isHumanities".equalsIgnoreCase(field)){
            courseMeta.setIsHumanities(newVal);
        }else if("isSocSci".equalsIgnoreCase(field)){
            courseMeta.setIsSocialScience(newVal);
        }else if("isNatSci".equalsIgnoreCase(field)){
            courseMeta.setIsNaturalScience(newVal);
        }else if("isComm".equalsIgnoreCase(field)){
            courseMeta.setIsCommunications(newVal);
        }else if("isCompLit".equalsIgnoreCase(field)){
            courseMeta.setIsComputerLit(newVal);
        }else {
            LOG.error("We recieved a value we didn't understand!");
        }
    }

    private void doClassMetaUpdate(ResponseWrapper respWrapper, Object parsedJson) {
        //This will be a map of maps.
        Map<String, Map<String, CourseMetadata>> courseMetaMap = new HashMap<String, Map<String, CourseMetadata>>();
        if(parsedJson instanceof JSONArray){
            //
            JSONArray array = (JSONArray)parsedJson;
            for(Object obj : array){
                if(obj instanceof JSONObject){
                    JSONObject jsonObj = (JSONObject)obj;
                    final Object name = jsonObj.get("name");
                    final Object value = jsonObj.get("value");
                    if(null != name){
                        final String[] split = name.toString().split("\\.");
                        if(split.length != 3){
                            LOG.error("Error in meta update trying to parse class. "+name);
                            continue;
                        }
                        CourseMetadata curCourse = courseForClass(split[0], split[1], courseMetaMap);
                        setCourseMeta(curCourse, split[2], value.toString());
                    }
                    System.out.println("name"+name+" Value:"+value);
                }
            }


            for(String subject : courseMetaMap.keySet()){
                final Map<String, CourseMetadata> subjectMap = courseMetaMap.get(subject);
                for(CourseMetadata courseMeta : subjectMap.values()){
                    getStorageService().updateCourseMeta(courseMeta);
                }
            }


            respWrapper.writeResponse(HttpResponseStatus.OK, "<p>Updated<p>", MimeType.TEXT_HTML);

        }
    }

    private void doClassSearch(ResponseWrapper respWrapper, Object parsedJson) {
        if(parsedJson instanceof JSONArray){
            JSONArray array = (JSONArray)parsedJson;
            String classToSearch = null;
            for(Object obj : array){
                if(obj instanceof JSONObject){
                    JSONObject jsonObj = (JSONObject)obj;
                    final Object name = jsonObj.get("name");
                    final Object value = jsonObj.get("value");
                    if("classname".equals(name)){
                        classToSearch = value.toString();
                    }
                    System.out.println("name"+name+" Value:"+value);
                }
            }

            //OK, classname has been found.
            DataStorage storage = new DataStorage();
            final List<CourseMetadata> coursesWithSubjectSubstring = storage.getCoursesWithSubjectSubstring(classToSearch);
            String form = constructDynamicFormFromCourseMeta(coursesWithSubjectSubstring);

            respWrapper.writeResponse(HttpResponseStatus.OK, form, MimeType.TEXT_HTML);

        }
    }

    private String constructDynamicFormFromCourseMeta(List<CourseMetadata> courses){
        StringBuilder sb = new StringBuilder();
        sb.append("\t\t<form method=\"post\" action=\"\" id=\"class-update\">\n");

        for(CourseMetadata course : courses){
            String idPrefix = course.getSubject()+"."+course.getCourseNumber()+".";
            //<label>Class Name</label> <input type="text" name="math101.classname" value="Math 101" disabled="true"/>
            sb.append("<br/><label>Class Name</label> <input type=\"text\" name=\""+idPrefix+"classname\" value=\""+
                              course.getSubject()+course.getCourseNumber()+"\" disabled=\"true\"/> ");
            //			<input type="checkbox" name="math101.isMath" /><label>is Math</label>
            boolean isMath = course.isMath();
            sb.append("\t\t\t<input type=\"checkbox\" name=\""+idPrefix+"isMath\" "+(isMath ? "checked=\"checked\"":"")+" /><label>is Math</label>\n");

            boolean isHumanities = course.isHumanities();
            sb.append("\t\t\t<input type=\"checkbox\" name=\""+idPrefix+"isHumanities\" "+(isHumanities ? "checked=\"checked\"":"")+" /><label>is Humanities</label>\n");

            boolean isSocSci = course.isSocialScience();
            sb.append("\t\t\t<input type=\"checkbox\" name=\""+idPrefix+"isSocSci\" "+(isSocSci ? "checked=\"checked\"":"")+" /><label>is Social Science</label>\n");

            boolean isNatsci = course.isNaturalScience();
            sb.append("\t\t\t<input type=\"checkbox\" name=\""+idPrefix+"isNatSci\" "+(isNatsci ? "checked=\"checked\"":"")+" /><label>is Natural Science</label>\n");

            boolean isComm = course.isCommunications();
            sb.append("\t\t\t<input type=\"checkbox\" name=\""+idPrefix+"isComm\" "+(isComm ? "checked=\"checked\"":"")+" /><label>is Communications</label>\n");

            boolean isCompLit = course.isComputerLit();
            sb.append("\t\t\t<input type=\"checkbox\" name=\""+idPrefix+"isCompLit\" "+(isCompLit ? "checked=\"checked\"":"")+" /><label>is Computer Lit</label>\n");

        }

        sb.append("\t\t\t<input type=\"button\" value=\"Update\" onclick=\"classUpdate(this.form)\" />\n</form>");

        return sb.toString();
    }


}
