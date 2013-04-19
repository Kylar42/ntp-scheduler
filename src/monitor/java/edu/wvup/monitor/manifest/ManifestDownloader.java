package edu.wvup.monitor.manifest;

import edu.wvup.monitor.Entry;
import edu.wvup.monitor.EntryStatus;
import edu.wvup.monitor.Util;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * User: Tom Byrne(kylar42@gmail.com)
 * "I code not because I have a problem to solve, but because there is
 * code within me, crying to get out."
 * TODO: Write a file inside the tmp dir to track what's been downloaded.
 */
public class ManifestDownloader implements Runnable {
    private static Logger LOG = LoggerFactory.getLogger(ManifestDownloader.class);

    private File _tempDirForFiles;
    private Manifest _manifest;

    private boolean _running = true;

    public ManifestDownloader(Manifest toWorkOn, File tmpDir) {
        _manifest = toWorkOn;
        _tempDirForFiles = tmpDir;
    }

    public void run() {
        //Ok, here we need to:
        //delete the contents of the directory.
        if (!_tempDirForFiles.exists()) {
            if (!_tempDirForFiles.mkdirs()) {
                //deal with error? TODO: Decide if this will actually be run in a thread.
            }
        } else {
            recursiveDeleteChildren(_tempDirForFiles, false);
        }

        //should exist and be happy.

        //write the manifest for safe-keeping.
        writeManifest(_tempDirForFiles, _manifest);

        //iterate the entries and download them one at a time, checking hashes.
        for(Entry e : _manifest.getEntries()){
            if(EntryStatus.DIRECTORY != e.getStatus()){
                try {
                    String hash = downloadEntryToDir(_tempDirForFiles, e, _manifest.getUrlRoot());
                    //check if the has was the same.
                    if(e.getHash() != null){
                        if(hash == null || !hash.equals(e.getHash())){
                            LOG.error("Hashes for the entry:{} didn't match what was in the Manifest.", e.toString());
                            throw new ManifestException("Hash value for file didn't match, possible corruption. Don't continue.");
                        }
                    }
                } catch (ManifestException e1) {
                    LOG.error("Unable to download entry in manifest:{}", e.getRelativeFile());
                }
            }
        }


    }

    private String downloadEntryToDir(File rootDir, Entry e, String urlRoot) throws ManifestException{
        //make sure URLRoot doesn't end with a slash if entry start ends with a slash.

        File toWrite = createFileToWriteTo(rootDir, e);

        if(e.getRelativeFile().startsWith("/") && urlRoot.endsWith("/")){
            urlRoot = urlRoot.substring(0, urlRoot.lastIndexOf("/"));
        }
        String fullURL = urlRoot+e.getRelativeFile();
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        }catch(NoSuchAlgorithmException nsa){
            throw new ManifestException(nsa);
        }
        URL toFetch = null;
        InputStream inputStream = null;
        try {
            toFetch = new URL(fullURL);
            toFetch.openConnection();
            inputStream = toFetch.openStream();
            inputStream = new DigestInputStream(inputStream, md);
            writeData(inputStream, toWrite);
            return Util.toHexString(md.digest());
        } catch (IOException e1) {
            throw new ManifestException(e1);
        }finally {
            Util.closeIfNotNullWithoutRethrow(inputStream);
        }
    }

    private void writeData(InputStream urlInputStream, File toWriteTo) throws ManifestException{
        if(null == urlInputStream || null == toWriteTo){
            LOG.error("Wasn't able to write from inputstream to file:{}", toWriteTo);
        }

        //make sure that the file can be written to.
        toWriteTo.getParentFile().mkdirs();
        FileOutputStream fos = null;
        try {
             fos = new FileOutputStream(toWriteTo);
            Util.bufferCopy(urlInputStream, fos, 8192);//copy it out.
        } catch (IOException e) {
            LOG.error("Error occurred trying to write to file: {}", toWriteTo, e.getMessage());
        }finally {
            Util.closeIfNotNullWithoutRethrow(fos);
        }
    }

    /**
     * Return the appropriate file in this filesystem to write this entry to.
     * @param rootDir
     * @param e
     * @return
     */
    private File createFileToWriteTo(File rootDir, Entry e){
        File toReturn = new File(rootDir, e.getRelativeFile());
        return toReturn;
    }

    private void writeManifest(File directory, Manifest manifest) {
        File outputFile = new File(directory, ManifestConstants.FILENAME);
        JSONObject objectToWrite = ManifestTransformer.jsonObjectFromManifest(manifest);
        String stringManifest = objectToWrite.toJSONString(JSONStyle.NO_COMPRESS);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(outputFile);

            fos.write(stringManifest.getBytes());

        } catch (IOException ioe) {
            LOG.error("Unable to write file:", ioe);
        } finally {
            Util.closeIfNotNullWithoutRethrow(fos);
        }
    }

    private void recursiveDeleteChildren(File f, boolean deleteThisFile) {
        if (f.isDirectory()) {
            for (File kid : f.listFiles()) {
                recursiveDeleteChildren(kid, true);
            }
        } else {
            if (f.exists()) {
                f.delete();
            }
        }
    }


}
