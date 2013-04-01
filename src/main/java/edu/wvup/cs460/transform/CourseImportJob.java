package edu.wvup.cs460.transform;

import edu.wvup.cs460.NTPAppServer;
import edu.wvup.cs460.configuration.AppProperties;
import edu.wvup.cs460.db.CourseSeeder;
import edu.wvup.cs460.db.DBContext;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.ScheduleBuilder;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

/**
 * User: Tom Byrne(kylar42@gmail.com)
 * "Code early, Code often."
 */
public class CourseImportJob implements Job{
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        DBContext context = new DBContext(NTPAppServer.getInstance().getAppProperties());
        new CourseSeeder().seedAllCourses(context);
    }


    public static void scheduleImportJob(Scheduler scheduler) throws SchedulerException{
        final AppProperties appProperties = NTPAppServer.getInstance().getAppProperties();
        String importJobName = appProperties.getProperty("import.job.name", "import");
        String importJobGroup = appProperties.getProperty("import.job.group", "importGroup");
        String importJobTriggerName = appProperties.getProperty("import.job.trigger.name", "importTrigger");

        JobDetail job = JobBuilder.newJob(CourseImportJob.class).withIdentity(importJobName, importJobName).build();
        final SimpleScheduleBuilder simpleScheduleBuilder =
                SimpleScheduleBuilder.simpleSchedule().withIntervalInMinutes(30).repeatForever();

        Trigger trigger = TriggerBuilder.newTrigger().withIdentity(importJobTriggerName, importJobGroup).startNow().withSchedule(simpleScheduleBuilder).build();

         scheduler.scheduleJob(job, trigger);
    }



    /**
     * // define the job and tie it to our HelloJob class
     JobDetail job = newJob(HelloJob.class)
     .withIdentity("job1", "group1")
     .build();

     // Trigger the job to run now, and then repeat every 40 seconds
     Trigger trigger = newTrigger()
     .withIdentity("trigger1", "group1")
     .startNow()
     .withSchedule(simpleSchedule()
     .withIntervalInSeconds(40)
     .repeatForever())
     .build();

     // Tell quartz to schedule the job using our trigger
     sched.scheduleJob(job, trigger);
     */
}
