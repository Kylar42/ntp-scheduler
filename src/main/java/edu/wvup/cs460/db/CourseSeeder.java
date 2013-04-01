package edu.wvup.cs460.db;

import edu.wvup.cs460.dataaccess.DataStorage;
import edu.wvup.cs460.datamodel.CourseInstance;
import edu.wvup.cs460.datamodel.CourseMetadata;
import edu.wvup.cs460.transform.CourseImportContext;
import edu.wvup.cs460.transform.CourseImporter;
import edu.wvup.cs460.transform.ImportFactory;
import edu.wvup.cs460.util.Tuple;

import java.util.Date;
import java.util.List;

/**
 * User: Tom Byrne(kylar42@gmail.com)
 * "Code early, Code often."
 */
public class CourseSeeder {


    public void seedAllCourses(DBContext context){
        DataStorage storageInstance = new DataStorage(context);
        final List<Tuple<String, String>> lastModTimes = storageInstance.urlCacheStorage().retrieveList(null);


        final List<CourseImportContext> coursesContexts = ImportFactory.getInstance().getCourseImporter().getCourses(lastModTimes);//
        writeModDates(storageInstance, coursesContexts);

        for(CourseImportContext cic : coursesContexts){
            List<CourseInstance> courses = cic.getInstances();

            for(CourseInstance ci : courses){
                storageInstance.courseInstanceStorage().insertOrUpdate(ci);
                //create a meta
                CourseMetadata meta = new CourseMetadata(ci.getSubject(), ci.getCourseNumber());
                storageInstance.courseMetadataStorage().insert(meta);//we're calling insert here instead of insert or update, so we don't blow away existing ones.
            }

        }
    }

   private void writeModDates(DataStorage storageInstance, List<CourseImportContext> contexts){
       for(CourseImportContext context : contexts){
           Tuple<String, String> lastModData = new Tuple<String, String>(context.getPath(), context.getContentMD5());
           storageInstance.urlCacheStorage().insertOrUpdate(lastModData);
       }
       //write to DB
   }
    public static void main(String[] args) {
        //new CourseSeeder().seedAllCourses();
    }
}
