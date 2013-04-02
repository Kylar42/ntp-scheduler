package edu.wvup.cs460.db;

import edu.wvup.cs460.configuration.AppProperties;
import edu.wvup.cs460.dataaccess.DataStorage;
import edu.wvup.cs460.db.migration.TableMigrator;

/**
 * User: Tom Byrne(kylar42@gmail.com)
 * "Code early, Code often."
 */
public class DatabaseMigrator {


    static void migrateTables(AppProperties props){
        DBContext context = new DBContext(props);
        DataStorage storage = new DataStorage(context);
        TableMigrator tableMigrator = new TableMigrator();
        tableMigrator.updateCourseMetaTableWithUpperDivision(storage);
    }

    public static void main(String[] args) {
        AppProperties props = new AppProperties();
        props.initPropertiesFromCommandLine(args);
        migrateTables(props);
    }

}
