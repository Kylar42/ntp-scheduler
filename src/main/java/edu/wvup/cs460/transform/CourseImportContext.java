package edu.wvup.cs460.transform;

import edu.wvup.cs460.datamodel.CourseInstance;

import java.util.Date;
import java.util.List;

/**
 * User: Tom Byrne(tom.byrne@apple.com)
 * "Code early, Code often."
 */
public class CourseImportContext {

    private final Date                  _dateImportLastModified;
    private final String                _path;
    private final List<CourseInstance>  _instances;

    public CourseImportContext(Date lastMod, String path, List<CourseInstance> instances){
        _dateImportLastModified = lastMod;
        _path = path;
        _instances = instances;
    }


    public String getPath(){
        return _path;
    }

    public List<CourseInstance> getInstances(){
        return _instances;
    }

    public Date getLastModified(){
        return _dateImportLastModified;
    }
}
