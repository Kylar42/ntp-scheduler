package edu.wvup.cs460.transform;

import edu.wvup.cs460.datamodel.CourseInstance;
import edu.wvup.cs460.util.Tuple;

import java.util.Date;
import java.util.List;

/**
 * User: Tom Byrne(kylar42@gmail.com)
 * "Code early, Code often."
 * This will return a list of parsed course instances, as well as a date that they were produced.
 * We can later use the date to determine if they need to be re-fetched.
 */
public interface CourseImporter {
    List<CourseImportContext> getCourses(List<Tuple<String, String>> urlCacheTimes);
}
