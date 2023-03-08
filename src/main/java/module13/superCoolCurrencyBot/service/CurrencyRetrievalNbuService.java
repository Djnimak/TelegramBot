package module13.superCoolCurrencyBot.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import module13.superCoolCurrencyBot.dto.CurrencyRateDto;
import module13.superCoolCurrencyBot.dto.CurrencyRateNbuResponseDto;
import module13.superCoolCurrencyBot.enums.BankName;
import module13.superCoolCurrencyBot.enums.Currency;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

public class CurrencyRetrievalNbuService implements CurrencyRetrievalService
{
   private static final String URL = "https://bank.gov.ua/NBUStatService/v1/statdirectory/exchange?json";

   @Override
   public List<CurrencyRateDto> getCurrencyRates()
   {
      try
      {
         String response = Jsoup.connect(URL).ignoreContentType(Boolean.TRUE).get().body().text();
         List<CurrencyRateNbuResponseDto> responseDtoList = convertResponseToList(response);
         return responseDtoList.stream()
            .filter(rate -> rate.getCc() == Currency.EUR || rate.getCc() == Currency.USD)
            .map(item -> new CurrencyRateDto(item.getCc(), item.getRate(), item.getRate(), BankName.NBU))
            .collect(Collectors.toList());
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }

   private List<CurrencyRateNbuResponseDto> convertResponseToList(String response)
   {
      Type type = TypeToken.getParameterized(List.class, CurrencyRateNbuResponseDto.class).getType();
      return new Gson().fromJson(response, type);
   }
}
