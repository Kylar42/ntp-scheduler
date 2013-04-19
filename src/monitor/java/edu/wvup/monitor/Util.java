package edu.wvup.monitor;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.Properties;

/**
 * User: Tom Byrne(kylar42@gmail.com)
 * "Code early, Code often."
 */
public class Util {

    public static Properties parsePropsFromCommandLine( final String[] cliArgs) {
        final Properties cliProps = new Properties();

        if (cliArgs != null) {
            for (final String curArg : cliArgs) {
                if (curArg.startsWith("-D")) {
                    final String keyValueString = curArg.substring(2);
                    final int indexOfEquals = keyValueString.indexOf(('='));
                    if (indexOfEquals == -1) {
                        throw new RuntimeException("Invalid parameter definition: " + curArg);
                    }
                    final String keyString = keyValueString.substring(0, indexOfEquals).trim();
                    final String valueString = keyValueString.substring(indexOfEquals + 1).trim();
                    cliProps.put(keyString, valueString);
                }
            }
        }
        return cliProps;
    }

    public static Properties readPropsFile(File propsFile) throws IOException {
        Properties propsToReturn = new Properties();

        propsToReturn.load(new FileInputStream(propsFile));

        return propsToReturn;
    }

    public static String toHexString(byte[] bytes){
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            sb.append(Integer.toHexString(0xff & bytes[i]));
        }
        return sb.toString();
    }

    public static String readContentStream(final InputStream stream, int byteCount){
        byte[] bytes = new byte[byteCount];
        try {
            stream.read(bytes);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return new String(bytes);
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

    public static int intFromStringWithoutThrow(String intVal){
        int toReturn = Integer.MIN_VALUE;
        try {
            toReturn = Integer.parseInt(intVal);
        } catch (NumberFormatException ignore) {}

        return toReturn;
    }

    public static long longFromStringWithoutThrow(String longVal){
        long toReturn = Long.MIN_VALUE;
        try{
            toReturn = Long.parseLong(longVal);
        }catch(NumberFormatException ignore){}
        return toReturn;
    }

    public static void closeIfNotNullWithoutRethrow(OutputStream os){
        if(null != os){
            try{
                os.flush();
                os.close();
            }catch(IOException ignore){}
        }
    }
    public static void closeIfNotNullWithoutRethrow(InputStream is){
        if(null != is){
            try{
                is.close();
            }catch(IOException ignore){}
        }
    }

    public static String md5File(File f) {
        MessageDigest md = null;
        InputStream is = null;
        try {
            md = MessageDigest.getInstance("MD5");
            is = new FileInputStream(f);

            is = new DigestInputStream(is, md);
            // read stream to EOF as normal...
            byte[] buffer = new byte[4096];//allocate a small array.
            while (is.read(buffer) != -1) {}//read file in chunks

        } catch (Throwable ignore) {
            //don't care.
            ignore.printStackTrace();
            return null;

        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException ignore) {
                }
            }
        }
        if (null != md) {
            return toHexString(md.digest());
        }
        return null;
    }
}
