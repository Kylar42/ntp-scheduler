package edu.wvup.cs460.dataaccess;

import edu.wvup.cs460.datamodel.CourseInstance;
import edu.wvup.cs460.datamodel.CourseMetadata;
import edu.wvup.cs460.db.ConnectionPool;
import edu.wvup.cs460.transform.ImportFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * User: Tom Byrne(tom.byrne@apple.com)
 * Copyright (C) 2013 Apple Inc.
 * "Code early, Code often."
 */
public class DataStorage {
    //------------------------------------------------------------------Prepared Statements for Course Instances
    String COURSE_INSTANCE_INSERT_SQL = "insert into course_instance (crn, type, crosslisted, subject, course_number," +
            "course_title, credits, days, time, instructor, room, start_date, end_date, seats_available, " +
            "term_length, campus) VALUES " +
            "( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";

    String COURSE_INSTANCE_UPDATE_SQL = "update course_instance set"+
            "campus=?, type=?, crosslisted=?, subject=?, course_number=?, course_title=?, credits=?, days=?, time=?,"+
            "instructor=?, room=?, start_date=?, end_date=?, seats_available=?, term_length=? where crn=?";

    String COURSE_INSTANCE_EXISTS_SQL = "select count(*) from course_instance where crn=?";

    //------------------------------------------------------------------ PreparedStatements for CourseMetadata
    String COURSE_META_INSERT_SQL = "insert into course_meta (subject, course_number, humanities, natsci, socsci, math, communications, complit)"+
            " values (?, ?, ?, ?, ?, ?, ?, ?)";
    String COURSE_META_UPDATE_SQL = "update course_meta set humanities=?, natsci=?, socsci=?, math=?, communications=?, complit=? where subject=? and course_number=?";
    String COURSE_META_EXISTS_SQL = "select count(*) from course_meta where subject=? and course_number=?";
    //------------------------------------------------------------------End

    public boolean insertCourseMeta(CourseMetadata courseMeta){
        final Connection connection = ConnectionPool.getInstance().getConnection();
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

            final int i = preparedStatement.executeUpdate();
            return i > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            ConnectionPool.getInstance().returnConnection(connection);
        }

    }

    public boolean updateCourseMeta(CourseMetadata courseMeta){
        final Connection connection = ConnectionPool.getInstance().getConnection();
        try {
            final PreparedStatement preparedStatement = connection.prepareStatement(COURSE_META_UPDATE_SQL);
            preparedStatement.setBoolean(1, courseMeta.isHumanities());
            preparedStatement.setBoolean(2, courseMeta.isNaturalScience());
            preparedStatement.setBoolean(3, courseMeta.isSocialScience());
            preparedStatement.setBoolean(4, courseMeta.isMath());
            preparedStatement.setBoolean(5, courseMeta.isCommunications());
            preparedStatement.setBoolean(6, courseMeta.isComputerLit());
            preparedStatement.setString(7, courseMeta.getSubject());
            preparedStatement.setString(8, courseMeta.getCourseNumber());

            final int i = preparedStatement.executeUpdate();
            return i > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            ConnectionPool.getInstance().returnConnection(connection);
        }

    }


    public boolean insertCourseInstance(CourseInstance c) {
        final Connection connection = ConnectionPool.getInstance().getConnection();
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
            final int i = preparedStatement.executeUpdate();
            return i > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            ConnectionPool.getInstance().returnConnection(connection);
        }

    }

    public boolean updateCourseInstance(CourseInstance c) {
        final Connection connection = ConnectionPool.getInstance().getConnection();
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
            e.printStackTrace();
            return false;
        } finally {
            ConnectionPool.getInstance().returnConnection(connection);
        }

    }

    public void insertOrUpdateCourseInstance(CourseInstance c) {
        if(courseInstanceExists(c)){
            updateCourseInstance(c);
        }else{
            insertCourseInstance(c);
        }
    }
    public void insertOrUpdateCourseMeta(CourseMetadata c) {
        if(courseMetaExists(c)){
            updateCourseMeta(c);
        }else{
            insertCourseMeta(c);
        }
    }

    public boolean courseInstanceExists(CourseInstance c) {
        final Connection connection = ConnectionPool.getInstance().getConnection();
        try {
            final PreparedStatement preparedStatement = connection.prepareStatement(COURSE_INSTANCE_EXISTS_SQL);
            preparedStatement.setString(1, c.getCrn());
            final ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            final int anInt = resultSet.getInt(1);
            return anInt > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionPool.getInstance().returnConnection(connection);
        }

        return true;
    }

    public boolean courseMetaExists(CourseMetadata c) {
        final Connection connection = ConnectionPool.getInstance().getConnection();
        try {
            final PreparedStatement preparedStatement = connection.prepareStatement(COURSE_META_EXISTS_SQL);
            preparedStatement.setString(1, c.getSubject());
            preparedStatement.setString(2, c.getCourseNumber());
            final ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            final int anInt = resultSet.getInt(1);
            return anInt > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionPool.getInstance().returnConnection(connection);
        }

        return true;
    }

    private static void importAllCourses(){

    }

    public static void main(String[] args) {
        importAllCourses();
    }



}
