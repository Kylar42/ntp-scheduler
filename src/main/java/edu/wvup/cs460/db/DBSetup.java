package edu.wvup.cs460.db;

import edu.wvup.cs460.AppProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * User: Tom Byrne(tom.byrne@apple.com)
 */
public class DBSetup {
    
    //private static final DBSetup INSTANCE = new DBSetup();

    private final DBContext         _context;
    private final ConnectionPool    _connectionPool;
    private final DBSetupSQLStrings _sqlStrings;

    private static final Logger LOG = LoggerFactory.getLogger(DBSetup.class);


    //insert into course_meta (subject, course_number, humanities, natsci, socsci, math, communications, complit)"+
    //" values (?, ?, ?, ?, ?, ?, ?, ?)";

    private DBSetup(AppProperties props){
        _context = new DBContext(props);
        _sqlStrings = new DBSetupSQLStrings(_context);
        _connectionPool = new ConnectionPool(_context);
    }


    public DBContext getDBContext(){
        return _context;
    }


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
            final PreparedStatement createTerms = myConnection.prepareStatement(_sqlStrings.CREATE_TERMS_TABLE);
            createTerms.executeUpdate();
            LOG.info("School Terms table created.");
        }catch(SQLException sql){
            sql.printStackTrace();
        }finally{
            _connectionPool.returnConnection(myConnection);
        }
    }

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

        new CourseSeeder().seedAllCourses(setup.getDBContext());
    }
}

