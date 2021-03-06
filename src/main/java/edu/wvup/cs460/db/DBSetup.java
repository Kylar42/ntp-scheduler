package edu.wvup.cs460.db;

import edu.wvup.cs460.configuration.AppProperties;
import edu.wvup.cs460.dataaccess.DataStorage;
import edu.wvup.cs460.util.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * User: Tom Byrne(kylar42@gmail.com)
 * "If I cannot see, it is because I am being stood upon by giants."
 *
 * This is the main class that will do all the database setup, all it needs is the database to be installed, and the username and password of the admin user.
 *
 */
public class DBSetup {

    private static final Logger LOG = LoggerFactory.getLogger(DBSetup.class);

    //=========================================================Data Members
    private final DBContext         _context;
    private final ConnectionPool    _connectionPool;
    private final DBSetupSQLStrings _sqlStrings;


    private DBSetup(AppProperties props){
        _context = new DBContext(props);
        _sqlStrings = new DBSetupSQLStrings(_context);
        _connectionPool = new ConnectionPool(_context);
    }


    public DBContext getDBContext(){
        return _context;
    }

    //=========================================================

    /**
     * This method creates the db for all our tables.
     */
    public void createOrSetupDB(){
        LOG.info("Attempting to create DB");
        Connection myConnection = _connectionPool.getRootConnection();
        try{
            final PreparedStatement preparedStatement = myConnection.prepareStatement(_sqlStrings.DB_CREATE);
            final int executed = preparedStatement.executeUpdate();
           LOG.info("Database created.");
        }catch(SQLException sql){
            sql.printStackTrace();
        }finally{
            _connectionPool.returnRootConnection(myConnection);
        }
    }

    /**
     * This method will drop our read/write user.
     * Used in testing.
     */
    public void dropUser(){
        Connection myConnection =  _connectionPool.getRootConnection();
        try{
            final PreparedStatement preparedStatement = myConnection.prepareStatement(_sqlStrings.RW_USER_DROP);
            final int executed = preparedStatement.executeUpdate();
            LOG.info("Users Dropped.");
        }catch(SQLException sql){
            sql.printStackTrace();
        }finally{
            _connectionPool.returnRootConnection(myConnection);
        }
    }

    /**
     * Create our application's Read/write user.
     */
    public void createUser(){
        Connection myConnection =  _connectionPool.getRootConnection();
        try{
            PreparedStatement preparedStatement = myConnection.prepareStatement(_sqlStrings.RW_USER_CREATE);
            int executed = preparedStatement.executeUpdate();
            LOG.info("Successfully created User.");
            preparedStatement = myConnection.prepareStatement(_sqlStrings.RW_USER_GRANT);
            executed = preparedStatement.executeUpdate();
            LOG.info("Successfully granted user access.");
        }catch(SQLException sqlErr){
            LOG.error("Unable to create user!", sqlErr);
        }finally{
            _connectionPool.returnRootConnection(myConnection);
        }
    }

    /**
     * Create all the appropriate tables for our application.
     */
    public void createOrSetupTables(){
        Connection myConnection =  _connectionPool.getConnection();
        try{
            final PreparedStatement createCourseInstance = myConnection.prepareStatement(_sqlStrings.CREATE_COURSE_INSTANCE_TABLE);
            createCourseInstance.executeUpdate();
            LOG.info("Course Instance Table created.");
            final PreparedStatement createCourseMeta = myConnection.prepareStatement(_sqlStrings.CREATE_COURSE_META_TABLE);
            createCourseMeta.executeUpdate();
            LOG.info("Course Metadata Table created.");
            final PreparedStatement createUrlCache = myConnection.prepareStatement(_sqlStrings.CREATE_URL_CACHE_TABLE);
            createUrlCache.executeUpdate();
            LOG.info("URL Cache Table created.");
            final PreparedStatement createVersions = myConnection.prepareStatement(_sqlStrings.CREATE_TABLE_VERSION_TABLE);
            createVersions.executeUpdate();
            LOG.info("Table Versions table created.");
            final PreparedStatement createTerms = myConnection.prepareStatement(_sqlStrings.CREATE_TERMS_TABLE);
            createTerms.executeUpdate();
            LOG.info("School Terms table created.");
        }catch(SQLException sql){
            sql.printStackTrace();
        }finally{
            _connectionPool.returnConnection(myConnection);
        }
    }

    /**
     * Seed base info for each table we created.
     */
    public void seedVersionsTable(){
        DataStorage storage = new DataStorage(_context);
        for(String tableName : DBSetupSQLStrings.DATABASE_TABLES){
            storage.tableVersionStorage().insert(new Tuple<String, Integer>(tableName, 1));
        }
    }

    /**
     * Drop the db and all associated tables.
     * Used for testing. BEWARE THIS METHOD!
     */
    public void dropDB(){
        Connection myConnection =  _connectionPool.getRootConnection();
        try{
            final PreparedStatement preparedStatement = myConnection.prepareStatement(_sqlStrings.DB_DROP);
            final int executed = preparedStatement.executeUpdate();

        }catch(SQLException sql){
            sql.printStackTrace();
        }finally{
            _connectionPool.returnRootConnection(myConnection);
        }
    }

    /**
     * Entry point for database setup. Note that the drop statements are commented out. Don't uncomment them
     * unless you want a full reset of everything.
     * @param args
     */
    public static void main(String[] args) {
        AppProperties props = new AppProperties();
        props.initPropertiesFromCommandLine(args);
        DBSetup setup = new DBSetup(props);
        //look for what was asked for.

        //setup.dropDB();
        //setup.dropUser();
        setup.createOrSetupDB();
        setup.createUser();
        setup.createOrSetupTables();
        setup.seedVersionsTable();//need this for the migrator to use later.

        //new CourseSeeder().seedAllCourses(setup.getDBContext());
    }
}

