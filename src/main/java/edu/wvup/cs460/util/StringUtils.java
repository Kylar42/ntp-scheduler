package edu.wvup.cs460.util;

/**
 * User: Tom Byrne(tom.byrne@apple.com)
 * Copyright (C) 2013 Apple Inc.
 * "Code early, Code often."
 */
public class StringUtils {

    /**
     * Parse out all the integers out of a string and return an int value.
     * @param value
     * @return
     */
    public static int parseIntFromString(String value){
        if(null == value || value.length() < 1){
            return Integer.MIN_VALUE;
        }
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < value.length(); i ++){
            char c = value.charAt(i);
            if(Character.isDigit(c)){
                sb.append(c);
            }
        }

        int toReturn = Integer.MIN_VALUE;

        try{
            toReturn = Integer.parseInt(sb.toString());
        }catch(NumberFormatException ignore){}

        return toReturn;
    }
}
