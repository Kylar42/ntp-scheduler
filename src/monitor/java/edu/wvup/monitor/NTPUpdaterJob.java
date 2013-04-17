package edu.wvup.monitor;

import edu.wvup.monitor.os.OSType;
import edu.wvup.monitor.os.OSUtilFactory;
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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Properties;

/**
 * User: Tom Byrne(kylar42@gmail.com)
 * "Code early, Code often."
 */
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class NTPUpdaterJob implements Job {
    Logger LOG = LoggerFactory.getLogger(NTPUpdaterJob.class);
    //We are only going to run on linux or windows.


    private String getRunningNTPVersion(){
        final String urlString = AppProperties.APP_PROPERTIES.getProperty(MonitorProperties.NTP_URL);
        try {
            URL url = new URL(urlString);
            final URLConnection urlConnection = url.openConnection();
            urlConnection.connect();
            final String contentType = urlConnection.getContentType();
            if(null != contentType && contentType.contains("application/json")){
                JSONParser parser = new JSONParser(JSONParser.MODE_PERMISSIVE);
                JSONObject object = (JSONObject)parser.parse(urlConnection.getInputStream());
                final Object version = object.get("version");
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

       return "0";
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        //check for update.
        getRunningNTPVersion();



        final Properties properties = System.getProperties();

        OSType type = getOSType(properties.getProperty("os.name"));

        if(OSType.Unknown == type){
            LOG.error("Fatal Error: can't find OS Type: "+properties.getProperty("os.name"));
            throw new JobExecutionException("Unable to determine OS Type.");
        }

        if(OSType.Windows == type){
            performForWindows(jobExecutionContext);
        }else{
            performForLinux(jobExecutionContext);
        }

    }

    private void performForWindows(JobExecutionContext context){
        //see if it's running.
        OSUtils utils = OSUtilFactory.getOSUtilsForOS(OSType.Windows);
        final List<ProcessInfo> strings = utils.listRunningProcesses();
        ProcessInfo ntpProcess = findProcess("NTPAppServer", strings);

        if(null != ntpProcess){
            AppProperties.APP_PROPERTIES.setPropertyAsBoolean(ManifestConstants.UPDATING, true);
            utils.stopProcess(ntpProcess);
            doUpdate(context);
            AppProperties.APP_PROPERTIES.setPropertyAsBoolean(ManifestConstants.UPDATING, false);
            //didn't find it. Start it up.
            String startCommand = context.getMergedJobDataMap().getString("start.command");
            String startCommandDir = context.getMergedJobDataMap().getString("start.command.dir");
            File scDir = new File(startCommandDir);

            utils.startProcess(startCommand, scDir);
        }

    }
    private void performForLinux(JobExecutionContext context){
        //see if it's running.
        OSUtils utils = OSUtilFactory.getOSUtilsForOS(OSType.Linux);
        final List<ProcessInfo> strings = utils.listRunningProcesses();
        ProcessInfo ntpProcess = findProcess("NTPAppServer", strings);

        if(null != ntpProcess){
            AppProperties.APP_PROPERTIES.setPropertyAsBoolean(ManifestConstants.UPDATING, true);
            utils.stopProcess(ntpProcess);
            doUpdate(context);
            AppProperties.APP_PROPERTIES.setPropertyAsBoolean(ManifestConstants.UPDATING, false);
            //didn't find it. Start it up.
            String startCommand = context.getMergedJobDataMap().getString("start.command");
            String startCommandDir = context.getMergedJobDataMap().getString("start.command.dir");
            File scDir = new File(startCommandDir);

            utils.startProcess(startCommand, scDir);
        }


    }


    private void doUpdate(JobExecutionContext context){

    }
    private ProcessInfo findProcess(String uniqueProcessString, List<ProcessInfo> infos){
        for(ProcessInfo pi : infos){
            if(pi.getProcessName().contains(uniqueProcessString)){
                return pi;
            }
        }

        return null;
    }

    private OSType getOSType(String osName){
        if(null == osName){
            return OSType.Unknown;
        }
        if(osName.contains("Windows")){
            return OSType.Windows;
        }

        return OSType.Linux;//should work for Mac too.
    }


    public static void scheduleNTPUpdateJob() throws SchedulerException{
        String serverWatchJobName = AppProperties.APP_PROPERTIES.getProperty("serverupdate.job.name", "serverUpdater");
        String serverWatchJobGroup = AppProperties.APP_PROPERTIES.getProperty("serverupdate.job.group", "serverUpdaterGroup");
        String serverWatchJobTriggerName = AppProperties.APP_PROPERTIES.getProperty("serverupdate.job.trigger.name", "serverWatchTrigger");

        final String startCommand = AppProperties.APP_PROPERTIES.getProperty("ntpserver.command", "command.sh");
        final String startCommandDir = AppProperties.APP_PROPERTIES.getProperty("ntpserver.command.dir", "..");

        JobDetail job = JobBuilder.newJob(NTPUpdaterJob.class).withIdentity(serverWatchJobName, serverWatchJobName).build();

        job.getJobDataMap().put("start.command", startCommand);
        job.getJobDataMap().put("start.command.dir", startCommandDir);

        final SimpleScheduleBuilder simpleScheduleBuilder =
                SimpleScheduleBuilder.simpleSchedule().withIntervalInMinutes(1).repeatForever();

        Trigger trigger = TriggerBuilder.newTrigger().withIdentity(serverWatchJobTriggerName,serverWatchJobGroup).startNow().withSchedule(simpleScheduleBuilder).build();

        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        scheduler.scheduleJob(job, trigger);

    }
}
