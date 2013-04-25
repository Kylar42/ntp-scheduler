package edu.wvup.monitor.os;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * User: Tom Byrne(kylar42@gmail.com)
 * "Code early, Code often."
 */
class LinuxUtils implements OSUtils {

    private final Set<PosixFilePermission> executeSet = PosixFilePermissions.fromString("rwxr-xr-x");

    @Override
    public OSType getType() {
        return OSType.Linux;
    }

    public List<ProcessInfo> listRunningJavaProcesses() {
        ArrayList<ProcessInfo> toReturn = new ArrayList<ProcessInfo>();
        try {
            String line;
            Process p = Runtime.getRuntime().exec("ps aux");
            BufferedReader input = new BufferedReader
                    (new InputStreamReader(p.getInputStream()));
            while ((line = input.readLine()) != null) {
                if (!line.trim().startsWith("USER  ")) {
                    //kylar          13195   0.0  0.2  2515748  13420   ??  S    Sun08AM   0:08.42 /usr/sbin/cfprefsd agent

                    // keep only the process name
                    String owner = line.substring(0, 15);//don't need this, will remove.
                    String pid = line.substring(15, 21).trim();
                    String processName = line.substring(77);
                    toReturn.add(new ProcessInfo(processName, pid));
                }

            }
            input.close();
        } catch (Exception err) {
            err.printStackTrace();
        }
        return toReturn;
    }

    @Override
    public void stopProcess(ProcessInfo processInfo) {
        String killCmd = "kill -9 " + processInfo.getPid();

        try {
            Runtime.getRuntime().exec(killCmd);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public Process startProcess(String command, File workingDirectory, boolean modIfNecessary) {

        File fullFile = new File(workingDirectory, command);
        ProcessBuilder builder = new ProcessBuilder(fullFile.getAbsolutePath(), "&");
        Process toReturn = null;
        //File out = new File("/tmp/monitor-output.log");
        //File err = new File("/tmp/monitor-error.log");

        try {
            if (!Files.isExecutable(fullFile.toPath()) && modIfNecessary) {
                Files.setPosixFilePermissions(fullFile.toPath(), executeSet);
            }


            builder.directory(workingDirectory);

            //builder.redirectErrorStream();
            //builder.redirectError(err);
            //builder.redirectOutput(out);
            toReturn = builder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return toReturn;
    }
}
