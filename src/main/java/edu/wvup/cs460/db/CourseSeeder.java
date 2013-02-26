package edu.wvup.cs460.db;

import edu.wvup.cs460.datamodel.Course;
import edu.wvup.cs460.transform.CourseImporter;
import edu.wvup.cs460.transform.ImportFactory;

import java.util.List;

/**
 * User: Tom Byrne(tom.byrne@apple.com)
 * Copyright (C) 2013 Apple Inc.
 * "Code early, Code often."
 */
public class CourseSeeder {
    public static void main(String[] args) {
        final CourseImporter courseImporter = ImportFactory.getCourseImporter();
        final List<Course> courses = courseImporter.getCourses();
        System.out.println(courses);
    }
}
