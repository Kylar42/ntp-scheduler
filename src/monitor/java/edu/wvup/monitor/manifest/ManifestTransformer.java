package edu.wvup.monitor.manifest;

import edu.wvup.monitor.Entry;
import edu.wvup.monitor.EntryStatus;
import edu.wvup.monitor.Util;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

/**
 * User: Tom Byrne(kylar42@gmail.com)
 * "I code not because I have a problem to solve, but because there is
 * code within me, crying to get out."
 */
public class ManifestTransformer {


    private static Entry entryFromJsonObject(JSONObject object) {
        String file = object.get(ManifestConstants.FILE).toString();
        String hash = object.get(ManifestConstants.HASH).toString();
        String time = object.get(ManifestConstants.TIME).toString();
        String size = object.get(ManifestConstants.SIZE).toString();
        String type = object.get(ManifestConstants.TYPE).toString();
        EntryStatus statType = EntryStatus.valueOf(type);
        return new Entry(file, hash,
                         Util.longFromStringWithoutThrow(time),
                         Util.longFromStringWithoutThrow(size),
                         statType);


    }

    public static Manifest manifestFromJSonObject(JSONObject parsedObject) {
        String versionStr = "";
        String guid = "";
        String urlRoot = "";

        Object tmp = parsedObject.get(ManifestConstants.VERSION);
        if (null != tmp) {
            versionStr = tmp.toString();
        }

        tmp = parsedObject.get(ManifestConstants.GUID);
        if (null != tmp) {
            guid = tmp.toString();
        }
        tmp = parsedObject.get(ManifestConstants.URLROOT);
        if (null != tmp) {
            urlRoot = tmp.toString();
        }

        Manifest toReturn = new Manifest(Util.intFromStringWithoutThrow(versionStr), guid, urlRoot);

        tmp = parsedObject.get(ManifestConstants.ENTRIES);
        if (tmp instanceof JSONArray) {
            JSONArray entries = (JSONArray) tmp;
            for (Object tmpEntry : entries) {
                if (tmpEntry instanceof JSONObject) {
                    Entry e = entryFromJsonObject((JSONObject) tmpEntry);
                    if (null != e) {
                        toReturn.addEntry(e);
                    }
                }
            }
        }
        return toReturn;
    }

    public static JSONObject jsonObjectFromManifest(Manifest manifest){
            JSONObject rootObject = new JSONObject();
                    rootObject.put(ManifestConstants.TIME, manifest.getTimestamp());
                    rootObject.put(ManifestConstants.GUID, manifest.getGuid());//unique identifier for this manifest.
                    rootObject.put(ManifestConstants.URLROOT, manifest.getUrlRoot());
                    rootObject.put(ManifestConstants.VERSION, manifest.getVersion());
                     //let's get that array
                    JSONArray array = new JSONArray();
                    for (Entry entry : manifest.getEntries()) {
                        array.add(jsonObjectFromEntry(entry));
                    }
                    rootObject.put(ManifestConstants.ENTRIES, array);
            return rootObject;
        }

        private static JSONObject jsonObjectFromEntry(Entry entry) {
            JSONObject objToReturn = new JSONObject();
            objToReturn.put(ManifestConstants.FILE, entry.getRelativeFile());
            objToReturn.put(ManifestConstants.HASH, entry.getHash());
            objToReturn.put(ManifestConstants.TIME, entry.getTime());
            objToReturn.put(ManifestConstants.SIZE, entry.getSize());
            objToReturn.put(ManifestConstants.TYPE, entry.getStatus().toString());
            return objToReturn;
        }
}
