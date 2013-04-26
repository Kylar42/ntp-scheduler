package edu.wvup.cs460.transform;

/**
 * User: Tom Byrne(kylar42@gmail.com)
 * "Code early, Code often."
 * Factory to return the right type of importer. I used to have 2, but now I'm only using the HTMLScraper.
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
