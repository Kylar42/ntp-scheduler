package edu.wvup.monitor.os;

import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.Properties;

/**
 * User: Tom Byrne(kylar42@gmail.com)
 * "Code early, Code often."
 */
public interface OSUtils {

    static Logger LOG = LoggerFactory.getLogger(OSUtils.class);

    public List<ProcessInfo> listRunningJavaProcesses();

    public Process startProcess(String command, File directory);

    public void stopProcess(ProcessInfo processInfo);

    public OSType getType();

    public static class OSUtilsCreator {
        public static OSUtils createOSUtils() {
            final Properties properties = System.getProperties();

            OSType type = getOSType(properties.getProperty("os.name"));

            if (OSType.Unknown == type) {
                LOG.error("Fatal Error: can't find OS Type: " + properties.getProperty("os.name"));
            }

            if (OSType.Windows == type) {
                return new WindowsUtils();
            } else {
                return new LinuxUtils();
            }

        }


        private static OSType getOSType(String osName) {
            if (null == osName) {
                return OSType.Unknown;
            }
            if (osName.contains("Windows")) {
                return OSType.Windows;
            }

            return OSType.Linux;//should work for Mac too.
        }
    }
}
