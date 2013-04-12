package edu.wvup.cs460.http;

import edu.wvup.cs460.util.StringUtils;

/**
 * User: Tom Byrne(kylar42@gmail.com)
 * "Code early, Code often."
 */
public class ParsedURL {

    public enum OBJECT_TYPE{
        classlist, classmeta, authentication, user, unknown, nonexistant, unauthorized;

        private static OBJECT_TYPE forValue(String value){
            for(OBJECT_TYPE ot : values()){
                if(ot.name().equalsIgnoreCase(value)){
                    if(ot == OBJECT_TYPE.nonexistant){
                        return OBJECT_TYPE.unknown;
                    }
                    return ot;
                }
            }

            return unknown;
        }
    }

    public enum ACTION_TYPE{
        search, update, invalidate, validate, unknown, nonexistant;
        private static ACTION_TYPE forValue(String value){
            for(ACTION_TYPE ot : values()){
                if(ot.name().equalsIgnoreCase(value)){
                    if(ot == ACTION_TYPE.nonexistant){
                        return ACTION_TYPE.unknown;//if they pass in a string of /nonexistant, we don't recognize it.
                    }
                    return ot;
                }
            }

            return unknown;
        }

    }


    public static final ParsedURL ROOT_URL = new ParsedURL("/");

    OBJECT_TYPE _objectType;
    ACTION_TYPE _actionType;
    String[]     _remaining;
    String[]       _fullURL;


    private ParsedURL(String requestURL){



        // /OBJECTTYPE/ACTION/other

        if(null == requestURL ||requestURL.isEmpty()){
            _objectType = OBJECT_TYPE.nonexistant;
            _actionType = ACTION_TYPE.nonexistant;
            _remaining = null;
        }else{
            final String[] split = StringUtils.splitStringWithoutEmpty(requestURL, "/");
            _fullURL = split;
            if(null == split || split.length < 1){
                _objectType = OBJECT_TYPE.nonexistant;
                _actionType = ACTION_TYPE.nonexistant;
                _remaining = null;
            }else if(1 == split.length){
                _objectType = OBJECT_TYPE.forValue(split[0]);
                _actionType = ACTION_TYPE.nonexistant;
                _remaining = null;
            } else if(2 == split.length){
                _objectType = OBJECT_TYPE.forValue(split[0]);
                _actionType = ACTION_TYPE.forValue(split[1]);
                _remaining = null;
            } else{
                _objectType = OBJECT_TYPE.forValue(split[0]);
                _actionType = ACTION_TYPE.forValue(split[1]);
                int len = split.length - 2;
                _remaining = new String[len];
                System.arraycopy(split, 2, _remaining, 0, len);
            }
        }
    }

    public OBJECT_TYPE getObjectType(){
        return _objectType;
    }

    public ACTION_TYPE getActionType(){
        return _actionType;
    }

    public String[] getRemainingSegments(){
        return _remaining;
    }

    public boolean isRoot(){
        //it's root if there are no existing bits.
        return _objectType == OBJECT_TYPE.nonexistant && _actionType == ACTION_TYPE.nonexistant;
    }


    protected static boolean containsRelativeSegments(String url) {
        return (null != url && url.contains(".."));
    }


    public static ParsedURL createParsedURL(String url){
        if(containsRelativeSegments(url)){
            return ROOT_URL;  //let's just short-circuit this.
        }

        return new ParsedURL(url);
    }

    public String elementAt(int location){
        if(null == _fullURL || _fullURL.length < (location-1)){
            return null;
        }
        return _fullURL[location];
    }
}
