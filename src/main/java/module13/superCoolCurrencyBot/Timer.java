package module13.superCoolCurrencyBot;

import lombok.AllArgsConstructor;
import module13.superCoolCurrencyBot.dto.SettingsDto;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

public class Timer implements Runnable
{
   private List<SettingsDto> settingsDto;

   public Timer(List<SettingsDto> settingsDto)
   {
      this.settingsDto = settingsDto;
   }

   @Override
   public void run()
   {
      while (true)
      {
         try
         {
            timer();
            Thread.sleep(1500);
         }
         catch (InterruptedException | TelegramApiException e)
         {
            throw new RuntimeException(e);
         }
      }
   }

   public void timer() throws InterruptedException, TelegramApiException
   {
      ZonedDateTime greenwich = ZonedDateTime.now().withZoneSameInstant(ZoneOffset.UTC);
      LocalDateTime startTime = LocalDateTime.from(greenwich);
      LocalDateTime startDays = LocalDateTime.from(greenwich).withHour(0).withMinute(0).withSecond(0);
      LocalDateTime timeToSendMessage = LocalDateTime.from(greenwich).withMinute(0).withSecond(0);
      if (timeToSendMessage.isBefore(startTime))
      {
         timeToSendMessage = timeToSendMessage.plusHours(1);
      }
      Duration timeToSendMesg = Duration.between(startTime, timeToSendMessage);
      Thread.sleep(timeToSendMesg.toMillis());
      Duration hour = Duration.between(startDays, timeToSendMessage);
      for (SettingsDto setting : settingsDto)
      {
         long chatId = setting.getChatId();
         int notificationTime;
         if (!setting.getNotificationTime().equalsIgnoreCase("off"))
         {
            notificationTime = Integer.parseInt(setting.getNotificationTime());
            if (notificationTime == (int) hour.toHours())
            {
               CurrencyBot currencyBot = new CurrencyBot();
               currencyBot.sendNotifications(chatId, settingsDto.stream().filter(set -> set.getChatId() == chatId)
                  .findFirst().get());
            }
         }

      }
   }
}
