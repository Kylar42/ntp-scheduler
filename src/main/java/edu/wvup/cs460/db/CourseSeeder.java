package edu.wvup.cs460.db;

import edu.wvup.cs460.dataaccess.DataStorage;
import edu.wvup.cs460.datamodel.CourseInstance;
import edu.wvup.cs460.datamodel.CourseMetadata;
import edu.wvup.cs460.transform.CourseImporter;
import edu.wvup.cs460.transform.ImportFactory;

import java.util.List;

/**
 * User: Tom Byrne(tom.byrne@apple.com)
 * Copyright (C) 2013 Apple Inc.
 * "Code early, Code often."
 */
public class CourseSeeder {


    void seedAllCourses(){
        final List<CourseInstance> courses = ImportFactory.getInstance().getCourseImporter().getCourses();
        DataStorage storageInstance = new DataStorage();
        for(CourseInstance ci : courses){
            storageInstance.insertOrUpdateCourseInstance(ci);
            //create a meta
            CourseMetadata meta = new CourseMetadata(ci.getSubject(), ci.getCourseNumber());
            storageInstance.insertOrUpdateCourseMeta(meta);
        }
    }


    public static void main(String[] args) {
        new CourseSeeder().seedAllCourses();
    }
}
