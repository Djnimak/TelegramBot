package module13.superCoolCurrencyBot.service;

import module13.superCoolCurrencyBot.dto.CurrencyRateDto;

import java.text.MessageFormat;
import java.util.Optional;

public class PrettyRateResponseService {

    private static final String RATE_RESPONSE = "Курс в {0}: {1}/UAH\n Купівля: {2}\n Продаж: {3}";

    public static String formRateResponse(Optional<CurrencyRateDto> rates)
    {
        String response;
        if (rates.isPresent())
        {
            CurrencyRateDto rate = rates.get();
            response = MessageFormat.format(RATE_RESPONSE, rate.getBankName(), rate.getCurrency(),
               rate.getBuyRate(), rate.getSellRate());
        }
        else
        {
            response = "В даний момент інформація не доступна. Спробуйте пізніше";
        }
        return response;
    }
}
