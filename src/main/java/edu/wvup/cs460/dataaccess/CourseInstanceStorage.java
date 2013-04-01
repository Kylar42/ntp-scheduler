package edu.wvup.cs460.dataaccess;

import edu.wvup.cs460.NTPAppServer;
import edu.wvup.cs460.datamodel.CourseInstance;
import edu.wvup.cs460.db.ConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User: Tom Byrne(kylar42@gmail.com)
 * "Code early, Code often."
 */
public class CourseInstanceStorage implements DataStorage.StorageInstance<CourseInstance> {

    private static Logger LOG = LoggerFactory.getLogger(CourseInstanceStorage.class);
    //------------------------------------------------------------------Prepared Statements for Course Instances
    String COURSE_INSTANCE_INSERT_SQL = "insert into course_instance (crn, type, crosslisted, subject, course_number," +
            "course_title, credits, days, time, instructor, room, start_date, end_date, seats_available, " +
            "term_length, campus, term, year) VALUES " +
            "( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";

    String COURSE_INSTANCE_UPDATE_SQL = "update course_instance set " +
            "campus=?, type=?, crosslisted=?, subject=?, course_number=?, course_title=?, credits=?, days=?, time=?," +
            "instructor=?, room=?, start_date=?, end_date=?, seats_available=?, term_length=?, term=?, year=? where crn=?";

    String COURSE_INSTANCE_EXISTS_SQL = "select count(*) from course_instance where crn=?";

    String COURSE_INSTANCE_SEARCH_SQL = "select * from course_instance where subject=?, course_number=?";

    private final ConnectionPool _connectionPool;

    public CourseInstanceStorage(ConnectionPool sharedPool) {
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
            preparedStatement.setString(16, c.getTerm());
            preparedStatement.setString(17, c.getTermYear());
            preparedStatement.setString(18, c.getCrn());
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


    private final String COURSE_INSTANCE_QUERY_SQL = "SELECT course_instance.crn, course_instance.type, course_instance.subject, course_instance.course_number, " +
            "course_instance.course_title, course_instance.credits, course_instance.days, course_instance.time, course_instance.instructor, " +
            "course_instance.room, course_instance.start_date, course_instance.end_date, course_instance.seats_available, course_instance.term_length, " +
            " course_instance.campus" +
            " FROM course_instance" +
            " INNER JOIN course_meta" +
            " ON course_instance.subject=course_meta.subject and course_instance.course_number=course_meta.course_number " +
            " where ";

    //course_meta.humanities=True AND course_instance.term='Spring' and course_instance.year='2013'; \n";
    @Override
    public List<CourseInstance> retrieveList(Object context) {
        Map<String, String> requestedData = (Map<String, String>) context;
        List<CourseInstance> toReturn = new ArrayList<CourseInstance>();
        String sql = COURSE_INSTANCE_QUERY_SQL + whereClause(requestedData);
        final Connection connection = _connectionPool.getConnection();
        try {
            final PreparedStatement preparedStatement = connection.prepareStatement(sql);
            final ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                final String crn = resultSet.getString(1);
                final String type = resultSet.getString(2);
                final String subject = resultSet.getString(3);
                final String course_number = resultSet.getString(4);
                final String course_title = resultSet.getString(5);
                final short credits = resultSet.getShort(6);
                final String days = resultSet.getString(7);
                final String time = resultSet.getString(8);
                final String instructor = resultSet.getString(9);
                final String room = resultSet.getString(10);
                final Date startDate = resultSet.getDate(11);
                final Date endDate = resultSet.getDate(12);
                final short seats = resultSet.getShort(13);
                final String termLength = resultSet.getString(14);
                final String campus = resultSet.getString(15);

                CourseInstance instance = new CourseInstance(crn, type, false, subject, course_number, course_title, credits, days, time, instructor, room, startDate, endDate, seats, termLength, campus, "", "");
                toReturn.add(instance);
            }

        } catch (SQLException e) {
            LOG.error("Error occurred", e);
        } finally {
            _connectionPool.returnConnection(connection);
        }


        return toReturn;
    }

    private static String whereClause(Map<String, String> requests) {
        StringBuilder toReturn = new StringBuilder();
        boolean hasStarted = false;

        if (!requests.containsKey("fullClasses")) {
            hasStarted = true;
            toReturn.append(" course_instance.seats_available > 0 ");
        }

        for (String key : requests.keySet()) {   //
            if (!"fullClasses".equalsIgnoreCase(key)) {
                if (hasStarted) {
                    toReturn.append(" AND ");
                } else {
                    hasStarted = true;
                }
            }
            if (key.equals("term")) {
                toReturn.append(" course_instance.term='").append(requests.get(key)).append("'");
            } else if ("classname".equalsIgnoreCase(key)) {
                toReturn.append(" lower(course_instance.subject) like lower('%").append(requests.get(key)).append("%') ");
            } else if ("year".equalsIgnoreCase(key)) {
                toReturn.append(" course_instance.year='").append(requests.get(key)).append("'");
            } else if ("fullClasses".equalsIgnoreCase(key)) {
                //do nothing.
            } else {
                toReturn.append(" course_meta.").append(key).append("=True");
            }
        }


        return toReturn.toString();
    }
}
