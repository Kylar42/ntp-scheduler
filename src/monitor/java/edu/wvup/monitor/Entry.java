package edu.wvup.monitor;



/**
 * User: Tom Byrne(kylar42@gmail.com)
 * "Code early, Code often."
 */
public class Entry {
    String      _relativeFile;
    String      _hash;
    long        _time;
    long        _size;
    EntryStatus _status;

    public Entry(String relativeFile, String hash, long time, long size, EntryStatus status){
        _relativeFile = relativeFile;
        _hash = hash;
        _time = time;
        _size = size;
        _status = status;
    }

    public String getRelativeFile() { return _relativeFile; }
    public String         getHash() { return _hash;         }
    public long           getTime() { return _time;         }
    public long           getSize() { return _size;         }
    public EntryStatus  getStatus() { return _status;       }
}
