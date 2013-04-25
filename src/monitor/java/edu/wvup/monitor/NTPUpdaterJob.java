package edu.wvup.monitor;

import edu.wvup.monitor.manifest.Manifest;
import edu.wvup.monitor.manifest.ManifestConstants;
import edu.wvup.monitor.manifest.ManifestDownloader;
import edu.wvup.monitor.manifest.ManifestParser;
import edu.wvup.monitor.manifest.Updater;
import edu.wvup.monitor.os.OSUtils;
import edu.wvup.monitor.os.ProcessInfo;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

/**
 * User: Tom Byrne(kylar42@gmail.com)
 * "Code early, Code often."
 */
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class NTPUpdaterJob implements Job {
    private static Logger LOG = LoggerFactory.getLogger(NTPUpdaterJob.class);

    private String getRunningNTPVersion() {
        final String urlString = AppProperties.APP_PROPERTIES.getProperty(MonitorProperties.NTP_URL);
        try {
            URL url = new URL(urlString);
            final URLConnection urlConnection = url.openConnection();
            urlConnection.connect();
            final String contentType = urlConnection.getContentType();
            if (null != contentType && contentType.contains("application/json")) {
                JSONParser parser = new JSONParser(JSONParser.MODE_PERMISSIVE);
                JSONObject object = (JSONObject) parser.parse(urlConnection.getInputStream());
                final Object version = object.get("app.version");
                return null == version ? "0" : version.toString();
            }
        } catch (Throwable e) {
            LOG.error("Unable to fetch data. Is the app running?", e);
        }

        return "0";
    }

    private Manifest getNewNTPVersion() {
        final String urlString = AppProperties.APP_PROPERTIES.getProperty(MonitorProperties.NTP_UPDATE_URL);
        try {
            return ManifestParser.parseFromURL(new URL(urlString));
        } catch (MalformedURLException e) {
            LOG.error("Was passed a bad URL in properties:" + urlString);
        }
        return null;
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        OSUtils osUtils = OSUtils.OSUtilsCreator.createOSUtils();

        //check for update.
        perform(jobExecutionContext, osUtils);


    }

    private void perform(JobExecutionContext context, OSUtils utils) {

        final String ntpProcessString = AppProperties.APP_PROPERTIES.getProperty(MonitorProperties.NTP_UNIQUE_STRING, "NTPAppServer");
        //see if it's running.
        final List<ProcessInfo> strings = utils.listRunningJavaProcesses();
        ProcessInfo ntpProcess = findProcess(ntpProcessString, strings);
        if (null != ntpProcess) {
            String currentVersionStr = getRunningNTPVersion();
            Manifest manifest = getNewNTPVersion();
            if (null == manifest) {
                //something bad happened, but we should have logged it already.
                return;
            }

            int newVersion = manifest.getVersion();

            if (null == currentVersionStr) {
                LOG.info("Wasn't able to proceed with NTPUpdater. Invalid versions - Current:{} New: {}", currentVersionStr, newVersion);
                return;
            }

            int oldVersion = 0;
            try {
                oldVersion = Integer.parseInt(currentVersionStr);
            } catch (NumberFormatException ignore) {
                //ignored by design - tbyrne
            }

            if (newVersion <= oldVersion) {
                return;//we're up to date.
            }
            //If we get here, we need to actually update
            AppProperties.APP_PROPERTIES.setPropertyAsBoolean(ManifestConstants.UPDATING, true);
            try {

                doUpdate(context, manifest);


            } finally {
                AppProperties.APP_PROPERTIES.setPropertyAsBoolean(ManifestConstants.UPDATING, false);
            }
        }


    }




    private void doUpdate(JobExecutionContext context, Manifest manifest) {
        //Ok, we have the manifest and we know what to do.
        final String ntpRunningDir = AppProperties.APP_PROPERTIES.getProperty(MonitorProperties.NTP_START_DIRECTORY);
        final String ntpProcessString = AppProperties.APP_PROPERTIES.getProperty(MonitorProperties.NTP_UNIQUE_STRING, "NTPAppServer");

        File runningDir = new File(".");
        String newDirName = new StringBuilder("manifest-download-").append(manifest.getVersion()).append("-").append(manifest.getGuid()).toString();
        File newDir = new File(runningDir, newDirName);

        ManifestDownloader downloader = new ManifestDownloader(manifest, newDir);
        boolean successful = downloader.performDownload();
        if (successful) {
            File stagingDir = downloader.getStagingDirectory();
            stopAllNTPServerProcesses(ntpProcessString);
            try{
                Updater updater = new Updater(stagingDir, new File(ntpRunningDir));
                updater.performUpdate();
            }catch(Throwable t){
                t.printStackTrace();
                //Ok, if we got here on an uncaught exception, something went wrong, and it's possible that we didn't copy all the files, or left things in a corrupt state.
                //this is probably our worst possible scenario. We need to pop up a modal alert so that someone can see it.
                LOG.error("Worst possible thing happened. Contact the system administrator or Tom Byrne at kylar42@gmail.com.");
                JOptionPane.showMessageDialog(null, "Fatal Error occurred during update. Contact Tom Byrne immediately!", "Fatal Update Error", JOptionPane.ERROR_MESSAGE);
            }finally{
                startNTPProcess(context);//we want to re-start, almost for sure.
            }

        }

    }




    //---------------------------------------------------------------Utility Methods for starting and stopping the NTPServer

    private ProcessInfo findProcess(String uniqueProcessString, List<ProcessInfo> infos) {
        for (ProcessInfo pi : infos) {
            if (pi.getProcessName().contains(uniqueProcessString)) {
                return pi;
            }
        }
        return null;
    }

    private void startNTPProcess(JobExecutionContext context) {
        OSUtils utils = OSUtils.OSUtilsCreator.createOSUtils();
        String startCommand = context.getMergedJobDataMap().getString("start.command");
        String startCommandDir = context.getMergedJobDataMap().getString("start.command.dir");
        File scDir = new File(startCommandDir);

        utils.startProcess(startCommand, scDir, true);
    }

    private boolean stopAllNTPServerProcesses(String uniqueProcessName) {
        OSUtils osUtils = OSUtils.OSUtilsCreator.createOSUtils();
        List<ProcessInfo> processes = osUtils.listRunningJavaProcesses();
        ProcessInfo currentProcess = findProcess(uniqueProcessName, processes);
        int safetyCounter = 0;
        while (null != currentProcess && safetyCounter < 10) {//if we loop through 10 times, something has gone horribly wrong.
            osUtils.stopProcess(currentProcess);
            //here I've chosen to get a new process list, despite the overhead, to make sure that there are no issues with
            //deadlocks.
            processes = osUtils.listRunningJavaProcesses();
            currentProcess = findProcess(uniqueProcessName, processes);
            safetyCounter++;
        }
        return (safetyCounter < 10);//if we looped through in less than 10, we exited because there were no more running processes.

    }


    //----------------------------------------------------------------------------------Static method, called to set up the job.
    public static void scheduleNTPUpdateJob() throws SchedulerException {
        OSUtils osUtils = OSUtils.OSUtilsCreator.createOSUtils();
        String serverWatchJobName = AppProperties.APP_PROPERTIES.getProperty("serverupdate.job.name", "serverUpdater");
        String serverWatchJobGroup = AppProperties.APP_PROPERTIES.getProperty("serverupdate.job.group", "serverUpdaterGroup");
        String serverWatchJobTriggerName = AppProperties.APP_PROPERTIES.getProperty("serverupdate.job.trigger.name", "serverWatchTrigger");

        final String startCommand = AppProperties.APP_PROPERTIES.getProperty("ntpserver.command."+osUtils.getType().toString(), "command.sh");
        final String startCommandDir = AppProperties.APP_PROPERTIES.getProperty("ntpserver.command.dir", "../ntp-server");

        final int intervalForUpdateJob = AppProperties.APP_PROPERTIES.getPropertyAsInt("ntpserver.update.interval.inminutes", 1);

        JobDetail job = JobBuilder.newJob(NTPUpdaterJob.class).withIdentity(serverWatchJobName, serverWatchJobName).build();

        job.getJobDataMap().put("start.command", startCommand);
        job.getJobDataMap().put("start.command.dir", startCommandDir);

        final SimpleScheduleBuilder simpleScheduleBuilder =
                SimpleScheduleBuilder.simpleSchedule().withIntervalInMinutes(1).repeatForever();

        Trigger trigger = TriggerBuilder.newTrigger().withIdentity(serverWatchJobTriggerName, serverWatchJobGroup).startNow().withSchedule(simpleScheduleBuilder).build();

        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        scheduler.scheduleJob(job, trigger);

    }
}
