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
        String killCmd = "taskkill /PID  "+processInfo.getPid();

        ProcessBuilder builder = new ProcessBuilder("taskkill", "/F", "/PID", processInfo.getPid());
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

    public List<ProcessInfo> listRunningJavaProcesses() {
        List<ProcessInfo> toReturn = new ArrayList<ProcessInfo>();
        try {
            String line;
            Process p = Runtime.getRuntime().exec("wmic PROCESS where \"name like '%java%'\" get Processid,Caption,Commandline");
            BufferedReader input = new BufferedReader
                    (new InputStreamReader(p.getInputStream()));
            while ((line = input.readLine()) != null) {
                if (line.startsWith("java.exe")) {
                    // keep only the process name
                    //trim
                    line = line.trim();
                    //let's get the end part, after the 'java.exe'
                    int lastIndex = line.lastIndexOf(' ');
                    if(-1 == lastIndex){
                        continue;
                    }
                    String pid = line.substring(lastIndex).trim();
                    String process = line.substring(0,lastIndex).trim();

                    toReturn.add(new ProcessInfo(process, pid));
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
        Process toReturn = null;
        //File out = new File(workingDirectory, "monitor-output.log");
        //File err = new File(workingDirectory, "monitor-error.log");


        File fullFile = new File(workingDirectory, command);
        //String fullExec = "cmd /c start "+fullFile.getAbsolutePath();
        ProcessBuilder builder = new ProcessBuilder("cmd", "/c", "start", fullFile.getAbsolutePath());

        try
        {
            //Runtime.getRuntime().exec(fullExec, new String[0], fullFile.getParentFile());

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

    public static void  main(String[] args){
        WindowsUtils windowsUtils = new WindowsUtils();
        List<ProcessInfo> processInfos = windowsUtils.listRunningJavaProcesses();
        for(ProcessInfo pi : processInfos){
            if(pi.getProcessName().contains("NTPAppServer")){
                windowsUtils.stopProcess(pi);
            }
        }
    }
}
