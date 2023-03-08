package module13.superCoolCurrencyBot;

import com.google.gson.Gson;
import module13.superCoolCurrencyBot.dto.SettingsDto;
import module13.superCoolCurrencyBot.service.*;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static module13.superCoolCurrencyBot.SuperCoolBotTest.SETTINGS_FILE;
import static module13.superCoolCurrencyBot.SuperCoolBotTest.SETTINGS_LIST;

public class CurrencyBot extends TelegramLongPollingBot
{
   private final CommandResolverService commandResolverService = new CommandResolverService();

   @Override
   public String getBotUsername()
   {
      return "NikitasFirstTestTelegramBot";
   }

   @Override
   public String getBotToken()
   {
      return "6066640637:AAHbM6bQ5O9G9XvidtbbXKkHtyY2HHxLaZk";
   }

   @Override
   public void onUpdateReceived(Update update)
   {
//      uploadSettings();
      if (update.hasMessage() && update.getMessage().hasText())
      {
         String command = update.getMessage().getText();
         long chatId = update.getMessage().getChatId();
         System.out.println("User with id: " + chatId + " started the chat");
         SendMessage message = commandResolverService.resolveMessageCommand(command, chatId);
         try
         {
            execute(message);
         }
         catch (TelegramApiException e)
         {
            e.printStackTrace();
         }

      }
      else if (update.hasCallbackQuery())
      {
         String command = update.getCallbackQuery().getData();
         long messageId = update.getCallbackQuery().getMessage().getMessageId();
         long chatId = update.getCallbackQuery().getMessage().getChatId();
         if (command.contains("set_"))
         {
            EditMessageText message =
               commandResolverService.resolveSettingsCommand(command, chatId, messageId);
            try
            {
               execute(message);
            }
            catch (TelegramApiException e)
            {
               e.printStackTrace();
            }
         }
         else
         {
            SendMessage message = commandResolverService.resolveMessageCommand(command, chatId);
            try
            {
               execute(message);
            }
            catch (TelegramApiException e)
            {
               e.printStackTrace();
            }
         }
//         showPermanentMenuButton(chatId);
      }

   }

   private static void uploadSettings()
   {
      try
      {
         FileWriter fileWriter = new FileWriter(SETTINGS_FILE);
         new Gson().toJson(SETTINGS_LIST, fileWriter);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }

   public void sendNotifications(long chatId, SettingsDto userSettings)
   {
         SendMessage message = SendMessage.builder()
            .chatId(chatId)
            .text(commandResolverService.getRatesBasedOnSettings(userSettings))
            .build();
         try
         {
            execute(message);
         }
         catch (TelegramApiException e)
         {
            e.printStackTrace();
      }
   }

   private void showPermanentMenuButton(long chatId)
   {
      ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
      List<KeyboardRow> keyboardButtons = new ArrayList<>();
      KeyboardRow menuButton = new KeyboardRow();
      menuButton.add("Menu");
      keyboardButtons.add(menuButton);
      keyboardMarkup.setKeyboard(keyboardButtons);
      keyboardMarkup.setIsPersistent(true);
      keyboardMarkup.setResizeKeyboard(true);

      SendMessage message = SendMessage.builder()
         .text(" ")
         .chatId(chatId)
         .replyMarkup(keyboardMarkup)
         .build();
      try
      {
         execute(message);
      }
      catch (TelegramApiException e)
      {
         e.printStackTrace();
      }

   }
}

