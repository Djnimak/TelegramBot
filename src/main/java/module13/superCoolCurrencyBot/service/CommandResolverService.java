package module13.superCoolCurrencyBot.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.vdurmont.emoji.EmojiParser;
import module13.superCoolCurrencyBot.dto.SettingsDto;
import module13.superCoolCurrencyBot.enums.BankName;
import module13.superCoolCurrencyBot.enums.Currency;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static java.lang.Math.toIntExact;
import static module13.superCoolCurrencyBot.SuperCoolBotTest.SETTINGS_FILE;
import static module13.superCoolCurrencyBot.SuperCoolBotTest.SETTINGS_LIST;

public class CommandResolverService
{

   private static final String DECIMAL_COUNT_CALLBACK = "decimalCount";

   private static final String DECIMAL_COUNT_TEXT = "Кількість знаків після коми";

   private static final String BANK_TEXT = "Банк";

   private static final String CURRENCY_TEXT = "Валюта";

   private static final String NOTIFICATION_TEXT = "Час сповіщень";

   private static final String BANK_CALLBACK = "bankName";

   private static final String CURRENCY_CALLBACK = "currency";

   private static final String NOTIFICATION_CALLBACK = "notificationTime";

   private static final String DECIMAL_COUNT_MAIN_TEXT = "Оберіть кількість знаків після коми";

   private static final String DECIMAL_TWO_CALLBACK = "set_dec_decimalTwo";

   private static final String DECIMAL_THREE_CALLBACK = "set_dec_decimalThree";

   private static final String DECIMAL_FOUR_CALLBACK = "set_dec_decimalFour";

   private static final String DECIMAL_OPTION_THREE = "3";

   private static final String DECIMAL_OPTION_FOUR = "4";

   private static final String DECIMAL_OPTION_TWO = "2";

   private static final String CHECK_MARK_EMOJI = ":white_check_mark:";

   private static final String BANK_MENU_TEXT = "Оберіть банк за замовчуванням";

   private static final String PRIVAT_CALLBACK = "set_bank_privatCallback";

   private static final String MONO_CALLBACK = "set_bank_monoCallback";

   private static final String NBU_CALLBACK = "set_bank_nbuCallback";

   private static final String CURRENCY_MENU_TEXT = "Оберіть валюту за замовчуванням";

   private static final String USD_CALLBACK = "set_cur_usdCallback";

   private static final String EUR_CALLBACK = "set_cur_eurCallback";

   private static final String NOTIFICATION_MENU_TEXT = "Оберіть час отримання сповіщень";

   private static final String MAIN_MENU = "Головне меню";

   private static final String START = "/start";

   private static final String BACK_TO_MAIN_MANU = "Повернення до головного меню";

   private static final String OPTION_SAVED = "Ваша опція збережена";

//   public static final List<SettingsDto> SETTINGS_LIST = downloadSettings();

//   private static final String SETTINGS_FILE = "src/main/resources/settings.json";

   private static final String WELCOME_TEXT =
      "Ласкаво просимо. Цей бот допоможе відслідковувати актуальні курси валют";

   private static final String SETTINGS_TEXT = "Налаштування";

   private static final String SETTINGS_MENU_CALLBACK = "showSettings";

   private static final String INFO_TEXT = "Отримати інфо";

   private static final String INFO_CALLBACK = "getRateInfo";

   private static final List<String> notificationOptions = getNotificationsList();

   private static final RateOperationsService rateOperationsService = new RateOperationsService();

