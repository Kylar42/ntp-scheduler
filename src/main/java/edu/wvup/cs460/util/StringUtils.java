package edu.wvup.cs460.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.StringTokenizer;

/**
 * User: Tom Byrne(kylar42@gmail.com)
 * "Code early, Code often."
 *
 * A set of convenience methods.
 *
 */
public class StringUtils {


    public static String DATE_FORMAT_STRING = "EEE, dd MMM yyyy HH:mm:ss zzz";

    public static String TABLE_DATE_OUTPUT_STRING = "dd-MMM-yy";

    //utility class, everything is static.
    private StringUtils(){}

    private static int DEFAULT_BUFFER_SIZE = 4096;//TODO:should be a property, so we can tune

    /**
     * Convert a set of bytes into a hexadecimal string. Useful for storing MD5's as strings.
     * @param bytes byte array to convert
     * @return a Hexadecimal string representation of the bytes passed in.
     */
    public static String toHexString(byte[] bytes){
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            sb.append(Integer.toHexString(0xff & aByte));
        }
        return sb.toString();
    }

    /**
     * Parse out all the integers out of a string and return an int value.
     * This will look at each character in a string and if it is parseable as a Character.isDigit,
     * it will be appended to the return integer. For example: a String value of "A1B2C345ZZ\n" will
     * return an int value of 12345.
     * @param value String value to parse.
     * @return the accumulated values of all ints in the passed-in string, in position.
     */
    public static int parseIntegersFromString(String value){
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

    /**
     * Read an inputstream to it's end and return the data as a String.
     * @param input InputStream to read from.
     * @param charset Charset for creating the charset.
     * @return the content as a String
     * @throws IOException
     */
    public static String readString(final InputStream input, final Charset charset) throws IOException {
        final byte[] bytes = readBytes(input, DEFAULT_BUFFER_SIZE);
        return new String(bytes, charset);
    }

    /**
     * Read an inputstream to the end, returning all the bytes.
     * @param input InputStream to read
     * @param bufferSize size of buffer for reading
     * @return the content of an InputStream as a byte array. If this ends up being too big, an OutOfMemoryExcept
     * @throws IOException
     */
    public static byte[] readBytes(final InputStream input, final int bufferSize) throws IOException {
        final ByteArrayOutputStream output = new ByteArrayOutputStream(bufferSize);
        bufferCopy(input, output, bufferSize);
        return output.toByteArray();

    }

    /**
     * Convenience method to copy all the content of one InputStream into an OutputStream.
     * @param input InputStream to copy from
     * @param output OutputStream to copy to.
     * @param bufferSize size of the buffer to use to copy.
     * @throws IOException
     */
    public static void bufferCopy(final InputStream input, final OutputStream output, final int bufferSize) throws IOException {
        final byte[] buffer = new byte[bufferSize];
        int count;
        while((count = input.read(buffer)) != -1) {
            output.write(buffer, 0, count);
        }
    }

    /**
     * Convenence method to split a string into multiple substrings without returning any empty substrings.
     * @param value String value to split
     * @param delimiter Delimiter to split the string at. This can be multiple delimiters in the string, for example: ",: " will split on any of the three characters.
     * @return a String array with the split values.
     * @throws NullPointerException
     */
    public static String[] splitStringWithoutEmpty(final String value, final String delimiter) throws NullPointerException {
        if (null == value || null == delimiter) {
            throw new NullPointerException("Invalid args for split for string: " + value);
        }

        final StringTokenizer tokenizer = new StringTokenizer(value, delimiter, false);

        final String[] result = new String[tokenizer.countTokens()];

        int i = 0;
        while (tokenizer.hasMoreTokens()) {
            result[i++] = tokenizer.nextToken();
        }
        return result;
    }

}

