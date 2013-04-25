package edu.wvup.cs460.util;

import java.util.Map;

/**
 * User: Tom Byrne(kylar42@gmail.com)
 *
 * "Code early, Code often."
 * A tuple of two typed values. I'll implement Map.Entry as well to make things a bit easier for collection manipulations later.
 *
 */
public class Tuple<K, V> implements Map.Entry<K, V> {

    private final K _value1;
    private V _value2;


    public Tuple(K k, V v){
        _value1 = k;
        _value2 = v;
    }

    @Override
    public K getKey() {
        return _value1;
    }

    @Override
    public V getValue() {
        return _value2;
    }

    @Override
    public V setValue(V value) {
        V toReturn = _value2;
        _value2 = value;
        return toReturn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Tuple tuple = (Tuple) o;

        if (_value1 != null ? !_value1.equals(tuple._value1) : tuple._value1 != null) {
            return false;
        }
        if (_value2 != null ? !_value2.equals(tuple._value2) : tuple._value2 != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = _value1 != null ? _value1.hashCode() : 0;
        result = 31 * result + (_value2 != null ? _value2.hashCode() : 0);
        return result;
    }
}
