package module13.superCoolCurrencyBot.dto;

import lombok.Getter;
import lombok.Setter;
import module13.superCoolCurrencyBot.enums.BankName;
import module13.superCoolCurrencyBot.enums.Currency;

@Getter
@Setter
public class SettingsDto
{
   private long chatId;

   private int decimalCount;

   private BankName bank;

   private Currency currency;

   private String notificationTime;

   public SettingsDto (long chatId)
   {
      this.chatId = chatId;
      decimalCount = 2;
      bank = BankName.PRIVATBANK;
      currency = Currency.USD;
      notificationTime = "OFF";
   }
}
