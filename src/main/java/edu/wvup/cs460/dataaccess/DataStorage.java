package edu.wvup.cs460.dataaccess;

import edu.wvup.cs460.datamodel.CourseInstance;
import edu.wvup.cs460.datamodel.CourseMetadata;
import edu.wvup.cs460.db.ConnectionPool;
import edu.wvup.cs460.db.DBContext;
import edu.wvup.cs460.transform.ImportFactory;
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
 * User: Tom Byrne(kylar42@gmail.com)
 * "Code early, Code often."
 * This class is the main entry point for all Database operations.
 * Requests for StorageInstance implementations come here, and the appropriate
 * Implementation is returned.
 */
public class DataStorage {

    private static final Logger LOG = LoggerFactory.getLogger(DataStorage.class);

    //------------------------------------------------------------------generic storage

    /**
     * Generic interface that defines the API all our implementations will presenbt.
     * @param <T>
     */
    public interface StorageInstance<T>{
        boolean insert(T object);
        boolean update(T object);
        boolean exists(T object);
        boolean insertOrUpdate(T object);
        List<T> retrieveList(Object context);
    }

    //------------------------------------------------------------------

    private ConnectionPool          _sharedPool;

    //Delegates for each type
    private CourseMetaStorage       _courseMetaStorage;

    private CourseInstanceStorage   _courseInstanceStorage;

    private URLCacheStorage         _urlCacheStorage;

    private TableVersionsStorage    _tableVersionsStorage;

    private UserStorage             _userStorage;
    //------------------------------------------------------------------
    public DataStorage(DBContext context){
        _sharedPool = new ConnectionPool(context);
        _courseMetaStorage = new CourseMetaStorage(_sharedPool);
        _courseInstanceStorage = new CourseInstanceStorage(_sharedPool);
        _urlCacheStorage = new URLCacheStorage(_sharedPool);
        _tableVersionsStorage = new TableVersionsStorage(_sharedPool);
        _userStorage = new UserStorage(_sharedPool);
    }

    public StorageInstance<CourseMetadata> courseMetadataStorage(){
        return _courseMetaStorage;
    }

    public StorageInstance<CourseInstance> courseInstanceStorage(){
        return _courseInstanceStorage;
    }

    public StorageInstance<Tuple<String, String>> urlCacheStorage(){
        return _urlCacheStorage;
    }

    public StorageInstance<Tuple<String, Integer>> tableVersionStorage(){
        return _tableVersionsStorage;
    }
    // I took out the code that uses the user table, but I'm leaving it in
    // since it's forseeable that we could use it.
    public StorageInstance<Tuple<String, String>> userStorage(){
        return _userStorage;
    }

}
