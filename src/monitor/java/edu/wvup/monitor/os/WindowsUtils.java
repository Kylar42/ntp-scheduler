package edu.wvup.monitor.os;

import java.io.*;
import java.util.*;
/**
 * User: Tom Byrne(kylar42@gmail.com)
 * "Code early, Code often."
 */

class WindowsUtils implements OSUtils{

    @Override
    public OSType getType() {
        return OSType.Windows;
    }

    @Override
    public void stopProcess(ProcessInfo processInfo) {
        //taskkill /IM
        String killCmd = "taskkill /IM  "+processInfo.getPid();

        ProcessBuilder builder = new ProcessBuilder(killCmd);
        File out = new File(".", "kill-output.log");
        File err = new File(".", "kill-error.log");

        try
        {
            builder.redirectErrorStream();
            //builder.redirectError(err);
            //builder.redirectOutput(out);
            builder.start();
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
        }
    }

    public List<ProcessInfo> listRunningProcesses() {
        List<ProcessInfo> toReturn = new ArrayList<ProcessInfo>();
        try {
            String line;
            Process p = Runtime.getRuntime().exec("tasklist.exe /fo csv /nh");
            BufferedReader input = new BufferedReader
                    (new InputStreamReader(p.getInputStream()));
            while ((line = input.readLine()) != null) {
                if (!line.trim().equals("")) {
                    // keep only the process name
                    line = line.substring(1);
                    String processName = line.substring(0, line.indexOf("\""));
                    toReturn.add(new ProcessInfo(processName, ""));//no PID.
                }

            }
            input.close();
        }
        catch (Exception err) {
            err.printStackTrace();
        }
        return toReturn;
    }

    @Override
    public Process startProcess(String command, File workingDirectory) {
        ProcessBuilder builder = new ProcessBuilder(command);
        Process toReturn = null;
        File out = new File(workingDirectory, "monitor-output.log");
        File err = new File(workingDirectory, "monitor-error.log");

        try
        {
            builder.directory(workingDirectory);

            builder.redirectErrorStream();
            //builder.redirectError(err);
            //builder.redirectOutput(out);
            toReturn = builder.start();
            System.out.println("Started.");
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
        }
        return toReturn;
    }
}
