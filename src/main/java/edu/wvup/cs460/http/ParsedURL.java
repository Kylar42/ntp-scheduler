package edu.wvup.cs460.http;

import edu.wvup.cs460.util.StringUtils;

/**
 * User: Tom Byrne(kylar42@gmail.com)
 * "Code early, Code often."
 */
public class ParsedURL {

    public enum OBJECT_TYPE{
        classlist, classmeta, authentication, user, unknown;

        private static OBJECT_TYPE forValue(String value){
            for(OBJECT_TYPE ot : values()){
                if(ot.name().equalsIgnoreCase(value)){
                    return ot;
                }
            }

            return unknown;
        }
    }

    public enum ACTION_TYPE{
        search, update, invalidate, validate, unknown;
        private static ACTION_TYPE forValue(String value){
            for(ACTION_TYPE ot : values()){
                if(ot.name().equalsIgnoreCase(value)){
                    return ot;
                }
            }

            return unknown;
        }

    }


    OBJECT_TYPE objectType;
    ACTION_TYPE actionType;
    String[]      remaining;

    public ParsedURL(String requestURL){
        //URL should look like

        // /OBJECTTYPE/ACTION/other

        if(null == requestURL ||requestURL.isEmpty()){
            objectType = OBJECT_TYPE.unknown;
            actionType = ACTION_TYPE.unknown;
            remaining = null;
        }else{
            final String[] split = StringUtils.splitStringWithoutEmpty(requestURL, "/");
            if(null == split || split.length < 1){
                objectType = OBJECT_TYPE.unknown;
                actionType = ACTION_TYPE.unknown;
                remaining = null;
            }else if(1 == split.length){
                objectType = OBJECT_TYPE.forValue(split[0]);
                actionType = ACTION_TYPE.unknown;
                remaining = null;
            } else if(2 == split.length){
                objectType = OBJECT_TYPE.forValue(split[0]);
                actionType = ACTION_TYPE.forValue(split[1]);
                remaining = null;
            } else{
                objectType = OBJECT_TYPE.forValue(split[0]);
                actionType = ACTION_TYPE.forValue(split[1]);
                int len = split.length - 2;
                remaining = new String[len];
                System.arraycopy(split, 2, remaining, 0, len);
            }

        }




    }

    public OBJECT_TYPE getObjectType(){
        return objectType;
    }

    public ACTION_TYPE getActionType(){
        return actionType;
    }

    public String[] getRemainingSegments(){
        return remaining;
    }
}
