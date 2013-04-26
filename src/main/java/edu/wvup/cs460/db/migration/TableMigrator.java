package edu.wvup.cs460.db.migration;

import edu.wvup.cs460.dataaccess.DataStorage;
import edu.wvup.cs460.datamodel.CourseMetadata;

import java.util.List;

/**
 * User: Tom Byrne(kylar42@gmail.com)
 * "Code early, Code often."
 * This is a class that will hold specific migrations for tables that require code to update.
 */
public class TableMigrator {

    public void updateCourseMetaTableWithUpperDivision(DataStorage storage){
        final DataStorage.StorageInstance<CourseMetadata> courseMetadataStorageInstance = storage.courseMetadataStorage();
        final List<CourseMetadata> courseMetadatas = courseMetadataStorageInstance.retrieveList("");
        //now, best part, just stick em back in the DB, as the metadata object will recalculate them.
        for(CourseMetadata cm : courseMetadatas){
            courseMetadataStorageInstance.update(cm);
        }

        //and we're done.
    }

}