   public SendMessage resolveMessageCommand(String command, long chatId)
   {
      SettingsDto userSettings = getUsersSettings(chatId);
      saveSettings(userSettings);
      uploadSettings();
//      SETTINGS_LIST.add(userSettings);

      if (notificationOptions.contains(command))
      {
         userSettings.setNotificationTime(command);
         return SendMessage.builder()
            .chatId(chatId)
            .text(OPTION_SAVED)
            .replyMarkup(getStartMenu())
            .build();
      }

      switch (command)
      {
         case START:
         {
            return SendMessage.builder()
               .chatId(chatId)
               .text(WELCOME_TEXT)
               .replyMarkup(getStartMenu())
               .build();
         }
         case INFO_CALLBACK:
         {
            return SendMessage.builder()
               .chatId(chatId)
               .text(getRatesBasedOnSettings(userSettings))
               .replyMarkup(getStartMenu())
               .build();
         }
         case SETTINGS_MENU_CALLBACK:
         {
            return SendMessage.builder()
               .chatId(chatId)
               .text(SETTINGS_TEXT)
               .replyMarkup(getSettingsMenu())
               .build();
         }
         case DECIMAL_COUNT_CALLBACK:
         {
            return SendMessage.builder()
               .chatId(chatId)
               .text(DECIMAL_COUNT_MAIN_TEXT)
               .replyMarkup(getDecimalMenu(userSettings.getDecimalCount()))
               .build();
         }
         case BANK_CALLBACK:
         {
            return SendMessage.builder()
               .chatId(chatId)
               .text(BANK_MENU_TEXT)
               .replyMarkup(getBankMenu(userSettings.getBank()))
               .build();
         }
         case CURRENCY_CALLBACK:
         {
            return SendMessage.builder()
               .chatId(chatId)
               .text(CURRENCY_MENU_TEXT)
               .replyMarkup(getCurrencyMenu(userSettings.getCurrency()))
               .build();
         }
         case NOTIFICATION_CALLBACK:
         {
            return SendMessage.builder()
               .chatId(chatId)
               .text(NOTIFICATION_MENU_TEXT)
               .replyMarkup(getNotificationMenu())
               .build();
         }
         default:
         {
            return SendMessage.builder()
               .chatId(chatId)
               .text(BACK_TO_MAIN_MANU)
               .replyMarkup(getReturnButton())
               .build();
         }
      }
   }

   private void saveSettings(SettingsDto userSettings)
   {
      if (SETTINGS_LIST.stream().noneMatch(set -> set.getChatId() == userSettings.getChatId()))
      {
         SETTINGS_LIST.add(userSettings);
      }
   }

