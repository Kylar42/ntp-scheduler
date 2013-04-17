package edu.wvup.monitor.os;

/**
 * User: Tom Byrne(kylar42@gmail.com)
 * "Code early, Code often."
 */
public class OSUtilFactory {

    public static OSUtils getOSUtilsForOS(OSType type){
        if(OSType.Windows == type){
            return new WindowsUtils();
        }

        //default to Linux for now.
        return new LinuxUtils();
    }
}
