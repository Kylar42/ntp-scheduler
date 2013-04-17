package edu.wvup.monitor.os;

/**
 * User: Tom Byrne(kylar42@gmail.com)
 * "Code early, Code often."
 */
public class ProcessInfo {

    private final String processName;
    private final String pid;

    public ProcessInfo(String processName, String pid) {
        this.processName = processName;
        this.pid = pid;
    }

    public String getProcessName() {
        return processName;
    }

    public String getPid() {
        return pid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ProcessInfo that = (ProcessInfo) o;

        if (pid != null ? !pid.equals(that.pid) : that.pid != null) {
            return false;
        }
        if (processName != null ? !processName.equals(that.processName) : that.processName != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = processName != null ? processName.hashCode() : 0;
        result = 31 * result + (pid != null ? pid.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ProcessInfo{" +
                "processName='" + processName + '\'' +
                ", pid='" + pid + '\'' +
                '}';
    }
}
