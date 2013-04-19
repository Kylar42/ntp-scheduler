package edu.wvup.monitor;



/**
 * User: Tom Byrne(kylar42@gmail.com)
 * "Code early, Code often."
 */
public class Entry {
    private String      _relativeFile;
    private String      _hash;
    private long        _time;
    private long        _size;
    private EntryStatus _status;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Entry entry = (Entry) o;

        if (_size != entry._size) {
            return false;
        }
        if (_time != entry._time) {
            return false;
        }
        if (_hash != null ? !_hash.equals(entry._hash) : entry._hash != null) {
            return false;
        }
        if (_relativeFile != null ? !_relativeFile.equals(entry._relativeFile) : entry._relativeFile != null) {
            return false;
        }
        if (_status != entry._status) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = _relativeFile != null ? _relativeFile.hashCode() : 0;
        result = 31 * result + (_hash != null ? _hash.hashCode() : 0);
        result = 31 * result + (int) (_time ^ (_time >>> 32));
        result = 31 * result + (int) (_size ^ (_size >>> 32));
        result = 31 * result + (_status != null ? _status.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Entry{" +
                "_relativeFile='" + _relativeFile + '\'' +
                ", _hash='" + _hash + '\'' +
                ", _time=" + _time +
                ", _size=" + _size +
                ", _status=" + _status +
                '}';
    }
}
