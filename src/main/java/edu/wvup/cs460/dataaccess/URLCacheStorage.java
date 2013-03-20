package edu.wvup.cs460.dataaccess;

import edu.wvup.cs460.datamodel.CourseMetadata;
import edu.wvup.cs460.db.ConnectionPool;
import edu.wvup.cs460.util.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: Tom Byrne(tom.byrne@apple.com)
 * Copyright (C) 2013 Apple Inc.
 * "Code early, Code often."
 */
public class URLCacheStorage implements DataStorage.StorageInstance<Tuple<String, Date>> {

    private static final Logger LOG = LoggerFactory.getLogger(URLCacheStorage.class);

    //------------------------------------------------------------------ PreparedStatements for CourseMetadata
    String URL_CACHE_INSERT_SQL = "insert into url_cache (url, lastmodified)" +
            " values (?, ?)";
    String URL_CACHE_UPDATE_SQL = "update url_cache set lastmodified=? where url=?";
    String URL_CACHE_EXISTS_SQL = "select count(*) from url_cache where url=?";

    String URL_CACHE_LIST_SQL = "select * from url_cache;";
    String[] URL_CACHE_COLS = {"url", "lastmodified"};

    private final ConnectionPool _connectionPool;

    public URLCacheStorage(ConnectionPool pool) {
        _connectionPool = pool;
    }

    @Override
    public boolean insert(Tuple<String, Date> object) {
        final Connection connection = _connectionPool.getConnection();
        try {
            final PreparedStatement preparedStatement = connection.prepareStatement(URL_CACHE_INSERT_SQL);
            preparedStatement.setString(1, object.getKey());
            preparedStatement.setDate(2, new java.sql.Date(object.getValue().getTime()));
            final int i = preparedStatement.executeUpdate();
            return i > 0;
        } catch (SQLException e) {
            LOG.error("Error occurred", e);
            return false;
        } finally {
            _connectionPool.returnConnection(connection);
        }
    }

    @Override
    public boolean update(Tuple<String, Date> object) {
        final Connection connection = _connectionPool.getConnection();
        try {
            final PreparedStatement preparedStatement = connection.prepareStatement(URL_CACHE_UPDATE_SQL);
            preparedStatement.setDate(1, new java.sql.Date(object.getValue().getTime()));
            preparedStatement.setString(2, object.getKey());
            final int i = preparedStatement.executeUpdate();
            return i > 0;
        } catch (SQLException e) {
            LOG.error("Error occurred", e);
            return false;
        } finally {
            _connectionPool.returnConnection(connection);
        }
    }

    @Override
    public boolean exists(Tuple<String, Date> object) {
        final Connection connection = _connectionPool.getConnection();
        try {
            final PreparedStatement preparedStatement = connection.prepareStatement(URL_CACHE_EXISTS_SQL);
            preparedStatement.setString(1, object.getKey());
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
    public boolean insertOrUpdate(Tuple<String, Date> object) {
        if(exists(object)){
            return update(object);
        }else{
            return insert(object);
        }
    }

    @Override
    public List<Tuple<String, Date>> retrieveList(Object context) {
        List<Tuple<String, Date>> courses = new ArrayList<Tuple<String, Date>>();
        final Connection connection = _connectionPool.getConnection();
        try {
            final PreparedStatement preparedStatement = connection.prepareStatement(URL_CACHE_LIST_SQL);
            final ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String url = resultSet.getString(URL_CACHE_COLS[0]);
                Date lastMod = resultSet.getDate(URL_CACHE_COLS[1]);
                courses.add(new Tuple<String, Date>(url, lastMod));
            }
        } catch (SQLException e) {
            LOG.error("SQLError While retrieving URL Course list.", e);
        } finally {
            _connectionPool.returnConnection(connection);
        }


        return courses;
    }
}
