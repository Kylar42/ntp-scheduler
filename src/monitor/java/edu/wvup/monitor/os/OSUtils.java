package edu.wvup.monitor.os;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

/**
 * User: Tom Byrne(kylar42@gmail.com)
 * "Code early, Code often."
 */
public interface OSUtils {

    static Logger LOG = LoggerFactory.getLogger(OSUtils.class);

    public List<ProcessInfo> listRunningProcesses();
    public Process startProcess(String command, File directory);
    public void stopProcess(ProcessInfo processInfo);
    public OSType getType();

}
