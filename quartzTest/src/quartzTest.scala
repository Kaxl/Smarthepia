import java.util.Date

import org.quartz.JobBuilder.newJob
import org.quartz.SimpleScheduleBuilder.simpleSchedule
import org.quartz.TriggerBuilder.newTrigger
import org.quartz.impl.StdSchedulerFactory
import org.quartz.Job
import org.quartz.JobExecutionContext

object Main extends App {

  val scheduler = StdSchedulerFactory.getDefaultScheduler();

  println("Quarz scheduler starting...")

  scheduler.start();

  // define the job and tie it to our HelloJob class
  val job = newJob(classOf[MyWorker]).withIdentity("job1", "group1").build();

  // Trigger the job to run now, and then repeat every 10 seconds
  val trigger = newTrigger()
    .withIdentity("trigger1", "group1")
    .startNow()
    .withSchedule(simpleSchedule()
      .withIntervalInSeconds(10)
      .repeatForever())
    .build();

  // Tell quartz to schedule the job using our trigger
  scheduler.scheduleJob(job, trigger);

  println("Quartz scheduler shutdown.")

}

class MyWorker extends Job {
  def execute(ctxt: JobExecutionContext) {
    println("Scheduled Job triggered at: " + new Date)
  }
}

