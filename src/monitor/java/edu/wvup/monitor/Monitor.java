package edu.wvup.monitor;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Properties;

/**
 * User: Tom Byrne(kylar42@gmail.com)
 * "Code early, Code often."
 */
public class Monitor {

    public Monitor(String[] args) {
        AppProperties.APP_PROPERTIES.initPropertiesFromCommandLine(args);
        //set up the scheduler.
        initProperties();
        initScheduler();

    }

    private void initScheduler() {
        try {
            // Grab the Scheduler instance from the Factory
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

            // and start it off
            scheduler.start();

            //NTPWatchJob.scheduleNTPWatchJob();
            NTPUpdaterJob.scheduleNTPUpdateJob();
        } catch (SchedulerException se) {
            se.printStackTrace();
        }
    }

    private void initProperties() {
        //initialize System properties for the Scheduler.

        System.setProperty("org.quartz.scheduler.instanceName", "NTPWatcher");
        System.setProperty("org.quartz.threadPool.threadCount", "2");
        System.setProperty("org.quartz.jobStore.class", "org.quartz.simpl.RAMJobStore");// keep in RAM, as we will start a new one when the server starts.

    }


    public static void main(String[] args) {
        new Monitor(args);
    }
}
