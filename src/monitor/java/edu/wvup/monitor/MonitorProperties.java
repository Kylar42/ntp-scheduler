package edu.wvup.monitor;

/**
 * User: Tom Byrne(kylar42@gmail.com)
 * "Code early, Code often."
 */
public class MonitorProperties {
    public static final String NTP_START_COMMAND = "ntpserver.command.";//this has an extra dot on the end because we're going to add the OS type.
    public static final String NTP_START_DIRECTORY = "ntpserver.command.dir";
    public static final String NTP_URL = "ntpserver.url";
    public static final String NTP_UPDATE_URL = "ntpupdate.url";

    public static final String NTP_UNIQUE_STRING = "ntp.process.string";

}
