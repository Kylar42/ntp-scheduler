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
public class UserStorage implements DataStorage.StorageInstance<Tuple<String, String>> {

    private static final Logger LOG = LoggerFactory.getLogger(UserStorage.class);

    //------------------------------------------------------------------ PreparedStatements for CourseMetadata
    String USERS_INSERT_SQL = "insert into users (user, password)" +
            " values (?, ?)";
    String USERS_UPDATE_SQL = "update users set password=? where user=?";
    String USERS_EXISTS_SQL = "select count(*) from users where user=?";

    String USERS_LIST_SQL = "select * from users";
    String[] USERS_COLS = {"user", "password"};

    private final ConnectionPool _connectionPool;

    public UserStorage(ConnectionPool pool) {
        _connectionPool = pool;
    }

    @Override
    public boolean insert(Tuple<String, String> object) {
        final Connection connection = _connectionPool.getConnection();
        try {
            final PreparedStatement preparedStatement = connection.prepareStatement(USERS_INSERT_SQL);
            preparedStatement.setString(1, object.getKey());
            preparedStatement.setString(2, object.getValue());
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
    public boolean update(Tuple<String, String> object) {
        final Connection connection = _connectionPool.getConnection();
        try {
            final PreparedStatement preparedStatement = connection.prepareStatement(USERS_UPDATE_SQL);
            preparedStatement.setString(1, object.getValue());
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
    public boolean exists(Tuple<String, String> object) {
        final Connection connection = _connectionPool.getConnection();
        try {
            final PreparedStatement preparedStatement = connection.prepareStatement(USERS_EXISTS_SQL);
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
    public boolean insertOrUpdate(Tuple<String, String> object) {
        if(exists(object)){
            return update(object);
        }else{
            return insert(object);
        }
    }

    @Override
    public List<Tuple<String, String>> retrieveList(Object context) {
        List<Tuple<String, String>> courses = new ArrayList<Tuple<String, String>>();
        final Connection connection = _connectionPool.getConnection();
        try {
            final PreparedStatement preparedStatement = connection.prepareStatement(USERS_LIST_SQL);
            final ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String user = resultSet.getString(USERS_COLS[0]);
                String password = resultSet.getString(USERS_COLS[1]);
                courses.add(new Tuple<String, String>(user, password));
            }
        } catch (SQLException e) {
            LOG.error("SQLError While retrieving URL Course list.", e);
        } finally {
            _connectionPool.returnConnection(connection);
        }


        return courses;
    }
}
