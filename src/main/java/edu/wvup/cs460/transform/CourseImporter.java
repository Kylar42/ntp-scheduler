package edu.wvup.cs460.transform;

import edu.wvup.cs460.datamodel.CourseInstance;

import java.util.List;

/**
 * User: Tom Byrne(tom.byrne@apple.com)
 * Copyright (C) 2013 Apple Inc.
 * "Code early, Code often."
 */
public interface CourseImporter {
    List<CourseInstance> getCourses();
}
