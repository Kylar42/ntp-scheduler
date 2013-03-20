package edu.wvup.cs460.dataaccess;

import edu.wvup.cs460.NTPAppServer;
import edu.wvup.cs460.datamodel.CourseInstance;
import edu.wvup.cs460.db.ConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User: Tom Byrne(tom.byrne@apple.com)
 * Copyright (C) 2013 Apple Inc.
 * "Code early, Code often."
 */
public class CourseInstanceStorage implements DataStorage.StorageInstance<CourseInstance> {

    private static Logger LOG = LoggerFactory.getLogger(CourseInstanceStorage.class);
    //------------------------------------------------------------------Prepared Statements for Course Instances
    String COURSE_INSTANCE_INSERT_SQL = "insert into course_instance (crn, type, crosslisted, subject, course_number," +
            "course_title, credits, days, time, instructor, room, start_date, end_date, seats_available, " +
            "term_length, campus) VALUES " +
            "( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";

    String COURSE_INSTANCE_UPDATE_SQL = "update course_instance set" +
            "campus=?, type=?, crosslisted=?, subject=?, course_number=?, course_title=?, credits=?, days=?, time=?," +
            "instructor=?, room=?, start_date=?, end_date=?, seats_available=?, term_length=?, term=?, year=? where crn=?";

    String COURSE_INSTANCE_EXISTS_SQL = "select count(*) from course_instance where crn=?";

    String COURSE_INSTANCE_SEARCH_SQL = "select * from course_instance where subject=?, course_number=?";

    private final ConnectionPool _connectionPool;
    
    public CourseInstanceStorage(ConnectionPool sharedPool){
        _connectionPool = sharedPool;
    }
    
    public boolean insert(CourseInstance c) {
        final Connection connection = _connectionPool.getConnection();
        try {
            final PreparedStatement preparedStatement = connection.prepareStatement(COURSE_INSTANCE_INSERT_SQL);
            preparedStatement.setString(1, c.getCrn());
            preparedStatement.setString(2, c.getType());
            preparedStatement.setBoolean(3, c.isCrosslisted());
            preparedStatement.setString(4, c.getSubject());
            preparedStatement.setString(5, c.getCourseNumber());
            preparedStatement.setString(6, c.getCourseTitle());
            preparedStatement.setShort(7, c.getCredits());
            preparedStatement.setString(8, c.getDays());
            preparedStatement.setString(9, c.getTime());
            preparedStatement.setString(10, c.getInstructor());
            preparedStatement.setString(11, c.getRoom());
            preparedStatement.setDate(12, new java.sql.Date(c.getStartDate().getTime()));
            preparedStatement.setDate(13, new java.sql.Date(c.getEndDate().getTime()));
            preparedStatement.setShort(14, c.getSeatsAvail());
            preparedStatement.setString(15, c.getTermLength());
            preparedStatement.setString(16, c.getCampus());
            preparedStatement.setString(17, c.getTerm());
            preparedStatement.setString(18, c.getTermYear());
            final int i = preparedStatement.executeUpdate();
            return i > 0;
        } catch (SQLException e) {
            LOG.error("Error occurred", e);
            return false;
        } finally {
            _connectionPool.returnConnection(connection);
        }

    }

    public boolean update(CourseInstance c) {
        final Connection connection = _connectionPool.getConnection();
        try {
            final PreparedStatement preparedStatement = connection.prepareStatement(COURSE_INSTANCE_UPDATE_SQL);
            preparedStatement.setString(1, c.getCampus());
            preparedStatement.setString(2, c.getType());
            preparedStatement.setBoolean(3, c.isCrosslisted());
            preparedStatement.setString(4, c.getSubject());
            preparedStatement.setString(5, c.getCourseNumber());
            preparedStatement.setString(6, c.getCourseTitle());
            preparedStatement.setShort(7, c.getCredits());
            preparedStatement.setString(8, c.getDays());
            preparedStatement.setString(9, c.getTime());
            preparedStatement.setString(10, c.getInstructor());
            preparedStatement.setString(11, c.getRoom());
            preparedStatement.setDate(12, new java.sql.Date(c.getStartDate().getTime()));
            preparedStatement.setDate(13, new java.sql.Date(c.getEndDate().getTime()));
            preparedStatement.setShort(14, c.getSeatsAvail());
            preparedStatement.setString(15, c.getTermLength());
            preparedStatement.setString(16, c.getCrn());
            final int i = preparedStatement.executeUpdate();
            return i > 0;
        } catch (SQLException e) {
            LOG.error("Error occurred", e);
            return false;
        } finally {
            _connectionPool.returnConnection(connection);
        }

    }

    public boolean insertOrUpdate(CourseInstance c) {
        if (exists(c)) {
            return update(c);
        } else {
            return insert(c);
        }
    }


    public boolean exists(CourseInstance c) {
        final Connection connection = _connectionPool.getConnection();
        try {
            final PreparedStatement preparedStatement = connection.prepareStatement(COURSE_INSTANCE_EXISTS_SQL);
            preparedStatement.setString(1, c.getCrn());
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

    @Override
    public List<CourseInstance> retrieveList(Object context) {
        Map<String, String> requestedData = (Map<String, String>)context;
        List<CourseInstance> toReturn = new ArrayList<CourseInstance>();




        return toReturn;
    }
}
