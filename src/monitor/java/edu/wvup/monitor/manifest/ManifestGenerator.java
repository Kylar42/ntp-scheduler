package edu.wvup.monitor.manifest;

import edu.wvup.monitor.Entry;
import edu.wvup.monitor.EntryStatus;
import edu.wvup.monitor.Util;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONStyle;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.util.UUID;

/**
 * User: Tom Byrne(kylar42@gmail.com)
 * "Code early, Code often."
 * This class will generate a manifest file that is suitable for consumption by the NTPUpdaterJob.
 * @see edu.wvup.monitor.NTPUpdaterJob
 */
public class ManifestGenerator {

    public ManifestGenerator() {

    }

    /**
     * Main entry - will descend into this directory and create a Manifest file with entries for each subfile and subdirectory.
     * @param directory
     * @param urlRoot
     * @param version
     */
    public void generateFromDirectory(File directory, String urlRoot, int version) {
        final long manifestTime = System.currentTimeMillis();
        final Collection<File> files = recursiveFindFilesFromDir(directory);
        //now we have our canonical list of files. We're going to create our file,
        //called manifest-list.json. It will be a JSON file that contains:
        //a list of each file
        //a hash and size for each file
        //a guid
        //a timestamp of when we created the manifest.
        File toWrite = new File(directory, ManifestConstants.FILENAME);

        String guid = UUID.randomUUID().toString();
        Manifest manifest = new Manifest(version, guid, urlRoot);

        for (File f : files) {
            if(f.equals(toWrite)){
                continue;//ignore a manifest if we see it.
            }
            String path = getPathRelativeTo(directory, f);
            if (f.isDirectory()) {
                String hash = "";//no hash for directories
                Entry dirEntry = new Entry(path, hash, f.lastModified(), 0L,  EntryStatus.DIRECTORY);
                manifest.addEntry(dirEntry);
            } else {
                String hash = Util.md5File(f);
                Entry fileEntry = new Entry(path, hash, f.lastModified(), f.length(), EntryStatus.NEW);
                manifest.addEntry(fileEntry);
            }

        }


        JSONObject rootObject = ManifestTransformer.jsonObjectFromManifest(manifest);

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

        System.out.println("Finished. Manifest was written as: "+toWrite.getAbsolutePath());

    }

    /**
     * Utility method to find a relative path. A bit hacky.
     * TODO: Replace with Path.relativize usages.
     * @param rootDir
     * @param newDir
     * @return
     */
    private String getPathRelativeTo(File rootDir, File newDir) {
        String rootDirStr = rootDir.getAbsolutePath();
        String newDirStr = newDir.getAbsolutePath();

        if (!newDirStr.startsWith(rootDirStr)) {
            System.out.println("Was told that file:" + newDirStr + " was in the path:" + rootDirStr);
            return null;
        }
        return newDirStr.substring(rootDirStr.length());
    }

    /**
     * Recursive method to create an aggregate of all files(but not directories) that exist in all levels below the passed-in directory.
     * @param directory
     * @return
     */
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

    /**
     * Main method so that this can be called via our build system.
     * Does some basic sanity checking on the command line args, then creates our generator.
     * @param args
     */
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

        String newVersion = properties.getProperty(ManifestConstants.VERSION);
        int newVersionInt = Util.intFromStringWithoutThrow(newVersion);
        if(Integer.MIN_VALUE == newVersionInt){
            System.out.println("Unable to parse version - must be an int. Please Specify -Dversion=[INTVALUE]");
            System.exit(-2);
        }
        String urlBase = properties.getProperty("url.base", "http://localhost:8000/");


        new ManifestGenerator().generateFromDirectory(sourceDirectory, urlBase, newVersionInt);

    }
}
