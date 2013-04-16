package edu.wvup.monitor;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONStyle;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.util.UUID;

/**
 * User: Tom Byrne(kylar42@gmail.com)
 * "Code early, Code often."
 */
public class ManifestGenerator {

    public ManifestGenerator() {

    }

    public void generateFromDirectory(File directory, String urlRoot) {
        final long manifestTime = System.currentTimeMillis();
        final Collection<File> files = recursiveFindFilesFromDir(directory);
        //now we have our canonical list of files. We're going to create our file,
        //called manifest-list.json. It will be a JSON file that contains:
        //a list of each file
        //a hash and size for each file
        //a guid
        //a timestamp of when we created the manifest.
        File toWrite = new File(directory, ManifestConstants.FILENAME);

        ArrayList<Entry> entries = new ArrayList<Entry>();
        for (File f : files) {
            if(f.equals(toWrite)){
                continue;//ignore a manifest if we see it.
            }
            String path = getPathRelativeTo(directory, f);
            if (f.isDirectory()) {
                String hash = "";//no hash for directories
                Entry dirEntry = new Entry(path, hash, f.lastModified(), 0L,  EntryStatus.NEW);
                entries.add(dirEntry);
            } else {
                String hash = md5File(f);
                Entry fileEntry = new Entry(path, hash, f.lastModified(), f.length(), EntryStatus.NEW);
                entries.add(fileEntry);
            }

        }


        //OK, we've got our entries! Let's write em out.


        JSONObject rootObject = new JSONObject();
        rootObject.put(ManifestConstants.TIME, manifestTime);
        rootObject.put(ManifestConstants.GUID, UUID.randomUUID().toString());//unique identifier for this manifest.
        rootObject.put(ManifestConstants.URLROOT, urlRoot);

        //let's get that array
        JSONArray array = new JSONArray();
        for (Entry entry : entries) {
            array.add(objectFromEntry(entry));
        }

        rootObject.put(ManifestConstants.ENTRIES, array);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(toWrite);
            fos.write(rootObject.toString(JSONStyle.NO_COMPRESS).getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != fos) {
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException ignore) {
                }
            }
        }


    }

    private JSONObject objectFromEntry(Entry entry) {
        JSONObject objToReturn = new JSONObject();
        objToReturn.put(ManifestConstants.FILE, entry.getRelativeFile());
        objToReturn.put(ManifestConstants.HASH, entry.getHash());
        objToReturn.put(ManifestConstants.TIME, entry.getTime());
        objToReturn.put(ManifestConstants.SIZE, entry.getSize());
        objToReturn.put(ManifestConstants.TYPE, entry.getStatus().toString());
        return objToReturn;
    }

    private String md5File(File f) {
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
            return Util.toHexString(md.digest());
        }
        return null;
    }

    private String getPathRelativeTo(File rootDir, File newDir) {
        String rootDirStr = rootDir.getAbsolutePath();
        String newDirStr = newDir.getAbsolutePath();

        if (!newDirStr.startsWith(rootDirStr)) {
            System.out.println("Was told that file:" + newDirStr + " was in the path:" + rootDirStr);
            return null;
        }
        return newDirStr.substring(rootDirStr.length());
    }

    private Collection<File> recursiveFindFilesFromDir(File directory) {
        ArrayList<File> toReturn = new ArrayList<File>();
        for (File f : directory.listFiles()) {
            toReturn.add(f);//so if it's a directory, it will come before it's children.
            if (f.isDirectory()) {
                toReturn.addAll(recursiveFindFilesFromDir(f));
            }
        }
        return toReturn;
    }

    public static void main(String args[]) {
        final Properties properties = Util.parsePropsFromCommandLine(args);
        final String sourceDir = properties.getProperty("source.dir");
        if (null == sourceDir) {
            System.out.println("No source directory. use -Dsource.dir=path/to/directory");
            System.exit(-1);
        }

        File sourceDirectory = new File(sourceDir);
        if (!sourceDirectory.exists()) {
            System.out.println("Specified source directory doesn't exist:" + sourceDirectory.getAbsolutePath());
            System.exit(-1);
        }

        if (!sourceDirectory.isDirectory()) {
            System.out.println("Specified source.dir argument existed, but wasn't a directory:" + sourceDirectory.getAbsolutePath());
            System.exit(-1);
        }

        if (!sourceDirectory.canWrite()) {
            if (!sourceDirectory.isDirectory()) {
                System.out.println("Specified source.dir is available, but don't have write permission." + sourceDirectory.getAbsolutePath());
                System.exit(-1);
            }
        }


        //Woohoo! Let's generate our "manifest".

        new ManifestGenerator().generateFromDirectory(sourceDirectory, "http://localhost:9000/goodstuff/");

    }
}