   private synchronized static void uploadSettings()
   {
      Path path = Paths.get(SETTINGS_FILE);

      try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8))
      {
         Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

         gson.toJson(SETTINGS_LIST, writer);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
//      try
//      {
//         FileWriter fileWriter = new FileWriter(SETTINGS_FILE);
//         new Gson().toJson(SETTINGS_LIST, fileWriter);
//      }
//      catch (IOException e)
//      {
//         e.printStackTrace();
//      }
   }

   public EditMessageText resolveSettingsCommand(String command, long chatId, long messageId)
   {
      SettingsDto userSettings = getUsersSettings(chatId);
      EditMessageText message;
      if (command.contains("_dec_"))
      {
         message = resolveDecimalCommands(userSettings, command, chatId, messageId);
      }
      else if (command.contains("_bank_"))
      {
         message = resolveBankCommands(userSettings, command, chatId, messageId);
      }
      else
      {
         message = resolveCurrencyCommand(userSettings, command, chatId, messageId);
      }
      return message;
   }

   public String getRatesBasedOnSettings(SettingsDto userSettings)
   {
      return PrettyRateResponseService.formRateResponse(
         rateOperationsService.getRatesBasedOnSettings(userSettings));
   }

   private EditMessageText resolveDecimalCommands(SettingsDto userSettings, String command,
      long chatId, long messageId)
   {
      InlineKeyboardMarkup keyboardMarkup;
      if (command.equalsIgnoreCase(DECIMAL_TWO_CALLBACK))
      {
         userSettings.setDecimalCount(2);
         keyboardMarkup = getDecimalMenu(2);
      }
      else if (command.equalsIgnoreCase(DECIMAL_THREE_CALLBACK))
      {
         userSettings.setDecimalCount(3);
         keyboardMarkup = getDecimalMenu(3);
      }
      else
      {
         userSettings.setDecimalCount(4);
         keyboardMarkup = getDecimalMenu(4);
      }
      return EditMessageText.builder()
         .text(OPTION_SAVED)
         .chatId(chatId)
         .messageId(toIntExact(messageId))
         .replyMarkup(keyboardMarkup)
         .build();
   }

   private EditMessageText resolveBankCommands(SettingsDto userSettings, String command,
      long chatId, long messageId)
   {
      InlineKeyboardMarkup keyboardMarkup;
      if (command.equalsIgnoreCase(PRIVAT_CALLBACK))
      {
         userSettings.setBank(BankName.PRIVATBANK);
         keyboardMarkup = getBankMenu(BankName.PRIVATBANK);
      }
      else if (command.equalsIgnoreCase(MONO_CALLBACK))
      {
         userSettings.setBank(BankName.MONOBANK);
         keyboardMarkup = getBankMenu(BankName.MONOBANK);
      }
      else
      {
         userSettings.setBank(BankName.NBU);
         keyboardMarkup = getBankMenu(BankName.NBU);
      }
      return EditMessageText.builder()
         .text(OPTION_SAVED)
         .chatId(chatId)
         .messageId(toIntExact(messageId))
         .replyMarkup(keyboardMarkup)
         .build();
   }

   private EditMessageText resolveCurrencyCommand(SettingsDto userSettings, String command,
      long chatId, long messageId)
   {
      InlineKeyboardMarkup keyboardMarkup;
      if (command.equalsIgnoreCase(USD_CALLBACK))
      {
         userSettings.setCurrency(Currency.USD);
         keyboardMarkup = getCurrencyMenu(Currency.USD);
      }
      else
      {
         userSettings.setCurrency(Currency.EUR);
         keyboardMarkup = getCurrencyMenu(Currency.EUR);
      }
      return EditMessageText.builder()
         .text(OPTION_SAVED)
         .chatId(chatId)
         .messageId(toIntExact(messageId))
         .replyMarkup(keyboardMarkup)
         .build();
   }

   private InlineKeyboardMarkup getReturnButton()
   {
      InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
      List<List<InlineKeyboardButton>> rowsInline = new LinkedList<>();
      rowsInline.add(createButton(MAIN_MENU, START));
      markup.setKeyboard(rowsInline);
      return markup;

   }

   private ReplyKeyboardMarkup getNotificationMenu()
   {
      ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
      List<KeyboardRow> keyboardButtons = new ArrayList<>();
      KeyboardRow menuButton = new KeyboardRow();
      menuButton.add("9");
      menuButton.add("10");
      menuButton.add("11");
      keyboardButtons.add(menuButton);

      menuButton = new KeyboardRow();
      menuButton.add("12");
      menuButton.add("13");
      menuButton.add("14");
      keyboardButtons.add(menuButton);

      menuButton = new KeyboardRow();
      menuButton.add("15");
      menuButton.add("16");
      menuButton.add("17");
      keyboardButtons.add(menuButton);

      menuButton = new KeyboardRow();
      menuButton.add("18");
      menuButton.add("Вимкунти сповіщення");
      keyboardButtons.add(menuButton);

      keyboardMarkup.setKeyboard(keyboardButtons);
      keyboardMarkup.setOneTimeKeyboard(true);
      keyboardMarkup.setResizeKeyboard(true);

      return keyboardMarkup;
   }

   private InlineKeyboardMarkup getCurrencyMenu(Currency chosenCurrency)
   {
      InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
      List<List<InlineKeyboardButton>> rowsInline = new LinkedList<>();
      String markedOption = EmojiParser.parseToUnicode(CHECK_MARK_EMOJI + chosenCurrency);
      if (chosenCurrency == Currency.USD)
      {
         rowsInline.add(createButton(markedOption, USD_CALLBACK));
         rowsInline.add(createButton(Currency.EUR.name(), EUR_CALLBACK));
      }
      else
      {
         rowsInline.add(createButton(Currency.USD.name(), USD_CALLBACK));
         rowsInline.add(createButton(markedOption, EUR_CALLBACK));
      }
      markup.setKeyboard(rowsInline);
      return markup;
   }

   private InlineKeyboardMarkup getBankMenu(BankName chosenBank)
   {
      InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
      List<List<InlineKeyboardButton>> rowsInline = new LinkedList<>();
      String markedOption = EmojiParser.parseToUnicode(CHECK_MARK_EMOJI + chosenBank);
      if (chosenBank == BankName.PRIVATBANK)
      {
         rowsInline.add(createButton(markedOption, PRIVAT_CALLBACK));
         rowsInline.add(createButton(BankName.MONOBANK.name(), MONO_CALLBACK));
         rowsInline.add(createButton(BankName.NBU.name(), NBU_CALLBACK));
      }
      else if (chosenBank == BankName.MONOBANK)
      {
         rowsInline.add(createButton(BankName.PRIVATBANK.name(), PRIVAT_CALLBACK));
         rowsInline.add(createButton(markedOption, MONO_CALLBACK));
         rowsInline.add(createButton(BankName.NBU.name(), NBU_CALLBACK));
      }
      else
      {
         rowsInline.add(createButton(BankName.PRIVATBANK.name(), PRIVAT_CALLBACK));
         rowsInline.add(createButton(BankName.MONOBANK.name(), MONO_CALLBACK));
         rowsInline.add(createButton(markedOption, NBU_CALLBACK));
      }
      markup.setKeyboard(rowsInline);
      return markup;
   }

   private InlineKeyboardMarkup getDecimalMenu(long chosenOption)
   {
      InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
      List<List<InlineKeyboardButton>> rowsInline = new LinkedList<>();
      String markedOption = EmojiParser.parseToUnicode(CHECK_MARK_EMOJI + chosenOption);

      if (chosenOption == 2)
      {
         rowsInline.add(createButton(markedOption, DECIMAL_TWO_CALLBACK));
         rowsInline.add(createButton(DECIMAL_OPTION_THREE, DECIMAL_THREE_CALLBACK));
         rowsInline.add(createButton(DECIMAL_OPTION_FOUR, DECIMAL_FOUR_CALLBACK));
      }
      else if (chosenOption == 3)
      {
         rowsInline.add(createButton(DECIMAL_OPTION_TWO, DECIMAL_TWO_CALLBACK));
         rowsInline.add(createButton(markedOption, DECIMAL_THREE_CALLBACK));
         rowsInline.add(createButton(DECIMAL_OPTION_FOUR, DECIMAL_FOUR_CALLBACK));
      }
      else
      {
         rowsInline.add(createButton(DECIMAL_OPTION_TWO, DECIMAL_TWO_CALLBACK));
         rowsInline.add(createButton(DECIMAL_OPTION_THREE, DECIMAL_THREE_CALLBACK));
         rowsInline.add(createButton(markedOption, DECIMAL_FOUR_CALLBACK));
      }
      markup.setKeyboard(rowsInline);
      return markup;
   }

   private SettingsDto getUsersSettings(long chatId)
   {
      return SETTINGS_LIST.stream()
         .filter(set -> set.getChatId() == chatId)
         .findFirst()
         .orElse(new SettingsDto(chatId));
   }

   private InlineKeyboardMarkup getStartMenu()
   {
      InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
      List<List<InlineKeyboardButton>> rowsInline = new LinkedList<>();
      rowsInline.add(createButton(INFO_TEXT, INFO_CALLBACK));
      rowsInline.add(createButton(SETTINGS_TEXT, SETTINGS_MENU_CALLBACK));
      markup.setKeyboard(rowsInline);
      return markup;
   }

   private List<InlineKeyboardButton> createButton(String text, String callbackData)
   {
      List<InlineKeyboardButton> buttons = new ArrayList<>();
      InlineKeyboardButton button = InlineKeyboardButton.builder()
         .text(text)
         .callbackData(callbackData)
         .build();
      buttons.add(button);
      return buttons;
   }

   private InlineKeyboardMarkup getSettingsMenu()
   {
      InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
      List<List<InlineKeyboardButton>> rowsInline = new LinkedList<>();
      rowsInline.add(createButton(DECIMAL_COUNT_TEXT, DECIMAL_COUNT_CALLBACK));
      rowsInline.add(createButton(BANK_TEXT, BANK_CALLBACK));
      rowsInline.add(createButton(CURRENCY_TEXT, CURRENCY_CALLBACK));
      rowsInline.add(createButton(NOTIFICATION_TEXT, NOTIFICATION_CALLBACK));
      markup.setKeyboard(rowsInline);
      return markup;
   }

   private static List<String> getNotificationsList()
   {
      return new LinkedList<>(
         Arrays.asList("9", "10", "11", "12", "13", "14", "15", "16", "17", "18",
            "Вимкунти сповіщення"));
   }
}
