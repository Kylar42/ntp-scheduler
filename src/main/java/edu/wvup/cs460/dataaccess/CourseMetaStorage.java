package edu.wvup.cs460.dataaccess;

import edu.wvup.cs460.datamodel.CourseMetadata;
import edu.wvup.cs460.db.ConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Tom Byrne(kylar42@gmail.com)
 * "Code early, Code often."
 * Implementation of StorageInstance for CourseMetadata objects.
 * @see edu.wvup.cs460.dataaccess.DataStorage.StorageInstance
 */
public class CourseMetaStorage implements DataStorage.StorageInstance<CourseMetadata> {

    private static final Logger LOG = LoggerFactory.getLogger(CourseMetaStorage.class);

    //------------------------------------------------------------------ PreparedStatements for CourseMetadata
    String COURSE_META_INSERT_SQL = "insert into course_meta (subject, course_number, humanities, natsci, socsci, math, communications, complit, upperdiv)"+
            " values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    String COURSE_META_UPDATE_SQL = "update course_meta set humanities=?, natsci=?, socsci=?, math=?, communications=?, complit=?, upperdiv=? where subject=? and course_number=?";
    String COURSE_META_EXISTS_SQL = "select count(*) from course_meta where subject=? and course_number=?";

    String COURSE_LIST_SQL = "select * from course_meta where lower(subject) like lower(?)";
    String[] COURSE_META_COLS = {"subject", "course_number", "humanities", "natsci", "socsci", "math", "communications", "complit", "upperdiv"};

    private final ConnectionPool _connectionPool;

    public CourseMetaStorage(ConnectionPool pool){
        _connectionPool = pool;
    }




    public List<CourseMetadata> retrieveList(Object substring){
        if(null == substring){
            substring="";//let's not try to search for a null.
        }
        List<CourseMetadata> courses = new ArrayList<CourseMetadata>();
        final Connection connection = _connectionPool.getConnection();
        try {
            final PreparedStatement preparedStatement = connection.prepareStatement(COURSE_LIST_SQL);
            String wildcard = "%"+substring+"%";//wildcard
            preparedStatement.setString(1, wildcard);
            final ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                String subject = resultSet.getString(COURSE_META_COLS[0]);
                String courseNum = resultSet.getString(COURSE_META_COLS[1]);

                boolean humanities = resultSet.getBoolean(COURSE_META_COLS[2]);
                boolean natsci = resultSet.getBoolean(COURSE_META_COLS[3]);
                boolean socsci = resultSet.getBoolean(COURSE_META_COLS[4]);
                boolean math = resultSet.getBoolean(COURSE_META_COLS[5]);
                boolean comm = resultSet.getBoolean(COURSE_META_COLS[6]);
                boolean complit = resultSet.getBoolean(COURSE_META_COLS[7]);
                boolean upperdiv = resultSet.getBoolean(COURSE_META_COLS[8]);
                CourseMetadata cMeta = new CourseMetadata(subject, courseNum, humanities, natsci, socsci, math, comm, complit, upperdiv);
                courses.add(cMeta);

            }
        } catch (SQLException e) {
            LOG.error("SQLError While retrieving Course list.", e);
        } finally {
            _connectionPool.returnConnection(connection);
        }


        return courses;
    }

    public boolean insert(CourseMetadata courseMeta){
        final Connection connection = _connectionPool.getConnection();
        try {
            final PreparedStatement preparedStatement = connection.prepareStatement(COURSE_META_INSERT_SQL);
            preparedStatement.setString(1, courseMeta.getSubject());
            preparedStatement.setString(2, courseMeta.getCourseNumber());
            preparedStatement.setBoolean(3, courseMeta.isHumanities());
            preparedStatement.setBoolean(4, courseMeta.isNaturalScience());
            preparedStatement.setBoolean(5, courseMeta.isSocialScience());
            preparedStatement.setBoolean(6, courseMeta.isMath());
            preparedStatement.setBoolean(7, courseMeta.isCommunications());
            preparedStatement.setBoolean(8, courseMeta.isComputerLit());
            preparedStatement.setBoolean(9, courseMeta.isUpperDivision());
            final int i = preparedStatement.executeUpdate();
            return i > 0;
        } catch (SQLException e) {
            LOG.debug("Error occurred", e); //moved this to debug because it can happen as part of our normal, for now.
            return false;
        } finally {
            _connectionPool.returnConnection(connection);
        }

    }

    public boolean update(CourseMetadata courseMeta){
        final Connection connection = _connectionPool.getConnection();
        try {
            final PreparedStatement preparedStatement = connection.prepareStatement(COURSE_META_UPDATE_SQL);
            preparedStatement.setBoolean(1, courseMeta.isHumanities());
            preparedStatement.setBoolean(2, courseMeta.isNaturalScience());
            preparedStatement.setBoolean(3, courseMeta.isSocialScience());
            preparedStatement.setBoolean(4, courseMeta.isMath());
            preparedStatement.setBoolean(5, courseMeta.isCommunications());
            preparedStatement.setBoolean(6, courseMeta.isComputerLit());
            preparedStatement.setBoolean(7, courseMeta.isUpperDivision());
            preparedStatement.setString(8, courseMeta.getSubject());
            preparedStatement.setString(9, courseMeta.getCourseNumber());

            final int i = preparedStatement.executeUpdate();
            return i > 0;
        } catch (SQLException e) {
            LOG.error("Error occurred", e);
            return false;
        } finally {
            _connectionPool.returnConnection(connection);
        }

    }

    public boolean exists(CourseMetadata c) {
        final Connection connection = _connectionPool.getConnection();
        try {
            final PreparedStatement preparedStatement = connection.prepareStatement(COURSE_META_EXISTS_SQL);
            preparedStatement.setString(1, c.getSubject());
            preparedStatement.setString(2, c.getCourseNumber());
            final ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            final int anInt = resultSet.getInt(1);
            return anInt > 0;
        } catch (SQLException e) {
            LOG.error("Error occurred", e);
        } finally {
            _connectionPool.returnConnection(connection);
        }

        return true;
    }

    public boolean insertOrUpdate(CourseMetadata c) {
        if(exists(c)){
            return update(c);
        }else{
            return insert(c);
        }
    }
}
