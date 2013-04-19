package edu.wvup.monitor.manifest;

import edu.wvup.monitor.Entry;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * User: Tom Byrne(kylar42@gmail.com)
 * "Code early, Code often."
 */
public class Manifest {

    private final int       _version;
    private final String    _guid;
    private final long      _timestamp;
    private final String    _urlRoot;

    private final ArrayList<Entry> _entries = new ArrayList<Entry>();


    public Manifest(int version, String guid, String urlRoot){
        _version = version;
        _guid = guid;
        _urlRoot = urlRoot;
        _timestamp = System.currentTimeMillis();
    }

    public int getVersion(){ return _version;}
    public String getGuid(){ return _guid;   }


    public long getTimestamp(){
        return _timestamp;
    }
    public List<Entry> getEntries(){
        return _entries;
    }

    public void addEntry(Entry e){
        _entries.add(e);
    }

    public String getUrlRoot(){
        return _urlRoot;
    }
}
