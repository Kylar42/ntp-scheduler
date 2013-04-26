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
 * Scheduled job for importing the Courses. Should be triggered on a regular basis
 * in order to keep our local data in sync.
 */
public class CourseImportJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        DBContext context = new DBContext(NTPAppServer.getInstance().getAppProperties());
        new CourseSeeder().seedAllCourses(context);
    }

    /**
     * Create the job and add it to the scheduler.
     * @param scheduler
     * @throws SchedulerException
     */
    public static void scheduleImportJob(Scheduler scheduler) throws SchedulerException {
        final AppProperties appProperties = NTPAppServer.getInstance().getAppProperties();
        String importJobName = appProperties.getProperty("import.job.name", "import");
        String importJobGroup = appProperties.getProperty("import.job.group", "importGroup");
        String importJobTriggerName = appProperties.getProperty("import.job.trigger.name", "importTrigger");
        int intervalInMinutes = appProperties.getPropertyAsInt("import.job.interval.inminutes", 15);

        JobDetail job = JobBuilder.newJob(CourseImportJob.class).withIdentity(importJobName, importJobName).build();
        final SimpleScheduleBuilder simpleScheduleBuilder =
                SimpleScheduleBuilder.simpleSchedule().withIntervalInMinutes(intervalInMinutes).repeatForever();

        Trigger trigger = TriggerBuilder.newTrigger().withIdentity(importJobTriggerName, importJobGroup).startNow().withSchedule(simpleScheduleBuilder).build();

        scheduler.scheduleJob(job, trigger);
    }


}
