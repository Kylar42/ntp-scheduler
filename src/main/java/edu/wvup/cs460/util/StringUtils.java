package edu.wvup.cs460.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * User: Tom Byrne(tom.byrne@apple.com)
 * Copyright (C) 2013 Apple Inc.
 * "Code early, Code often."
 */
public class StringUtils {

    private static int DEFAULT_BUFFER_SIZE = 4096;//TODO:should be a property, so we can tune
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

    public static String readString(final InputStream input, final Charset charset) throws IOException {
        final byte[] bytes = readBytes(input, 4096);
        return new String(bytes, charset);
    }

    public static byte[] readBytes(final InputStream input, final int bufferSize) throws IOException {
        final ByteArrayOutputStream output = new ByteArrayOutputStream(bufferSize);
        bufferCopy(input, output, bufferSize);
        return output.toByteArray();

    }

    public static void bufferCopy(final InputStream input, final OutputStream output, final int bufferSize) throws IOException {
        final byte[] buffer = new byte[bufferSize];
        int count;
        while((count = input.read(buffer)) != -1) {
            output.write(buffer, 0, count);
        }
    }
}

