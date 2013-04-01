package edu.wvup.cs460.transform;

import edu.wvup.cs460.datamodel.CourseInstance;

import java.util.Date;
import java.util.List;

/**
 * User: Tom Byrne(kylar42@gmail.com)
 * "Code early, Code often."
 */
public class CourseImportContext {

    private final String                _importContentMD5;
    private final String                _path;
    private final List<CourseInstance>  _instances;

    public CourseImportContext(String md5, String path, List<CourseInstance> instances){
        _importContentMD5 = md5;
        _path = path;
        _instances = instances;
    }


    public String getPath(){
        return _path;
    }

    public List<CourseInstance> getInstances(){
        return _instances;
    }

    public String getContentMD5(){
        return _importContentMD5;
    }
}
