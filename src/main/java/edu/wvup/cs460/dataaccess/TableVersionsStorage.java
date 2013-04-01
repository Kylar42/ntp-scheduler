package edu.wvup.cs460.dataaccess;

import edu.wvup.cs460.db.ConnectionPool;
import edu.wvup.cs460.util.Tuple;
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
 */
public class TableVersionsStorage implements DataStorage.StorageInstance<Tuple<String, Integer>> {

    private static final Logger LOG = LoggerFactory.getLogger(TableVersionsStorage.class);

    //------------------------------------------------------------------ PreparedStatements for CourseMetadata
    String TABLE_VERSION_INSERT_SQL = "insert into table_versions (TABLE_NAME, TABLE_VERSION)" +
            " values (?, ?)";
    String TABLE_VERSION_UPDATE_SQL = "update TABLE_VERSIONS set TABLE_VERSION=? where TABLE_NAME=?";
    String TABLE_VERSION_EXISTS_SQL = "select count(*) from TABLE_VERSIONS where TABLE_NAME=?";

    String TABLE_VERSION_LIST_SQL = "select * from TABLE_VERSIONS";
    String[] TABLE_VERSION_COLUMNS = {"TABLE_NAME", "TABLE_VERSION"};

    private final ConnectionPool _connectionPool;

    public TableVersionsStorage(ConnectionPool pool) {
        _connectionPool = pool;
    }

    @Override
    public boolean insert(Tuple<String, Integer> object) {
        final Connection connection = _connectionPool.getConnection();
        try {
            final PreparedStatement preparedStatement = connection.prepareStatement(TABLE_VERSION_INSERT_SQL);
            preparedStatement.setString(1, object.getKey());
            preparedStatement.setInt(2, object.getValue());
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
    public boolean update(Tuple<String, Integer> object) {
        final Connection connection = _connectionPool.getConnection();
        try {
            final PreparedStatement preparedStatement = connection.prepareStatement(TABLE_VERSION_UPDATE_SQL);
            preparedStatement.setInt(1, object.getValue());
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
    public boolean exists(Tuple<String, Integer> object) {
        final Connection connection = _connectionPool.getConnection();
        try {
            final PreparedStatement preparedStatement = connection.prepareStatement(TABLE_VERSION_EXISTS_SQL);
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
    public boolean insertOrUpdate(Tuple<String, Integer> object) {
        if(exists(object)){
            return update(object);
        }else{
            return insert(object);
        }
    }

    @Override
    public List<Tuple<String, Integer>> retrieveList(Object context) {
        List<Tuple<String, Integer>> courses = new ArrayList<Tuple<String, Integer>>();
        final Connection connection = _connectionPool.getConnection();
        try {
            final PreparedStatement preparedStatement = connection.prepareStatement(TABLE_VERSION_LIST_SQL);
            final ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String tableName = resultSet.getString(TABLE_VERSION_COLUMNS[0]);
                Integer tableVersion = resultSet.getInt(TABLE_VERSION_COLUMNS[1]);
                courses.add(new Tuple<String, Integer>(tableName, tableVersion));
            }
        } catch (SQLException e) {
            LOG.error("SQLError While retrieving URL Course list.", e);
        } finally {
            _connectionPool.returnConnection(connection);
        }


        return courses;
    }
}
