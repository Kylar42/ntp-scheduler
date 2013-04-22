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
public class CourseImportJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        DBContext context = new DBContext(NTPAppServer.getInstance().getAppProperties());
        new CourseSeeder().seedAllCourses(context);
    }


    public static void scheduleImportJob(Scheduler scheduler) throws SchedulerException {
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


}
