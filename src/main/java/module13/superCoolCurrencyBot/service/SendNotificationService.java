package module13.superCoolCurrencyBot.service;

import module13.superCoolCurrencyBot.CurrencyBot;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerContext;
import org.quartz.SchedulerException;

public class SendNotificationService implements Job
{
   @Override
   public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException
   {
      SchedulerContext schedulerContext;
      try {
         schedulerContext = jobExecutionContext.getScheduler().getContext();
         CurrencyBot currencyBot = (CurrencyBot) schedulerContext.get("bot");
//         currencyBot.sendNotifications();
      } catch (SchedulerException e) {
         e.printStackTrace();
      }
   }
}
