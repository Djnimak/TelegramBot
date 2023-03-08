package module13.superCoolCurrencyBot;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import module13.superCoolCurrencyBot.dto.SettingsDto;
import module13.superCoolCurrencyBot.service.SendNotificationService;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class SuperCoolBotTest
{
   public static final String SETTINGS_FILE = "src/main/resources/settings.json";

   public static List<SettingsDto> SETTINGS_LIST = downloadSettings();

   public static void main(String[] args)
   {
      CurrencyBot currencyBot = new CurrencyBot();
      Timer timer = new Timer(SETTINGS_LIST);
      Thread messageSender = new Thread(timer);
      messageSender.start();

      try
      {
         TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
         botsApi.registerBot(currencyBot);
      }
      catch (TelegramApiException e)
      {
         e.printStackTrace();
      }

      uploadSettings();

//      try
//      {
//         for (int i = 1; i < SETTINGS_LIST.size(); i++)
//         {
//            JobDetail jobSendNotification = JobBuilder.newJob(SendNotificationService.class)
//               .withIdentity("sendNotification")
//               .build();
//
//            Trigger trigger = TriggerBuilder
//               .newTrigger()
//               .withIdentity("atSpecificTime")
//               .withSchedule(CronScheduleBuilder.cronSchedule("*/1 * * * *"))
//               .build();
//
//            Scheduler scheduler = new StdSchedulerFactory().getScheduler();
//            scheduler.getContext().put("bot", currencyBot);
//            scheduler.start();
//            scheduler.scheduleJob(jobSendNotification, trigger);
//         }
//      }
//      catch (SchedulerException e)
//      {
//         e.printStackTrace();
//      }
   }

   private static void uploadSettings()
   {
      try
      {
         FileWriter fileWriter = new FileWriter(SETTINGS_FILE);
         new Gson().toJson(SETTINGS_LIST, fileWriter);
         if (Objects.isNull(SETTINGS_LIST))
         {
            SETTINGS_LIST = new LinkedList<>();
         }
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }

   private static List<SettingsDto> downloadSettings()
   {
      List<SettingsDto> settingsList = new LinkedList<>();
      try
      {
         Reader reader = new FileReader(SETTINGS_FILE);
         Type type = TypeToken.getParameterized(List.class, SettingsDto.class).getType();
         settingsList = new Gson().fromJson(reader, type);
      }
      catch (FileNotFoundException e)
      {
         e.printStackTrace();
      }
      if (Objects.isNull(settingsList))
      {
         settingsList = new LinkedList<>();
      }
      return settingsList;
   }
}
