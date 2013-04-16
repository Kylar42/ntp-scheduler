package edu.wvup.monitor;

import java.net.URL;
import java.util.Properties;

/**
 * User: Tom Byrne(kylar42@gmail.com)
 * "Code early, Code often."
 */
public class Manifest {
    public Manifest(URL manifestFile){

    }

    //main method for testing.
    public static void main(String[] args) throws Exception{
        new Manifest(new URL("file://source/personal/ntp-scheduler/dist/manifest-list.json"));


    }
}
