package edu.wvup.cs460.transform;

/**
 * User: Tom Byrne(kylar42@gmail.com)
 * "Code early, Code often."
 */
public class ImportFactory {

    private static ImportFactory INSTANCE = new ImportFactory();

    public static ImportFactory getInstance(){
        return INSTANCE;
    }

    public CourseImporter getCourseImporter(){
        return new HTMLScraperImporter();
    }


}
