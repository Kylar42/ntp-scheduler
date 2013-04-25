package edu.wvup.monitor;

import edu.wvup.monitor.manifest.ManifestConstants;
import edu.wvup.monitor.os.OSType;
import edu.wvup.monitor.os.OSUtilFactory;
import edu.wvup.monitor.os.OSUtils;
import edu.wvup.monitor.os.ProcessInfo;
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
import java.util.List;
import java.util.Properties;

/**
 * User: Tom Byrne(kylar42@gmail.com)
 * "Code early, Code often."
 */
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class NTPWatchJob implements Job {
    Logger LOG = LoggerFactory.getLogger(NTPWatchJob.class);
    //We are only going to run on linux or windows.


    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        //check if we're updating
        if (AppProperties.APP_PROPERTIES.getPropertyAsBoolean(ManifestConstants.UPDATING, false)) {
            LOG.info("Was asked to check for running process, but UPDATE flag was set. Returning.");
            return;
        }

        final Properties properties = System.getProperties();
        OSType type = getOSType(properties.getProperty("os.name"));


        if (OSType.Unknown == type) {
            LOG.error("Fatal Error: can't find OS Type: " + properties.getProperty("os.name"));
            throw new JobExecutionException("Unable to determine OS Type.");
        }

        if (OSType.Windows == type) {
            performForWindows(jobExecutionContext);
        } else {
            performForLinux(jobExecutionContext);
        }

    }

    private void performForWindows(JobExecutionContext context) {


        //see if it's running.
        OSUtils utils = OSUtilFactory.getOSUtilsForOS(OSType.Windows);
        final List<ProcessInfo> strings = utils.listRunningJavaProcesses();
        ProcessInfo ntpProcess = findProcess("NTPAppServer", strings);

        if (null == ntpProcess) {
            //didn't find it. Start it up.
            String startCommand = AppProperties.APP_PROPERTIES.getProperty(MonitorProperties.NTP_START_COMMAND + utils.getType().name());
            String startCommandDir = AppProperties.APP_PROPERTIES.getProperty(MonitorProperties.NTP_START_DIRECTORY);
            File scDir = new File(startCommandDir);

            utils.startProcess(startCommand, scDir, true);
        }

    }

    private void performForLinux(JobExecutionContext context) {
        //see if it's running.
        OSUtils utils = OSUtilFactory.getOSUtilsForOS(OSType.Linux);
        final List<ProcessInfo> strings = utils.listRunningJavaProcesses();
        ProcessInfo ntpProcess = findProcess("NTPAppServer", strings);

        if (null == ntpProcess) {
            //didn't find it. Start it up.
            String startCommand = AppProperties.APP_PROPERTIES.getProperty(MonitorProperties.NTP_START_COMMAND + utils.getType().name());
            String startCommandDir = AppProperties.APP_PROPERTIES.getProperty(MonitorProperties.NTP_START_DIRECTORY);
            File scDir = new File(startCommandDir);

            utils.startProcess(startCommand, scDir, true);
        }


    }

    private ProcessInfo findProcess(String uniqueProcessString, List<ProcessInfo> infos) {
        for (ProcessInfo pi : infos) {
            if (pi.getProcessName().contains(uniqueProcessString)) {
                return pi;
            }
        }

        return null;
    }

    private OSType getOSType(String osName) {
        if (null == osName) {
            return OSType.Unknown;
        }
        if (osName.contains("Windows")) {
            return OSType.Windows;
        }

        return OSType.Linux;//should work for Mac too.
    }


    public static void scheduleNTPWatchJob() throws SchedulerException {

        String serverWatchJobName = AppProperties.APP_PROPERTIES.getProperty("serverwatch.job.name", "serverWatch");
        String serverWatchJobGroup = AppProperties.APP_PROPERTIES.getProperty("serverwatch.job.group", "serverWatchGroup");
        String serverWatchJobTriggerName = AppProperties.APP_PROPERTIES.getProperty("serverwatch.job.trigger.name", "serverWatchTrigger");

        JobDetail job = JobBuilder.newJob(NTPWatchJob.class).withIdentity(serverWatchJobName, serverWatchJobName).build();


        final SimpleScheduleBuilder simpleScheduleBuilder =
                SimpleScheduleBuilder.simpleSchedule().withIntervalInMinutes(1).repeatForever();

        Trigger trigger = TriggerBuilder.newTrigger().withIdentity(serverWatchJobTriggerName, serverWatchJobGroup).startNow().withSchedule(simpleScheduleBuilder).build();

        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        scheduler.scheduleJob(job, trigger);

    }
}
